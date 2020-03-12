package org.springframework.social.zotero.api.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

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
import org.springframework.social.zotero.api.Group;
import org.springframework.social.zotero.api.GroupsOperations;
import org.springframework.social.zotero.api.Item;
import org.springframework.social.zotero.api.ItemCreationResponse;
import org.springframework.social.zotero.api.ZoteroFields;
import org.springframework.social.zotero.api.ZoteroRequestHeaders;
import org.springframework.social.zotero.api.ZoteroResponse;
import org.springframework.social.zotero.exception.ZoteroConnectionException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

public class GroupsTemplate extends AbstractZoteroOperations implements GroupsOperations {

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
    public ZoteroResponse<Item> getGroupItemsTop(String groupId, int start, int numberOfItems, String sortBy,
            Long groupVersion) {
        ZoteroResponse<Item> zoteroResponse = new ZoteroResponse<>();
        HttpHeaders headers = new HttpHeaders();
        if (groupVersion != null) {
            headers.add(ZoteroRequestHeaders.HEDAER_IF_MODIFIED_SINCE_VERSION, groupVersion.toString());
        }
        ResponseEntity<Item[]> response = restTemplate.exchange(
                buildGroupUri("items/top", groupId, start, numberOfItems, sortBy), HttpMethod.GET,
                new HttpEntity<String>(headers), new ParameterizedTypeReference<Item[]>() {
                });
        zoteroResponse.setResults(response.getBody());
        if (response.getStatusCode() == HttpStatus.NOT_MODIFIED) {
            zoteroResponse.setNotModified(true);
        }
        List<String> totalResultsHeader = response.getHeaders().get(ZoteroRequestHeaders.HEADER_TOTAL_RESULTS);
        if (totalResultsHeader != null && totalResultsHeader.size() > 0) {
            zoteroResponse.setTotalResults(new Long(totalResultsHeader.get(0)));
        }
        return zoteroResponse;
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
    public void deleteItem(String groupId, Item item, List<String> ignoreFields, List<String> validCreatorTypes)
            throws ZoteroConnectionException {
        String url = String.format("groups/%s/%s/%s", groupId, "items", item.getKey());

        HttpHeaders headers = new HttpHeaders();
        headers.set("If-Unmodified-Since-Version", item.getData().getVersion() + "");

        HttpEntity<JsonNode> dataHeader = new HttpEntity<JsonNode>(headers);

        try {
            restTemplate.exchange(buildUri(url, false), HttpMethod.DELETE, dataHeader, String.class);

        } catch (RestClientException e) {
            throw new ZoteroConnectionException("Could not delete item.", e);
        }

    }
}