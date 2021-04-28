package org.springframework.social.zotero.api;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * This class lists possible responses 
 * of Zotero delete multiple items API
 * in the form of enum.
 * 
 * @author Sayali Tanawade
 * 
 */
public enum ItemDeletionResponse {

    /**
     * 204 No Content 
     * The items were deleted.
     */
    SUCCESS(204, "No Content"),
    /**
     * 409 Conflict
     * The target library is locked.
     */
    LIBRARY_LOCKED(409, "Conflict"), 
    /**
     * 412 Precondition Failed
     * The library has changed since the specified version.
     */
    LIBRARY_VERSION_CHANGED(412, "Precondition Failed"),
    /**
     * 428 Precondition Required
     * [If-Unmodified-Since-Version] was not provided.
     */
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
    
    /**
     * 
     * @param code is the http status code from Zotero API
     * @return the enum constant corresponding to the code from lookup map
     */
        
    public static ItemDeletionResponse getStatusDescription(int code) {        
        return lookup.get(code);
    }

}
