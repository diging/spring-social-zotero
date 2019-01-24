package org.springframework.social.zotero.api;

import java.util.Map;

public class ItemCreationResponse {

    private Map<String, String> success;
    private Map<String, String> unchanged;
    private Map<String, FailedMessage> failed;
    
    public Map<String, String> getSuccess() {
        return success;
    }
    public void setSuccess(Map<String, String> success) {
        this.success = success;
    }
    public Map<String, String> getUnchanged() {
        return unchanged;
    }
    public void setUnchanged(Map<String, String> unchanged) {
        this.unchanged = unchanged;
    }
    public Map<String, FailedMessage> getFailed() {
        return failed;
    }
    public void setFailed(Map<String, FailedMessage> failed) {
        this.failed = failed;
    }
    
    public static class FailedMessage {
        private Integer code;
        private String message;
        
        public Integer getCode() {
            return code;
        }
        public void setCode(Integer code) {
            this.code = code;
        }
        public String getMessage() {
            return message;
        }
        public void setMessage(String message) {
            this.message = message;
        }
        
    }

}
