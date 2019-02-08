package org.springframework.social.zotero.api.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.social.zotero.api.Collection;
import org.springframework.social.zotero.api.Group;
import org.springframework.social.zotero.api.GroupCollectionsOperations;
import org.springframework.social.zotero.api.Item;
import org.springframework.social.zotero.api.ZoteroRequestHeaders;
import org.springframework.social.zotero.api.ZoteroResponse;
import org.springframework.web.client.RestTemplate;

public class GroupCollectionsTemplate extends AbstractZoteroOperations implements GroupCollectionsOperations {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final RestTemplate restTemplate;

    public GroupCollectionsTemplate(RestTemplate restTemplate, boolean isAuthorizedForUser, String providerUrl, String userId) {
        super(isAuthorizedForUser, providerUrl);
        setUserId(userId);
        this.restTemplate = restTemplate;
    }
    
    @Override
    public Collection getCollection(String groupId, String collectionId) {
        String url = String.format("groups/%s/collections/%s", groupId, collectionId);
        return restTemplate.getForObject(buildUri(url, false), Collection.class);
    }

    /* (non-Javadoc)
     * @see org.springframework.social.zotero.api.impl.GroupCollectionsOperations#getTopCollections(java.lang.String, int, int, java.lang.String, java.lang.Long)
     */
    @Override
    public ZoteroResponse<Collection> getTopCollections(String groupId, int start, int numberOfItems, String sortBy, Long groupVersion) {
        ZoteroResponse<Collection> zoteroResponse = new ZoteroResponse<>();
        HttpHeaders headers = new HttpHeaders();
        if (groupVersion != null) {
            headers.add(ZoteroRequestHeaders.HEDAER_IF_MODIFIED_SINCE_VERSION, groupVersion.toString());
        }
        
        ResponseEntity<Collection[]> response = restTemplate.exchange(
                buildGroupUri("collections/top", groupId, start, numberOfItems, sortBy), HttpMethod.GET,
                new HttpEntity<String>(headers), new ParameterizedTypeReference<Collection[]>() {
                });
        zoteroResponse.setResults(response.getBody());
        if (response.getStatusCode() == HttpStatus.NOT_MODIFIED) {
            zoteroResponse.setNotModified(true);
        }
        List<String> totalResultsHeader = response.getHeaders().get(ZoteroRequestHeaders.HEADER_TOTAL_RESULTS);
        if (totalResultsHeader != null && totalResultsHeader.size() > 0) {
            zoteroResponse.setTotalResults(new Long(totalResultsHeader.get(0)));
        }
        return zoteroResponse;
    }
    
    @Override
    public ZoteroResponse<Collection> getCollections(String groupId, String collectionId, int start, int numberOfItems, String sortBy, Long groupVersion) {
        ZoteroResponse<Collection> zoteroResponse = new ZoteroResponse<>();
        HttpHeaders headers = new HttpHeaders();
        if (groupVersion != null) {
            headers.add(ZoteroRequestHeaders.HEDAER_IF_MODIFIED_SINCE_VERSION, groupVersion.toString());
        }
        
        ResponseEntity<Collection[]> response = restTemplate.exchange(
                buildGroupUri("collections/" + collectionId + "/collections", groupId, start, numberOfItems, sortBy), HttpMethod.GET,
                new HttpEntity<String>(headers), new ParameterizedTypeReference<Collection[]>() {
                });
        zoteroResponse.setResults(response.getBody());
        if (response.getStatusCode() == HttpStatus.NOT_MODIFIED) {
            zoteroResponse.setNotModified(true);
        }
        List<String> totalResultsHeader = response.getHeaders().get(ZoteroRequestHeaders.HEADER_TOTAL_RESULTS);
        if (totalResultsHeader != null && totalResultsHeader.size() > 0) {
            zoteroResponse.setTotalResults(new Long(totalResultsHeader.get(0)));
        }
        return zoteroResponse;
    }
    
    @Override
    public ZoteroResponse<Item> getItems(String groupId, String collectionId, int start, int numberOfItems, String sortBy, Long groupVersion) {
        ZoteroResponse<Item> zoteroResponse = new ZoteroResponse<>();
        HttpHeaders headers = new HttpHeaders();
        if (groupVersion != null) {
            headers.add(ZoteroRequestHeaders.HEDAER_IF_MODIFIED_SINCE_VERSION, groupVersion.toString());
        }
        ResponseEntity<Item[]> response = restTemplate.exchange(
                buildGroupUri("collections/" + collectionId + "/items", groupId, start, numberOfItems, sortBy), HttpMethod.GET,
                new HttpEntity<String>(headers), new ParameterizedTypeReference<Item[]>() {
                });
        zoteroResponse.setResults(response.getBody());
        if (response.getStatusCode() == HttpStatus.NOT_MODIFIED) {
            zoteroResponse.setNotModified(true);
        }
        List<String> totalResultsHeader = response.getHeaders().get(ZoteroRequestHeaders.HEADER_TOTAL_RESULTS);
        if (totalResultsHeader != null && totalResultsHeader.size() > 0) {
            zoteroResponse.setTotalResults(new Long(totalResultsHeader.get(0)));
        }
        return zoteroResponse;
    }
}
