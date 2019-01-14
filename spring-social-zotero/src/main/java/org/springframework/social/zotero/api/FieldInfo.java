package org.springframework.social.zotero.api;

public class FieldInfo {

    private String field;
    private String localized;
    
    public String getField() {
        return field;
    }
    public void setField(String field) {
        this.field = field;
    }
    public String getLocalized() {
        return localized;
    }
    public void setLocalized(String localized) {
        this.localized = localized;
    }
}
