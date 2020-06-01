package org.springframework.social.zotero.api;

public class ZoteroResponse<T> {

    private long totalResults;
    private long lastVersion;
    private int returnCode;
    private T[] results;
    private Boolean notModified;
    
    public long getTotalResults() {
        return totalResults;
    }
    public void setTotalResults(long totalResults) {
        this.totalResults = totalResults;
    }
    public long getLastVersion() {
        return lastVersion;
    }
    public void setLastVersion(long lastVersion) {
        this.lastVersion = lastVersion;
    }
    public int getReturnCode() {
        return returnCode;
    }
    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }
    public T[] getResults() {
        return results;
    }
    public void setResults(T[] results) {
        this.results = results;
    }
    public Boolean getNotModified() {
        return notModified;
    }
    public void setNotModified(Boolean notModified) {
        this.notModified = notModified;
    }
}
