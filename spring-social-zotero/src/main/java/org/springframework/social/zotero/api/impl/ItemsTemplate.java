package org.springframework.social.zotero.api.impl;

import org.springframework.social.zotero.api.Item;
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
    public Item[] getItemsInfo() {
        return restTemplate.getForObject(buildUri("items", true), Item[].class);
    }
}