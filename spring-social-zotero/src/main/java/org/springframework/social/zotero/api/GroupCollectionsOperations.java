package org.springframework.social.zotero.api;

public interface GroupCollectionsOperations extends ZoteroOperations {

    ZoteroResponse<Collection> getTopCollections(String groupId, int start, int numberOfItems, String sortBy,
            Long groupVersion);

    ZoteroResponse<Item> getItems(String groupId, String collectionId, int start, int numberOfItems, String sortBy, Long groupVersion);

    ZoteroResponse<Collection> getCollections(String groupId, String collectionId, int start, int numberOfItems, String sortBy, Long groupVersion);

    Collection getCollection(String groupId, String collectionId);

}