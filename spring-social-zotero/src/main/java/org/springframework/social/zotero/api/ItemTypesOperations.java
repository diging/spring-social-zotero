package org.springframework.social.zotero.api;

public interface ItemTypesOperations {

    void setUserId(String userId);

    FieldInfo[] getFields(String itemType);

    CreatorType[] getCreatorTypes(String itemType);
}