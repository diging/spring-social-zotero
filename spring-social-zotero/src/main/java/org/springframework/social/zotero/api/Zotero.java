package org.springframework.social.zotero.api;

import org.springframework.social.ApiBinding;

public interface Zotero extends ApiBinding {

    ItemsOperations getItemsOperations();

    String getUserId();
}
