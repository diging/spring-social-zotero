package org.springframework.social.zotero.api.impl;

import org.springframework.social.zotero.api.Group;
import org.springframework.social.zotero.api.GroupsOperations;
import org.springframework.social.zotero.api.Item;
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
    public Item[] getGroupItemsTop(String groupId, int start, int numberOfItems) {
        return restTemplate.getForObject(buildGroupUri("items/top", groupId, start, numberOfItems), Item[].class);
    }

    @Override
    public Group[] getGroups() {
        return restTemplate.getForObject(buildUri("users/" + getUserId() + "/groups", false), Group[].class);
    }
}