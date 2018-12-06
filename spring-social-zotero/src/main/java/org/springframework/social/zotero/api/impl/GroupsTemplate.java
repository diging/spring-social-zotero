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
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    public Item[] getGroupItems(String groupId, int start, int numberOfItems) {
        return restTemplate.getForObject(buildGroupUri("items", groupId, start, numberOfItems), Item[].class);
    }

    @Override
    public ZoteroResponse<Item> getGroupItemsTop(String groupId, int start, int numberOfItems) {
        ZoteroResponse<Item> zoteroResponse = new ZoteroResponse<>();
        ResponseEntity<Item[]> response = restTemplate.exchange(
                buildGroupUri("items/top", groupId, start, numberOfItems), HttpMethod.GET,
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
        ResponseEntity<Group[]> response = restTemplate.exchange(
                buildUri("users/" + getUserId() + "/groups", false), HttpMethod.GET,
                new HttpEntity<String>(new HttpHeaders()), new ParameterizedTypeReference<Group[]>() {
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
                buildUri("users/" + getUserId() + "/groups", false), HttpMethod.GET,
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
    public Item getGroupItem(String groupId, String itemKey) {
        String url = String.format("groups/%s/%s/%s", groupId, "items", itemKey);
        return restTemplate.getForObject(buildUri(url, false), Item.class);
    }
}