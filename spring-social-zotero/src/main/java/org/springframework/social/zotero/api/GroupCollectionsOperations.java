package org.springframework.social.zotero.api;

import java.util.List;

public interface GroupCollectionsOperations extends ZoteroOperations {

    ZoteroResponse<Collection> getTopCollections(String groupId, int start, int numberOfItems, String sortBy,
            Long groupVersion);

    ZoteroResponse<Item> getItems(String groupId, String collectionId, int start, int numberOfItems, String sortBy, Long groupVersion);

    ZoteroResponse<Collection> getCollections(String groupId, String collectionId, int start, int numberOfItems, String sortBy, Long groupVersion);

    Collection getCollection(String groupId, String collectionId);

    ZoteroResponse<Collection> getCollectionsVersions(String groupId, Long groupVersion);

    ZoteroResponse<Collection> getCollectionsByKey(String groupId, List<String> keys);

}