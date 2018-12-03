package org.springframework.social.zotero.api.impl;

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
    public Group[] getGroups() {
        return restTemplate.getForObject(buildUri("users/" + getUserId() + "/groups", false), Group[].class);
    }
}