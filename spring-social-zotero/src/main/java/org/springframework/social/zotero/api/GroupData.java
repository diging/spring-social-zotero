package org.springframework.social.zotero.api;

public class GroupData {

    private long id;
    private long version;
    private String name;
    private long owner;
    private String type;
    private String description;
    private String url;
    private String libraryEditing;
    private String libraryReading;
    private String fileEditing;
    
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
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public long getOwner() {
        return owner;
    }
    public void setOwner(long owner) {
        this.owner = owner;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getLibraryEditing() {
        return libraryEditing;
    }
    public void setLibraryEditing(String libraryEditing) {
        this.libraryEditing = libraryEditing;
    }
    public String getLibraryReading() {
        return libraryReading;
    }
    public void setLibraryReading(String libraryReading) {
        this.libraryReading = libraryReading;
    }
    public String getFileEditing() {
        return fileEditing;
    }
    public void setFileEditing(String fileEditing) {
        this.fileEditing = fileEditing;
    }
    
    
}
