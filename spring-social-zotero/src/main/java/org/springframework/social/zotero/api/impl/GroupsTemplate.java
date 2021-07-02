package org.springframework.social.zotero.api.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.social.zotero.api.Creator;
import org.springframework.social.zotero.api.Data;
import org.springframework.social.zotero.api.DeletedElements;
import org.springframework.social.zotero.api.Group;
import org.springframework.social.zotero.api.GroupsOperations;
import org.springframework.social.zotero.api.Item;
import org.springframework.social.zotero.api.ItemCreationResponse;
import org.springframework.social.zotero.api.ItemCreationResponse.FailedMessage;
import org.springframework.social.zotero.api.ZoteroFields;
import org.springframework.social.zotero.api.ZoteroRequestHeaders;
import org.springframework.social.zotero.api.ZoteroResponse;
import org.springframework.social.zotero.api.ZoteroUpdateItemsStatuses;
import org.springframework.social.zotero.exception.ZoteroConnectionException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

public class GroupsTemplate extends AbstractZoteroOperations implements GroupsOperations {
    
    private static final int ZOTERO_BATCH_UPDATE_LIMIT = 50;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final RestTemplate restTemplate;

    public GroupsTemplate(RestTemplate restTemplate, boolean isAuthorizedForUser, String providerUrl, String userId) {
        super(isAuthorizedForUser, providerUrl);
        setUserId(userId);
        this.restTemplate = restTemplate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.social.zotero.api.impl.GroupsOperations#getGroupItems(
     * java.lang.String)
     */
    @Override
    public Item[] getGroupItems(String groupId, int start, int numberOfItems, String sortBy) {
        return restTemplate.getForObject(buildGroupUri("items", groupId, start, numberOfItems, sortBy), Item[].class);
    }
    
    @Override
    public ZoteroResponse<Item> getGroupItemsByKey(String groupId, List<String> keys, boolean includeTrashed) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("itemKey", String.join(",", keys));
        if (includeTrashed) {
            queryParams.put("includeTrashed", "1");
        }
        
        ZoteroResponse<Item> zoteroResponse = new ZoteroResponse<>();
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<Item[]> response = restTemplate.exchange(
                buildGroupUri("items", groupId, queryParams), HttpMethod.GET,
                new HttpEntity<String>(headers), new ParameterizedTypeReference<Item[]>() {
                });
        zoteroResponse.setResults(response.getBody());
        zoteroResponse.setLastVersion(getLatestVersion(response.getHeaders()));
        
        return zoteroResponse;
    }
    
    @Override
    public ZoteroResponse<Item> getGroupItemsVersions(String groupId, long version, boolean includeTrashed) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("since", version + "");
        queryParams.put("format", "versions");
        if (includeTrashed) {
            queryParams.put("includeTrashed", "1");
        }
        
        List<Item> items = new ArrayList<>();
        
        ResponseEntity<String> response = restTemplate.exchange(
                buildGroupUri("items", groupId, queryParams), HttpMethod.GET,
                new HttpEntity<String>(new HttpHeaders()), new ParameterizedTypeReference<String>() {
                });
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode node = mapper.readTree(response.getBody());
            Iterator<String> keys = node.fieldNames();
            while (keys.hasNext()) {
                String key = keys.next();
                Item item = new Item();
                item.setKey(key);
                item.setVersion(node.get(key).asLong());
                items.add(item);
            }
        } catch (IOException e) {
            logger.error("Could not read JSON.", e);
        }
        
        ZoteroResponse<Item> zoteroResponse = new ZoteroResponse<>();
        zoteroResponse.setResults(items.toArray(new Item[items.size()]));
        return zoteroResponse;
    }

    @Override
    public ZoteroResponse<Item> getGroupItemsTop(String groupId, int start, int numberOfItems, String sortBy,
            Long groupVersion) {
        ZoteroResponse<Item> zoteroResponse = new ZoteroResponse<>();
        HttpHeaders headers = new HttpHeaders();
        if (groupVersion != null) {
            headers.add(ZoteroRequestHeaders.HEADER_IF_MODIFIED_SINCE_VERSION, groupVersion.toString());
        }
        ResponseEntity<Item[]> response = restTemplate.exchange(
                buildGroupUri("items/top", groupId, start, numberOfItems, sortBy), HttpMethod.GET,
                new HttpEntity<String>(headers), new ParameterizedTypeReference<Item[]>() {
                });
        zoteroResponse.setResults(response.getBody());
        if (response.getStatusCode() == HttpStatus.NOT_MODIFIED) {
            zoteroResponse.setNotModified(true);
        }  
        HttpHeaders responseHeaders = response.getHeaders();
        long latestVersion = getLatestVersion(responseHeaders);
        zoteroResponse.setLastVersion(latestVersion > -1 ? latestVersion : groupVersion);
        
        List<String> totalResultsHeader = response.getHeaders().get(ZoteroRequestHeaders.HEADER_TOTAL_RESULTS);
        if (totalResultsHeader != null && totalResultsHeader.size() > 0) {
            zoteroResponse.setTotalResults(new Long(totalResultsHeader.get(0)));
        }
        return zoteroResponse;
    }
    
    private long getLatestVersion(HttpHeaders responseHeaders) {
        if (responseHeaders.get(ZoteroRequestHeaders.HEADER_LAST_MODIFIED_VERSION) != null) {
            List<String> versions = responseHeaders.get(ZoteroRequestHeaders.HEADER_LAST_MODIFIED_VERSION);
            if (versions.size() > 0) {
                // there should be just one
                return new Long(versions.get(0));
            }
        }
        return -1;
    }

    @Override
    public ZoteroResponse<Group> getGroups() {
        ZoteroResponse<Group> zoteroResponse = new ZoteroResponse<>();
        ResponseEntity<Group[]> response = restTemplate.exchange(buildUri("users/" + getUserId() + "/groups", false),
                HttpMethod.GET, new HttpEntity<String>(new HttpHeaders()), new ParameterizedTypeReference<Group[]>() {
                });
        zoteroResponse.setResults(response.getBody());
        List<String> totalResultsHeader = response.getHeaders().get(ZoteroRequestHeaders.HEADER_TOTAL_RESULTS);
        if (totalResultsHeader != null && totalResultsHeader.size() > 0) {
            zoteroResponse.setTotalResults(new Long(totalResultsHeader.get(0)));
        }
        return zoteroResponse;
    }

    @Override
    public ZoteroResponse<Group> getGroupsVersions() {
        ZoteroResponse<Group> zoteroResponse = new ZoteroResponse<>();
        ResponseEntity<String> response = restTemplate.exchange(
                buildUri("users/" + getUserId() + "/groups?format=versions", false), HttpMethod.GET,
                new HttpEntity<String>(new HttpHeaders()), new ParameterizedTypeReference<String>() {
                });
        List<Group> groups = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode node = mapper.readTree(response.getBody());
            Iterator<String> fieldNames = node.fieldNames();
            fieldNames.forEachRemaining(f -> {
                Group group = new Group();
                group.setId(new Long(f));
                group.setVersion(node.findValue(f).asLong());
                groups.add(group);
            });
        } catch (IOException e) {
            logger.error("Could not read JSON.", e);
        }
        zoteroResponse.setResults(groups.toArray(new Group[groups.size()]));
        return zoteroResponse;
    }

    @Override
    public Group getGroup(String groupId) {
        String url = String.format("groups/%s", groupId);
        return restTemplate.getForObject(buildUri(url, false), Group.class);
    }

    @Override
    public Item getGroupItem(String groupId, String itemKey) {
        String url = String.format("groups/%s/%s/%s", groupId, "items", itemKey);
        return restTemplate.getForObject(buildUri(url, false), Item.class);
    }
    
    @Override
    public List<Item> getGroupItemChildren(String groupId, String itemKey) {
        String url = String.format("groups/%s/%s/%s/children", groupId, "items", itemKey);
        return restTemplate.exchange(buildUri(url, "format", "json", false), HttpMethod.GET, null, new ParameterizedTypeReference<List<Item>>() {}).getBody();
    }
    
    @Override
    public DeletedElements getDeletedElements(String groupId, long version) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("since", version + "");
        return restTemplate.getForObject(buildGroupUri("deleted", groupId, queryParams), DeletedElements.class);  
    }

    @Override
    public Long getGroupItemVersion(String groupId, String itemKey) {
        ResponseEntity<String> response = restTemplate.exchange(
                buildUri("groups/" + groupId + "/items?format=versions&itemKey=" + itemKey, false), HttpMethod.GET,
                new HttpEntity<String>(new HttpHeaders()), new ParameterizedTypeReference<String>() {
                });
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode node = mapper.readTree(response.getBody());
            JsonNode version = node.get(itemKey);
            return version.asLong();
        } catch (IOException e) {
            logger.error("Could not read JSON.", e);
        }
        return null;
    }

    @Override
    public void updateItem(String groupId, Item item, List<String> ignoreFields, List<String> validCreatorTypes)
            throws ZoteroConnectionException {
        String url = String.format("groups/%s/%s/%s", groupId, "items", item.getKey());
        HttpHeaders headers = new HttpHeaders();
        headers.set("If-Unmodified-Since-Version", item.getData().getVersion() + "");
        JsonNode dataAsJson = createDataJson(item, ignoreFields, validCreatorTypes, false);
        HttpEntity<JsonNode> data = new HttpEntity<JsonNode>(dataAsJson, headers);

        try {
            restTemplate.exchange(buildUri(url, false), HttpMethod.PATCH, data, String.class);
        } catch (RestClientException e) {
            throw new ZoteroConnectionException("Could not update item.", e);
        }
    }
    
    @Override
    public void updateNote(String groupId, Item item, List<String> ignoreFields) throws ZoteroConnectionException {
        String url = String.format("groups/%s/%s/%s", groupId, "items", item.getKey());
        HttpHeaders headers = new HttpHeaders();
        headers.set("If-Unmodified-Since-Version", item.getData().getVersion() + "");
        FilterProvider filter = new SimpleFilterProvider().addFilter("dataFilter", new ZoteroFieldFilter(ignoreFields));
        ObjectMapper mapper = new ObjectMapper();
        JsonNode dataAsJson;
        try {
            dataAsJson = mapper.readTree(mapper.writer(filter).writeValueAsString(item.getData()));
        } catch (JsonProcessingException e1) {
            // TODO Auto-generated catch block
            throw new ZoteroConnectionException("Could not serialize data.", e1);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            throw new ZoteroConnectionException("Could not deserialize data.", e1);
        }
        HttpEntity<JsonNode> data = new HttpEntity<JsonNode>(dataAsJson, headers);
        try {
            restTemplate.exchange(buildUri(url, false), HttpMethod.PATCH, data, String.class);
        } catch (RestClientException e) {
            throw new ZoteroConnectionException("Could not update item.", e);
        }
    }
    
    
    /**
     * This method makes a batch request call to Zotero to update items
     * 
     * @param groupId      group id of citations
     * @param items        items that have to be updated
     * @param ignoreFieldsList fields that are not necessary while updating citations
     * @param validCreatorTypesList valid creator types list
     * 
     * @return ZoteroUpdateItemsStatuses returns items statuses
     */
    @Override
    public ZoteroUpdateItemsStatuses batchUpdateItems(String groupId, List<Item> items,
            List<List<String>> ignoreFieldsList, List<List<String>> validCreatorTypesList)
            throws ZoteroConnectionException,JsonProcessingException {
        List<ItemCreationResponse> responses = new ArrayList<>();
        int totalItems = items.size() - 1;
        int itemsDone = 0;
        List<String> itemsKeys = new ArrayList<>();

        while (totalItems >= itemsDone) {
            int count = 0;
            List<JsonNode> dataAsJsonArray = new ArrayList<>();
            for (; itemsDone <= totalItems && count < ZOTERO_BATCH_UPDATE_LIMIT; count++, itemsDone++) {
                dataAsJsonArray.add(createDataJson(items.get(itemsDone), ignoreFieldsList.get(itemsDone),
                        validCreatorTypesList.get(itemsDone), false));
                itemsKeys.add(items.get(itemsDone).getKey());
            }

            ObjectMapper mapper = new ObjectMapper();
            ArrayNode arrayNode = mapper.createArrayNode();
            arrayNode.addAll(dataAsJsonArray);
            String jsonArrayString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(arrayNode);
          
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> data = new HttpEntity<String>(jsonArrayString, headers);
            String url = String.format("groups/%s/%s", groupId, "items/");
            
            try {
                ItemCreationResponse response = restTemplate
                        .exchange(buildUri(url, false), HttpMethod.POST, data, ItemCreationResponse.class).getBody();
                responses.add(response);
            } catch (RestClientException e) {
                // If an exception occurs here, stop the execution. Log it and return responses
                // so far. All unprocessed itemsKeys will be added to failedKeys list by
                // getStatusesFromResponse() method
                logger.error("Zotero connection exception occured.", e);
                return getStatusesFromResponse(responses, itemsKeys);
            }
        }
        return getStatusesFromResponse(responses, itemsKeys);
    }

    @Override
    public ItemCreationResponse createItem(String groupId, Item item, List<String> ignoreFields,
            List<String> validCreatorTypes) throws ZoteroConnectionException {
        String url = String.format("groups/%s/%s", groupId, "items");

        ignoreFields.add(ZoteroFields.VERSION);
        ignoreFields.add(ZoteroFields.KEY);

        JsonNode dataAsJson = createDataJson(item, ignoreFields, validCreatorTypes, true);
        HttpEntity<JsonNode> data = new HttpEntity<JsonNode>(dataAsJson);

        try {
            return restTemplate.exchange(buildUri(url, false), HttpMethod.POST, data, ItemCreationResponse.class)
                    .getBody();
        } catch (RestClientException e) {
            throw new ZoteroConnectionException("Could not create item.", e);
        }
    }
    
    @Override
    public ItemCreationResponse createNote(String groupId, Item item, List<String> ignoreFields) throws ZoteroConnectionException {
        String url = String.format("groups/%s/%s", groupId, "items");

        FilterProvider filter = new SimpleFilterProvider().addFilter("dataFilter", new ZoteroFieldFilter(ignoreFields));
        ObjectMapper mapper = new ObjectMapper();
        JsonNode dataAsJson;
        try {
            dataAsJson = mapper.readTree(mapper.writer(filter).writeValueAsString(new Data[] { item.getData() }));
        } catch (JsonProcessingException e1) {
            // TODO Auto-generated catch block
            throw new ZoteroConnectionException("Could not serialize data.", e1);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            throw new ZoteroConnectionException("Could not deserialize data.", e1);
        }
        HttpEntity<JsonNode> data = new HttpEntity<JsonNode>(dataAsJson);

        try {
            return restTemplate.exchange(buildUri(url, false), HttpMethod.POST, data, ItemCreationResponse.class)
                    .getBody();
        } catch (RestClientException e) {
            throw new ZoteroConnectionException("Could not create item.", e);
        }
    }
    
    private JsonNode createDataJson(Item item, List<String> ignoreFields, List<String> validCreatorTypes,
            boolean asArray) throws ZoteroConnectionException {
        FilterProvider filters = new SimpleFilterProvider().addFilter("dataFilter", new ZoteroFieldFilter(ignoreFields))
                .addFilter("creatorFilter", new CreatorFilter(validCreatorTypes));
        ObjectMapper mapper = new ObjectMapper();
        String dataAsJson;
        try {
            if (!asArray) {
                dataAsJson = mapper.writer(filters).writeValueAsString(item.getData());
            } else {
                dataAsJson = mapper.writer(filters).writeValueAsString(new Data[] { item.getData() });
            }
        } catch (JsonProcessingException e1) {
            throw new ZoteroConnectionException("Could not serialize data.", e1);
        }
        try {
            return mapper.readTree(dataAsJson);
        } catch (IOException e) {
            throw new ZoteroConnectionException("Could not deserialize data.", e);
        }
    }

    class ZoteroFieldFilter extends SimpleBeanPropertyFilter {

        private List<String> ignoreFields;

        public ZoteroFieldFilter(List<String> ignoreFields) {
            this.ignoreFields = ignoreFields;
        }

        @Override
        public void serializeAsField(Object pojo, JsonGenerator jgen, SerializerProvider provider,
                PropertyWriter writer) throws Exception {
            if (include(writer)) {
                if (!ignoreFields.contains(writer.getName())) {
                    writer.serializeAsField(pojo, jgen, provider);
                    return;
                }
            } else if (!jgen.canOmitFields()) { // since 2.3
                writer.serializeAsOmittedField(pojo, jgen, provider);
            }
        }
    }

    class CreatorFilter extends SimpleBeanPropertyFilter {

        private List<String> validCreatorTypes;

        public CreatorFilter(List<String> validCreatorTypes) {
            this.validCreatorTypes = validCreatorTypes;
        }

        @Override
        public void serializeAsField(Object pojo, JsonGenerator jgen, SerializerProvider provider,
                PropertyWriter writer) throws Exception {
            if (include(writer)) {
                if (validCreatorTypes.contains(((Creator) pojo).getCreatorType())) {
                    writer.serializeAsField(pojo, jgen, provider);
                    return;
                }
            } else if (!jgen.canOmitFields()) { // since 2.3
                writer.serializeAsOmittedField(pojo, jgen, provider);
            }
        }
    }

    @Override
    public void deleteItem(String groupId, String citationKey, Long citationVersion) throws ZoteroConnectionException {
        String url = String.format("groups/%s/%s/%s", groupId, "items", citationKey);

        HttpHeaders headers = new HttpHeaders();
        headers.set("If-Unmodified-Since-Version", citationVersion + "");

        HttpEntity<JsonNode> dataHeader = new HttpEntity<JsonNode>(headers);

        try {
            restTemplate.exchange(buildUri(url, false), HttpMethod.DELETE, dataHeader, String.class);

        } catch (RestClientException e) {
            throw new ZoteroConnectionException("Could not delete item.", e);
        }

    }

    private ZoteroUpdateItemsStatuses getStatusesFromResponse(List<ItemCreationResponse> responses,
            List<String> itemsKeys) {
        // Zotero is not sending failed items keys in response. So, we have to compute
        // failed items keys on our own using success and unchanged keys

        ZoteroUpdateItemsStatuses statuses = new ZoteroUpdateItemsStatuses();
        List<String> successKeys = new ArrayList<>();
        List<String> unchangedKeys = new ArrayList<>();
        List<FailedMessage> failedMessages = new ArrayList<>();

        for (ItemCreationResponse response : responses) {
            successKeys.addAll(extractItemKeys(response.getSuccess(), e -> e.getValue()));
            unchangedKeys.addAll(extractItemKeys(response.getUnchanged(), e -> e.getValue()));
            failedMessages.addAll(
                    response.getFailed().entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList()));
        }

        statuses.setSuccessItems(successKeys);
        statuses.setUnchangedItems(unchangedKeys);
        statuses.setFailedMessages(failedMessages);

        Set<String> successUnchangedKeys = Stream.of(successKeys, unchangedKeys).flatMap(Collection::stream)
                .collect(Collectors.toSet());
        List<String> failedKeys = new ArrayList<>();
        failedKeys = itemsKeys.stream().filter(e -> !successUnchangedKeys.contains(e)).collect(Collectors.toList());
        statuses.setFailedItems(failedKeys);

        return statuses;
    }

    private List<String> extractItemKeys(Map<String, String> map, Function<Map.Entry<String, String>, String> keyExtractor) {
        return map.entrySet().stream().map(e -> keyExtractor.apply(e)).collect(Collectors.toList());
    }
}