package org.springframework.social.zotero.api;

import java.util.List;
import java.util.Set;

public class ZoteroUpdateItemsStatuses {
    private List<String> successItems;
    private List<String> failedItems;
    private List<String> unchagedItems;
    private List<String> failedMessages;
    private Set<String> failedCodes;
    
    public void setSuccessItems(List<String> successItems) {
        this.successItems = successItems;
    }
    
    public List<String> getSuccessItems() {
        return successItems;
    }
    
    public void setFailedItems(List<String> failedItems) {
        this.failedItems = failedItems;
    }
    
    public List<String> getFailedItems() {
        return failedItems;
    }
    
    public void setUnchangedItems(List<String> unchagedItems) {
        this.unchagedItems = unchagedItems;
    }
    
    public List<String> getUnchangedItems() {
        return unchagedItems;
    }
    
    public List<String> getFailedMessages(){
        return failedMessages;
    }
    
    public void setFailedMessages(List<String> failedMessages) {
        this.failedMessages = failedMessages;
    }
    
    public Set<String> getFailedCodes(){
        return failedCodes;
    }
    
    public void setFailedCodes(Set<String> failedCodes) {
        this.failedCodes = failedCodes;
    } 
}
