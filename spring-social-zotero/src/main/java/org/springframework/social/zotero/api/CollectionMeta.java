package org.springframework.social.zotero.api;

public class CollectionMeta {

    private long numCollections;
    private long numItems;
    
    public long getNumCollections() {
        return numCollections;
    }
    public void setNumCollections(long numCollections) {
        this.numCollections = numCollections;
    }
    public long getNumItems() {
        return numItems;
    }
    public void setNumItems(long numItems) {
        this.numItems = numItems;
    }
}
