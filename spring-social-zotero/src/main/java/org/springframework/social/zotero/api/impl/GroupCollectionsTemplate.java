package org.springframework.social.zotero.api.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.social.zotero.api.Collection;
import org.springframework.social.zotero.api.GroupCollectionsOperations;
import org.springframework.social.zotero.api.Item;
import org.springframework.social.zotero.api.ItemCreationResponse;
import org.springframework.social.zotero.api.ZoteroRequestHeaders;
import org.springframework.social.zotero.api.ZoteroResponse;
import org.springframework.social.zotero.exception.ZoteroConnectionException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

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
        Collection collection = restTemplate.getForObject(buildUri(url, false), Collection.class);
        
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<Item[]> response = restTemplate.exchange(
                buildGroupUri("collections/" + collectionId + "/items", groupId, 0, 1, ""), HttpMethod.GET,
                new HttpEntity<String>(headers), new ParameterizedTypeReference<Item[]>() {
                });
        long latestContentVersion = getLatestVersion(response.getHeaders());
        if (latestContentVersion > -1) {
            collection.setContentVersion(latestContentVersion);
        }
        
        return collection;
    }

    /* (non-Javadoc)
     * @see org.springframework.social.zotero.api.impl.GroupCollectionsOperations#getTopCollections(java.lang.String, int, int, java.lang.String, java.lang.Long)
     */
    @Override
    public ZoteroResponse<Collection> getTopCollections(String groupId, int start, int numberOfItems, String sortBy, Long groupVersion) {
        ZoteroResponse<Collection> zoteroResponse = new ZoteroResponse<>();
        HttpHeaders headers = new HttpHeaders();
        if (groupVersion != null) {
            headers.add(ZoteroRequestHeaders.HEADER_IF_MODIFIED_SINCE_VERSION, groupVersion.toString());
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
            headers.add(ZoteroRequestHeaders.HEADER_IF_MODIFIED_SINCE_VERSION, groupVersion.toString());
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
    public ZoteroResponse<Collection> getCollectionsByKey(String groupId, List<String> keys) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("collectionKey", String.join(",", keys));
        
        ZoteroResponse<Collection> zoteroResponse = new ZoteroResponse<>();
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<Collection[]> response = restTemplate.exchange(
                buildGroupUri("collections", groupId, queryParams), HttpMethod.GET,
                new HttpEntity<String>(headers), new ParameterizedTypeReference<Collection[]>() {
                });
        zoteroResponse.setResults(response.getBody());
        zoteroResponse.setLastVersion(getLatestVersion(response.getHeaders()));
        
        return zoteroResponse;
    }
    
    @Override
    public ZoteroResponse<Collection> getCollectionsVersions(String groupId, Long groupVersion) {
        ZoteroResponse<Collection> zoteroResponse = new ZoteroResponse<>();
        HttpHeaders headers = new HttpHeaders();
        
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("since", groupVersion + "");
        queryParams.put("format", "versions");
        
        ResponseEntity<String> response = restTemplate.exchange(
                buildGroupUri("collections", groupId, queryParams), HttpMethod.GET,
                new HttpEntity<String>(headers), new ParameterizedTypeReference<String>() {
                });
        
        List<Collection> collections = new ArrayList<Collection>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode node = mapper.readTree(response.getBody());
            Iterator<String> keys = node.fieldNames();
            while (keys.hasNext()) {
                String key = keys.next();
                Collection collection = new Collection();
                collection.setKey(key);
                collection.setVersion(node.get(key).asLong());
                collections.add(collection);
            }
        } catch (IOException e) {
            logger.error("Could not read JSON.", e);
        }
        
        zoteroResponse.setResults(collections.toArray(new Collection[collections.size()]));
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
            headers.add(ZoteroRequestHeaders.HEADER_IF_MODIFIED_SINCE_VERSION, groupVersion.toString());
        }
        ResponseEntity<Item[]> response = restTemplate.exchange(
                buildGroupUri("collections/" + collectionId + "/items", groupId, start, numberOfItems, sortBy), HttpMethod.GET,
                new HttpEntity<String>(headers), new ParameterizedTypeReference<Item[]>() {
                });
        zoteroResponse.setResults(response.getBody());
        if (response.getStatusCode() == HttpStatus.NOT_MODIFIED) {
            zoteroResponse.setNotModified(true);
        }
        
        HttpHeaders responseHeaders = response.getHeaders();
        long latestVersion = getLatestVersion(responseHeaders);
        zoteroResponse.setLastVersion(latestVersion > -1 ? latestVersion : groupVersion);
        
        List<String> totalResultsHeader = response.getHeaders().get(ZoteroRequestHeaders.HEADER_TOTAL_RESULTS);
        if (totalResultsHeader != null && totalResultsHeader.size() > 0) {
            zoteroResponse.setTotalResults(new Long(totalResultsHeader.get(0)));
        }
        return zoteroResponse;
    }
    
    private long getLatestVersion(HttpHeaders responseHeaders) {
        if (responseHeaders.get(ZoteroRequestHeaders.HEADER_LAST_MODIFIED_VERSION) != null) {
            List<String> versions = responseHeaders.get(ZoteroRequestHeaders.HEADER_LAST_MODIFIED_VERSION);
            if (versions.size() > 0) {
                // there should be just one
                return new Long(versions.get(0));
            }
        }
        return -1;
    }

    @Override
    public ItemCreationResponse createCollection(String groupId, String collectionName, String parentCollection) throws ZoteroConnectionException {
        String url = String.format("groups/%s/collections", groupId);
        
        ObjectMapper mapper = new ObjectMapper();
        
        JsonNode dataAsJson = mapper.createObjectNode()
                .put("name", collectionName)
                .put("parentCollection", parentCollection);
        
        ArrayNode jsonArray = mapper.createArrayNode();
        jsonArray.add(dataAsJson);
        
        HttpEntity<ArrayNode> data = new HttpEntity<ArrayNode>(jsonArray);
        
        try {
            return restTemplate.exchange(buildUri(url, false), HttpMethod.POST, data, ItemCreationResponse.class)
                    .getBody();
        } catch (RestClientException e) {
            throw new ZoteroConnectionException("Could not create item.", e);
        }
    }
}
