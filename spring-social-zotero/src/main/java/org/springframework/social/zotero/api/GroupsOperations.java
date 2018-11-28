package org.springframework.social.zotero.api;

public interface GroupsOperations {

    Item[] getGroupItems(String groupId);

    Group[] getGroups();

    void setUserId(String userId);
}