package org.springframework.social.zotero.api.impl;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.social.MissingAuthorizationException;
import org.springframework.social.support.URIBuilder;
import org.springframework.social.zotero.api.ZoteroOperations;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

abstract class AbstractZoteroOperations implements ZoteroOperations {

    private final boolean isUserAuthorized;
    private String apiUrlBase;
    private String userId;

    public AbstractZoteroOperations(boolean isUserAuthorized, String providerUrl) {
        this.isUserAuthorized = isUserAuthorized;
        this.apiUrlBase = providerUrl.endsWith("/") ? providerUrl : providerUrl + "/";
    }

    protected void requireUserAuthorization() {
        if (!isUserAuthorized) {
            throw new MissingAuthorizationException("zotero");
        }
    }

    protected URI buildGroupUri(String path, String groupId, int start, int numberOfItems) {
        String url = String.format("%sgroups/%s/%s", apiUrlBase, groupId, path);
        Map<String, Integer> queryParams = new HashMap<>();
        if (start > -1) {
            queryParams.put("start", start);
        }
        if (numberOfItems > 0) {
            queryParams.put("limit", numberOfItems);
        }
        if (queryParams.size() > 0) {
            String queryString = String.join("&", queryParams.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.toList()));
            url = url + "?" + queryString;
        }
        
        return URIBuilder.fromUri(url).build();
    }

    protected URI buildUri(String path, boolean userApi) {
        return buildUri(path, EMPTY_PARAMETERS, userApi);
    }

    protected URI buildUri(String path, String parameterName, String parameterValue, boolean userApi) {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
        parameters.set(parameterName, parameterValue);
        return buildUri(path, parameters, userApi);
    }

    protected URI buildUri(String path, MultiValueMap<String, String> parameters, boolean userApi) {
        String baseUrl = apiUrlBase;
        if (userApi) {
            baseUrl += userId + "/";
        }
        return URIBuilder.fromUri(baseUrl + path).queryParams(parameters).build();
    }

    private static final LinkedMultiValueMap<String, String> EMPTY_PARAMETERS = new LinkedMultiValueMap<String, String>();

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.social.zotero.api.impl.ZoteroOperations#setUserId(java.
     * lang.String)
     */
    @Override
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    protected String getUserId() {
        return this.userId;
    }
}