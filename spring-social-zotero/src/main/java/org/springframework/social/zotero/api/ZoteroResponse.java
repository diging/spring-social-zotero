package org.springframework.social.zotero.api;

public class ZoteroResponse<T> {

    private long totalResults;
    private int returnCode;
    private T[] results;
    
    public long getTotalResults() {
        return totalResults;
    }
    public void setTotalResults(long totalResults) {
        this.totalResults = totalResults;
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
}
