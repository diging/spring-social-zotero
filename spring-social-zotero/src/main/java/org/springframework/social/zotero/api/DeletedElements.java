package org.springframework.social.zotero.api;

import java.util.List;

public class DeletedElements {

    private List<String> collections;
    private List<String> items;
    private List<String> searches;
    private List<String> tags;
    private List<String> settings;
    
    public List<String> getCollections() {
        return collections;
    }
    public void setCollections(List<String> collections) {
        this.collections = collections;
    }
    public List<String> getItems() {
        return items;
    }
    public void setItems(List<String> items) {
        this.items = items;
    }
    public List<String> getSearches() {
        return searches;
    }
    public void setSearches(List<String> searches) {
        this.searches = searches;
    }
    public List<String> getTags() {
        return tags;
    }
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    public List<String> getSettings() {
        return settings;
    }
    public void setSettings(List<String> settings) {
        this.settings = settings;
    }
}
