package org.springframework.social.zotero.api;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter("dataFilter")
public class Data {

    private String key;
    private long version;
    private String parentItem;
    private String itemType;
    private String linkMode;
    private String title;
    private String accessDate;
    private String url;
    private String note;
    private String contentType;
    private String charset;
    private String filename;
    private String md5;
    private long mtime;
    private String dateAdded;
    private String dateModified;
    private List<Creator> creators;
    private String abstractNote;
    private String publicationTitle;
    private String volume;
    private String issue;
    private String pages;
    private String date;
    private String series;
    private String seriesTitle;
    private String seriesText;
    private String journalAbbreviation;
    private String language;
    private String DOI;
    private String issn;
    private String shortTitle;
    private String archive;
    private String archiveLocation;
    private String libraryCatalog;
    private String callNumber;
    private String rights;
    private String extra;
    private List<Tag> tags;
    private List<String> collections;
    private int deleted;
    
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public long getVersion() {
        return version;
    }
    public void setVersion(long version) {
        this.version = version;
    }
    public String getParentItem() {
        return parentItem;
    }
    public void setParentItem(String parentItem) {
        this.parentItem = parentItem;
    }
    public String getItemType() {
        return itemType;
    }
    public void setItemType(String itemType) {
        this.itemType = itemType;
    }
    public String getLinkMode() {
        return linkMode;
    }
    public void setLinkMode(String linkMode) {
        this.linkMode = linkMode;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getAccessDate() {
        return accessDate;
    }
    public void setAccessDate(String accessDate) {
        this.accessDate = accessDate;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }
    public String getContentType() {
        return contentType;
    }
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    public String getCharset() {
        return charset;
    }
    public void setCharset(String charset) {
        this.charset = charset;
    }
    public String getFilename() {
        return filename;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }
    public String getMd5() {
        return md5;
    }
    public void setMd5(String md5) {
        this.md5 = md5;
    }
    public long getMtime() {
        return mtime;
    }
    public void setMtime(long mtime) {
        this.mtime = mtime;
    }
    public String getDateAdded() {
        return dateAdded;
    }
    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }
    public String getDateModified() {
        return dateModified;
    }
    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }
    public List<Creator> getCreators() {
        return creators;
    }
    public void setCreators(List<Creator> creators) {
        this.creators = creators;
    }
    public String getAbstractNote() {
        return abstractNote;
    }
    public void setAbstractNote(String abstractNote) {
        this.abstractNote = abstractNote;
    }
    public String getPublicationTitle() {
        return publicationTitle;
    }
    public void setPublicationTitle(String publicationTitle) {
        this.publicationTitle = publicationTitle;
    }
    public String getVolume() {
        return volume;
    }
    public void setVolume(String volume) {
        this.volume = volume;
    }
    public String getIssue() {
        return issue;
    }
    public void setIssue(String issue) {
        this.issue = issue;
    }
    public String getPages() {
        return pages;
    }
    public void setPages(String pages) {
        this.pages = pages;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getSeries() {
        return series;
    }
    public void setSeries(String series) {
        this.series = series;
    }
    public String getSeriesTitle() {
        return seriesTitle;
    }
    public void setSeriesTitle(String seriesTitle) {
        this.seriesTitle = seriesTitle;
    }
    public String getSeriesText() {
        return seriesText;
    }
    public void setSeriesText(String seriesText) {
        this.seriesText = seriesText;
    }
    public String getJournalAbbreviation() {
        return journalAbbreviation;
    }
    public void setJournalAbbreviation(String journalAbbreviation) {
        this.journalAbbreviation = journalAbbreviation;
    }
    public String getLanguage() {
        return language;
    }
    public void setLanguage(String language) {
        this.language = language;
    }
    public String getDOI() {
        System.out.println("====================== in get " + DOI);
        return DOI;
    }
    public void setDOI(String dOI) {
        System.out.println("====================== in set " + dOI);
        DOI = dOI;
    }
    public String getIssn() {
        return issn;
    }
    public void setIssn(String issn) {
        this.issn = issn;
    }
    public String getShortTitle() {
        return shortTitle;
    }
    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }
    public String getArchive() {
        return archive;
    }
    public void setArchive(String archive) {
        this.archive = archive;
    }
    public String getArchiveLocation() {
        return archiveLocation;
    }
    public void setArchiveLocation(String archiveLocation) {
        this.archiveLocation = archiveLocation;
    }
    public String getLibraryCatalog() {
        return libraryCatalog;
    }
    public void setLibraryCatalog(String libraryCatalog) {
        this.libraryCatalog = libraryCatalog;
    }
    public String getCallNumber() {
        return callNumber;
    }
    public void setCallNumber(String callNumber) {
        this.callNumber = callNumber;
    }
    public String getRights() {
        return rights;
    }
    public void setRights(String rights) {
        this.rights = rights;
    }
    public String getExtra() {
        return extra;
    }
    public void setExtra(String extra) {
        this.extra = extra;
    }
    public List<Tag> getTags() {
        return tags;
    }
    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
    public List<String> getCollections() {
        return collections;
    }
    public void setCollections(List<String> collections) {
        this.collections = collections;
    }
    public int getDeleted() {
        return deleted;
    }
    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }
}
