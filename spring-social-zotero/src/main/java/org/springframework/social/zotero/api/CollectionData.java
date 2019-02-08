package org.springframework.social.zotero.api;

public class CollectionData {

    private String key;
    private long version;
    private String name;
    private String parentCollection;
    
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public long getVersion() {
        return version;
    }
    public void setVersion(long version) {
        this.version = version;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getParentCollection() {
        return parentCollection;
    }
    public void setParentCollection(String parentCollection) {
        this.parentCollection = parentCollection;
    }
    
}
