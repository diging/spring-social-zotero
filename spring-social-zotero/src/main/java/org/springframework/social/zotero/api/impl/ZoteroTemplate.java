package org.springframework.social.zotero.api.impl;

import java.io.IOException;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.social.oauth1.AbstractOAuth1ApiBinding;
import org.springframework.social.support.HttpRequestDecorator;
import org.springframework.social.zotero.api.GroupsOperations;
import org.springframework.social.zotero.api.ItemsOperations;
import org.springframework.social.zotero.api.Zotero;
import org.springframework.web.client.RestTemplate;

public class ZoteroTemplate extends AbstractOAuth1ApiBinding implements Zotero {
    
    private String providerUrl;
    private ItemsOperations itemsOperations;
    private GroupsOperations groupOperations;
    private String userId;

    public ZoteroTemplate(String consumerKey, String consumerSecret, String accessToken, String secret, String providerUrl, String userId) {
        super(consumerKey, consumerSecret, accessToken, secret);
        this.providerUrl = providerUrl;
        this.userId = userId;
        
        RestTemplate template = getRestTemplate();
        // if we already have a token, let's use our own interceptor
        if (accessToken != null && !accessToken.isEmpty()) {
            ClientHttpRequestInterceptor existingInterceptor = null;
            for (ClientHttpRequestInterceptor interceptor : template.getInterceptors()) {
               if (interceptor instanceof ClientHttpRequestInterceptor) {
                   existingInterceptor = interceptor;
               }
            };
            template.getInterceptors().remove(existingInterceptor);
            
            template.getInterceptors().add(new ClientHttpRequestInterceptor() {
                
                @Override
                public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
                        throws IOException {
                    HttpRequestDecorator protectedResourceRequest = new HttpRequestDecorator(request);
                    protectedResourceRequest.getHeaders().add("Authorization", "Bearer " + accessToken);
                    return execution.execute(protectedResourceRequest, body);
                }
            });
        }
        
        initSubApis();
    }
    
    private void initSubApis() {
        this.itemsOperations = new ItemsTemplate(getRestTemplate(), isAuthorized(), providerUrl, userId);
        this.groupOperations = new GroupsTemplate(getRestTemplate(), isAuthorized(), providerUrl, userId);
    }

    @Override
    public ItemsOperations getItemsOperations() {
        return itemsOperations;
    }
    
    @Override
    public GroupsOperations getGroupsOperations() {
        return groupOperations;
    }
    
    @Override
    public String getUserId() {
        return userId; 
    }
    
    @Override
    public void setUserId(String userId) {
        this.userId = userId;
        this.itemsOperations.setUserId(userId);
        this.groupOperations.setUserId(userId);
    }
}
