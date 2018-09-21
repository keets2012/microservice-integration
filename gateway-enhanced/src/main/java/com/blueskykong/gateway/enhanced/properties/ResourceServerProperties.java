package com.blueskykong.gateway.enhanced.properties;

/**
 * @author keets
 * @date 2017/10/30
 */
public class ResourceServerProperties {

    private ResourceServerProperties.Client client = new ResourceServerProperties.Client();

    private ResourceServerProperties.Resource resource = new ResourceServerProperties.Resource();

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public static class Client {
        private String clientId;

        private String clientSecret;

        private String accessTokenUri;

        public String getAccessTokenUri() {
            return accessTokenUri;
        }

        public void setAccessTokenUri(String accessTokenUri) {
            this.accessTokenUri = accessTokenUri;
        }

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public void setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
        }

    }

    public static class Resource {
        private String userInfoUri;

        private String tokenInfoUri;


        public String getTokenInfoUri() {
            return tokenInfoUri;
        }

        public void setTokenInfoUri(String tokenInfoUri) {
            this.tokenInfoUri = tokenInfoUri;
        }

        public String getUserInfoUri() {
            return userInfoUri;
        }

        public void setUserInfoUri(String userInfoUri) {
            this.userInfoUri = userInfoUri;
        }
    }

}
