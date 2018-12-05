package org.springframework.social.zotero.api;

public interface GroupsOperations {

    Item[] getGroupItems(String groupId, int start, int numberOfItems);

    Group[] getGroups();

    void setUserId(String userId);

    ZoteroResponse<Item> getGroupItemsTop(String groupId, int start, int numberOfItems);

    Item getGroupItem(String groupId, String itemKey);
}