package org.springframework.social.zotero.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Collection {

    private String key;
    private long version;
    private Library library;
    private CollectionMeta meta;
    private CollectionData data;
    private long contentVersion;
    
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
    public Library getLibrary() {
        return library;
    }
    public void setLibrary(Library library) {
        this.library = library;
    }
    public CollectionMeta getMeta() {
        return meta;
    }
    public void setMeta(CollectionMeta meta) {
        this.meta = meta;
    }
    public CollectionData getData() {
        return data;
    }
    public void setData(CollectionData data) {
        this.data = data;
    }
    public long getContentVersion() {
        return contentVersion;
    }
    public void setContentVersion(long contentVersion) {
        this.contentVersion = contentVersion;
    }
}
