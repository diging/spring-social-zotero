package org.springframework.social.zotero.api.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.social.zotero.api.Group;
import org.springframework.social.zotero.api.GroupsOperations;
import org.springframework.social.zotero.api.Item;
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
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

public class GroupsTemplate extends AbstractZoteroOperations implements GroupsOperations {

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
    public ZoteroResponse<Item> getGroupItemsTop(String groupId, int start, int numberOfItems, String sortBy) {
        ZoteroResponse<Item> zoteroResponse = new ZoteroResponse<>();
        ResponseEntity<Item[]> response = restTemplate.exchange(
                buildGroupUri("items/top", groupId, start, numberOfItems, sortBy), HttpMethod.GET,
                new HttpEntity<String>(new HttpHeaders()), new ParameterizedTypeReference<Item[]>() {
                });
        zoteroResponse.setResults(response.getBody());
        List<String> totalResultsHeader = response.getHeaders().get(HEADER_TOTAL_RESULTS);
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
        List<String> totalResultsHeader = response.getHeaders().get(HEADER_TOTAL_RESULTS);
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
            // do nothing for now
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
    public void updateItem(String groupId, Item item, List<String> ignoreFields) throws ZoteroConnectionException {
        String url = String.format("groups/%s/%s/%s", groupId, "items", item.getKey());

        PropertyFilter filter = new SimpleBeanPropertyFilter() {
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
        };
        FilterProvider filters = new SimpleFilterProvider().addFilter("dataFilter", filter);
        ObjectMapper mapper = new ObjectMapper();
        String dataAsJson;
        try {
            dataAsJson = mapper.writer(filters).writeValueAsString(item.getData());
        } catch (JsonProcessingException e1) {
            throw new ZoteroConnectionException("Could not serialize data.", e1);
        }
        
        HttpEntity<String> data = new HttpEntity<String>(dataAsJson);

        try {
            restTemplate.exchange(buildUri(url, false), HttpMethod.PATCH, data, String.class);
        } catch (RestClientException e) {
            throw new ZoteroConnectionException("Could not update item.", e);
        }
    }
}