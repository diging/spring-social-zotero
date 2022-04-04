package org.springframework.social.zotero.api;

public class Tag {

    private String tag;
    private int type;
    
    public Tag() {
    }
    public Tag(String tag) {
        this.tag = tag;
    }
    public String getTag() {
        return tag;
    }
    public void setTag(String tag) {
        this.tag = tag;
    }
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    
    
}
