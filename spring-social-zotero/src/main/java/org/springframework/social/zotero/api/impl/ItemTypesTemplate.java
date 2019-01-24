package org.springframework.social.zotero.api.impl;

import org.springframework.social.zotero.api.CreatorType;
import org.springframework.social.zotero.api.FieldInfo;
import org.springframework.social.zotero.api.ItemTypesOperations;
import org.springframework.web.client.RestTemplate;

public class ItemTypesTemplate extends AbstractZoteroOperations implements ItemTypesOperations {

    private final RestTemplate restTemplate;

    public ItemTypesTemplate(RestTemplate restTemplate, boolean isAuthorizedForUser, String providerUrl, String userId) {
        super(isAuthorizedForUser, providerUrl);
        setUserId(userId);
        this.restTemplate = restTemplate;
    }

    @Override
    public FieldInfo[] getFields(String itemType) {
        return restTemplate.getForObject(buildUri("itemTypeFields?itemType=" + itemType, false), FieldInfo[].class);
    }
    
    @Override
    public CreatorType[] getCreatorTypes(String itemType) {
        return restTemplate.getForObject(buildUri("itemTypeCreatorTypes?itemType=" + itemType, false), CreatorType[].class);
    }
}
