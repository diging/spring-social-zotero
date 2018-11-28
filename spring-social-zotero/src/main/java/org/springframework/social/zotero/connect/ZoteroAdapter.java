package org.springframework.social.zotero.connect;

import org.springframework.social.ApiException;
import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.ConnectionValues;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.UserProfileBuilder;
import org.springframework.social.zotero.api.Zotero;

public class ZoteroAdapter implements ApiAdapter<Zotero> {

    @Override
    public boolean test(Zotero api) {
        try {
            api.getItemsOperations().getItemsInfo();
            return true;
        } catch (ApiException e) {
            return false;
        }
    }

    @Override
    public void setConnectionValues(Zotero api, ConnectionValues values) {
        values.setProviderUserId(api.getUserId());
        values.setDisplayName("@" + api.getUserId());
        values.setProfileUrl(null);
        values.setImageUrl(null);
    }

    @Override
    public UserProfile fetchUserProfile(Zotero api) {
        return new UserProfileBuilder().setName("test")
                .setUsername(api.getUserId()).setEmail("email")
                .setFirstName("first name")
                .setLastName("last name").build();
    }

    @Override
    public void updateStatus(Zotero api, String message) {
        // TODO Auto-generated method stub

    }

}