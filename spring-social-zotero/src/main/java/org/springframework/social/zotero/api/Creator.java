package org.springframework.social.zotero.api;

import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter("creatorFilter")
public class Creator {

    private String creatorType;
    private String firstName;
    private String lastName;
    
    public String getCreatorType() {
        return creatorType;
    }
    public void setCreatorType(String creatorType) {
        this.creatorType = creatorType;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    
}
