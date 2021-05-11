package org.springframework.social.zotero.api;

import java.util.List;

import org.springframework.social.zotero.api.ItemCreationResponse.FailedMessage;

public class ZoteroUpdateItemsStatuses {
    private List<String> successItems;
    private List<String> failedItems;
    private List<String> unchagedItems;
    private List<FailedMessage> failedMessages;
    
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
    
    public List<FailedMessage> getFailedMessages(){
        return failedMessages;
    }
    
    public void setFailedMessages(List<FailedMessage> failedMessages) {
        this.failedMessages = failedMessages;
    } 
}
