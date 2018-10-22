package org.springframework.social.zotero.api;

public class Library {

    private String type;
    private long id;
    private String name;
    
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    
}
