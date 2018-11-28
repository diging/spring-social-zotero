package org.springframework.social.zotero.api;

public class GroupMeta {

    private String created;
    private String lastModified;
    private long numItems;
    
    public String getCreated() {
        return created;
    }
    public void setCreated(String created) {
        this.created = created;
    }
    public String getLastModified() {
        return lastModified;
    }
    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }
    public long getNumItems() {
        return numItems;
    }
    public void setNumItems(long numItems) {
        this.numItems = numItems;
    }
    
    
}
