package org.springframework.social.zotero.api;

public class Item {

    private static final String ZOTERO_NOTE_KEY = "note";
    private static final String CITESPHERE_METADATA_TAG = "citesphere-metadata";
    
    private String key;
    private long version;
    private Library library;
    private Data data;
    private Meta meta;
    
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
    public Data getData() {
        return data;
    }
    public void setData(Data data) {
        this.data = data;
    }
    public Meta getMeta() {
        return meta;
    }
    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public boolean isMetaDataNote() {
        if (this.data.getItemType().equals(ZOTERO_NOTE_KEY)
                && this.data.getTags().stream().anyMatch(tag -> tag.getTag().equals(CITESPHERE_METADATA_TAG)))
            return true;
        return false;
    }
}
