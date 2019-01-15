package org.springframework.social.zotero.api;

import java.util.List;

import org.springframework.social.zotero.exception.ZoteroConnectionException;

public interface GroupsOperations {

    Item[] getGroupItems(String groupId, int start, int numberOfItems, String sortBy);

    ZoteroResponse<Group> getGroups();

    void setUserId(String userId);

    ZoteroResponse<Item> getGroupItemsTop(String groupId, int start, int numberOfItems, String sortBy);

    Item getGroupItem(String groupId, String itemKey);

    ZoteroResponse<Group> getGroupsVersions();

    Group getGroup(String groupId);
    
    void updateItem(String groupId, Item item, List<String> ignoreFields) throws ZoteroConnectionException;

    Long getGroupItemVersion(String groupId, String itemKey);
}