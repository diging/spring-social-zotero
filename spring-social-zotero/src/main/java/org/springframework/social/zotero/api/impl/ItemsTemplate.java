package org.springframework.social.zotero.api.impl;

import org.springframework.social.zotero.api.ItemsInfo;
import org.springframework.social.zotero.api.ItemsOperations;
import org.springframework.web.client.RestTemplate;

public class ItemsTemplate extends AbstractZoteroOperations implements ItemsOperations {

    private final RestTemplate restTemplate;
    
    public ItemsTemplate(RestTemplate restTemplate, boolean isAuthorizedForUser, String providerUrl, String userId) {
        super(isAuthorizedForUser, providerUrl);
        setUserId(userId);
        this.restTemplate = restTemplate;
    }
    
    /* (non-Javadoc)
     * @see org.springframework.social.zotero.api.impl.ItemsOperations#getItemsInfo()
     */
    @Override
    public ItemsInfo getItemsInfo() {
        return restTemplate.getForObject(buildUri("items", true), ItemsInfo.class);
    }
}