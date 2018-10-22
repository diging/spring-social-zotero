package org.springframework.social.zotero.api;

import java.io.Serializable;
import java.util.List;

public class ItemsInfo implements Serializable  {

    /**
     * 
     */
    private static final long serialVersionUID = -8513586363602674893L;

    private List<Item> items;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
    
}
