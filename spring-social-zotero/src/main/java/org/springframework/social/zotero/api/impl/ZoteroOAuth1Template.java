package org.springframework.social.zotero.api.impl;

import org.springframework.social.oauth1.OAuth1Template;
import org.springframework.social.oauth1.OAuth1Version;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.util.MultiValueMap;

public class ZoteroOAuth1Template extends OAuth1Template {
    
    private String userId;

    public ZoteroOAuth1Template(String consumerKey, String consumerSecret, String requestTokenUrl, String authorizeUrl,
            String accessTokenUrl, OAuth1Version version) {
        super(consumerKey, consumerSecret, requestTokenUrl, authorizeUrl, accessTokenUrl, version);
    }

    public ZoteroOAuth1Template(String consumerKey, String consumerSecret, String requestTokenUrl, String authorizeUrl,
            String authenticateUrl, String accessTokenUrl, OAuth1Version version) {
        super(consumerKey, consumerSecret, requestTokenUrl, authorizeUrl, authenticateUrl, accessTokenUrl, version);
    }

    public ZoteroOAuth1Template(String consumerKey, String consumerSecret, String requestTokenUrl, String authorizeUrl,
            String authenticateUrl, String accessTokenUrl) {
        super(consumerKey, consumerSecret, requestTokenUrl, authorizeUrl, authenticateUrl, accessTokenUrl);
    }

    public ZoteroOAuth1Template(String consumerKey, String consumerSecret, String requestTokenUrl, String authorizeUrl,
            String accessTokenUrl) {
        super(consumerKey, consumerSecret, requestTokenUrl, authorizeUrl, accessTokenUrl);
    }

    
    protected OAuthToken createOAuthToken(String tokenValue, String tokenSecret, MultiValueMap<String, String> response) {
        userId = response.getFirst("userID");
        return new OAuthToken(tokenValue, tokenSecret);
    }
    
    public String getUserId() {
        return userId;
    }
}
