package org.springframework.social.zotero.api;

import java.util.HashMap;
import java.util.Map;

public enum ItemDeletionResponse {

    
    SUCCESS(204, "No Content"),
    LIBRARY_LOCKED(409, "Conflict"), 
    LIBRARY_VERSION_CHANGED(412, "Precondition Failed"), 
    MISSING_IF_UNMODIFIED_SINCE_VERSION(428, "Precondition Required");

    
    private int statusCode;
    private String description;

    private static final Map<Integer, ItemDeletionResponse> lookup = new HashMap<Integer, ItemDeletionResponse>();
    
    static {
        for (ItemDeletionResponse code : ItemDeletionResponse.values()) {
            lookup.put(code.getValue(), code);
        }
    }
    
    ItemDeletionResponse(int statusCode, String description) {
        this.statusCode = statusCode;
        this.description = description;
    }
    
    public int getValue() {
        return statusCode;
    }
    
    public String getDescription() {
        return description;
    }
        
    public static ItemDeletionResponse getStatusDescription(int code) {        
        return lookup.get(code);
    }

}
