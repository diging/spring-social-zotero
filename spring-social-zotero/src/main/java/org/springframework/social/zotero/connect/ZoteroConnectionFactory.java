package org.springframework.social.zotero.connect;
import org.springframework.social.connect.support.OAuth1ConnectionFactory;
import org.springframework.social.zotero.api.Zotero;

public class ZoteroConnectionFactory extends OAuth1ConnectionFactory<Zotero> {
    
     public ZoteroConnectionFactory(String consumerKey, String consumerSecret) {
        super("zotero", new ZoteroServiceProvider(consumerKey, consumerSecret), new ZoteroAdapter());
    }
    
}