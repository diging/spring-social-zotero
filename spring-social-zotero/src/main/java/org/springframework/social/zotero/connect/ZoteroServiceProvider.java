package org.springframework.social.zotero.connect;

import org.springframework.social.oauth1.AbstractOAuth1ServiceProvider;
import org.springframework.social.zotero.api.Zotero;
import org.springframework.social.zotero.api.impl.ZoteroOAuth1Template;
import org.springframework.social.zotero.api.impl.ZoteroTemplate;

public class ZoteroServiceProvider extends AbstractOAuth1ServiceProvider<Zotero> {
    
    public ZoteroServiceProvider(String consumerKey, String consumerSecret) {
		super(consumerKey, consumerSecret, new ZoteroOAuth1Template(consumerKey, consumerSecret,
		        "https://www.zotero.org/oauth/request",
		        "https://www.zotero.org/oauth/authorize",
		        "https://www.zotero.org/oauth/access"
			));
	}

	public Zotero getApi(String accessToken, String secret) {
	    return new ZoteroTemplate(getConsumerKey(), getConsumerSecret(), accessToken, secret, "https://api.zotero.org/", ((ZoteroOAuth1Template)getOAuthOperations()).getUserId());
	}

}
