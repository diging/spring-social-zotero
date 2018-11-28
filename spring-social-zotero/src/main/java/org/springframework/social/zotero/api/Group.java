package org.springframework.social.zotero.api;

public class Group {

    private long id;
    private long version;
    private Links links;
    private GroupMeta meta;
    private GroupData data;
    
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public long getVersion() {
        return version;
    }
    public void setVersion(long version) {
        this.version = version;
    }
    public Links getLinks() {
        return links;
    }
    public void setLinks(Links links) {
        this.links = links;
    }
    public GroupMeta getMeta() {
        return meta;
    }
    public void setMeta(GroupMeta meta) {
        this.meta = meta;
    }
    public GroupData getData() {
        return data;
    }
    public void setData(GroupData data) {
        this.data = data;
    }
}
