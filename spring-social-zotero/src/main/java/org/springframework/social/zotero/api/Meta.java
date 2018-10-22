package org.springframework.social.zotero.api;

public class Meta {

    private String creatorSummary;
    private String parsedDate;
    private long numChildren;
    
    public String getCreatorSummary() {
        return creatorSummary;
    }
    public void setCreatorSummary(String creatorSummary) {
        this.creatorSummary = creatorSummary;
    }
    public String getParsedDate() {
        return parsedDate;
    }
    public void setParsedDate(String parsedDate) {
        this.parsedDate = parsedDate;
    }
    public long getNumChildren() {
        return numChildren;
    }
    public void setNumChildren(long numChildren) {
        this.numChildren = numChildren;
    }
    
}
