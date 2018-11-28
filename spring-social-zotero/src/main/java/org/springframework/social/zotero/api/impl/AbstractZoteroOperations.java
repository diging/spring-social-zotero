package org.springframework.social.zotero.api.impl;

import java.net.URI;

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

    protected URI buildGroupUri(String path, String groupId) {
        return URIBuilder.fromUri(String.format("%sgroups/%s/%s", apiUrlBase, groupId, path)).build();
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