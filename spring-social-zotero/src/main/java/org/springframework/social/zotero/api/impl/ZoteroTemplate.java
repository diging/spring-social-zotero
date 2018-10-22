package org.springframework.social.zotero.api.impl;

import org.springframework.social.oauth1.AbstractOAuth1ApiBinding;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.zotero.api.ItemsOperations;
import org.springframework.social.zotero.api.Zotero;

public class ZoteroTemplate extends AbstractOAuth1ApiBinding implements Zotero {
    
    private String providerUrl;
    private ZoteroOAuth1Template oauthTemplate;
    private ItemsOperations itemsOperations;
    private String userId;

    //public ZoteroTemplate(String consumerKey, String consumerSecret, String accessToken, String secret, String providerUrl, OAuth1Operations oauthOperations) {
    public ZoteroTemplate(String consumerKey, String consumerSecret, String accessToken, String secret, String providerUrl, String userId) {
        super(consumerKey, consumerSecret, accessToken, secret);
        this.providerUrl = providerUrl;
        //oauthTemplate = (ZoteroOAuth1Template) oauthOperations;
        this.userId = userId;
        initSubApis();
    }
    
    private void initSubApis() {
        this.itemsOperations = new ItemsTemplate(getRestTemplate(), isAuthorized(), providerUrl, userId);
    }

    @Override
    public ItemsOperations getItemsOperations() {
        //itemsOperations.setUserId(oauthTemplate.getUserId());
        return itemsOperations;
    }
    
    @Override
    public String getUserId() {
        return userId; //oauthTemplate.getUserId();
    }
}
