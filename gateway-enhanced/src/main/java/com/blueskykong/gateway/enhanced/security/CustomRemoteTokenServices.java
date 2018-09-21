package com.blueskykong.gateway.enhanced.security;


import com.blueskykong.gateway.enhanced.exception.ErrorCode;
import com.blueskykong.gateway.enhanced.exception.ServerException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author keets
 * @date 2018/9/20
 */
public class CustomRemoteTokenServices {

    private LoadBalancerClient loadBalancerClient;

    protected final Log logger = LogFactory.getLog(getClass());

    private RestTemplate restTemplate;

    private String checkTokenEndpointUrl;

    private String clientId;

    private String clientSecret;

    private String tokenName = "token";

    private final String ERROR = "error";

    public CustomRemoteTokenServices(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            // Ignore 400
            public void handleError(ClientHttpResponse response) throws IOException {
                if (response.getRawStatusCode() != 400) {
                    super.handleError(response);
                }
            }
        });
    }

    public void setCheckTokenEndpointUrl(String checkTokenEndpointUrl) {
        this.checkTokenEndpointUrl = checkTokenEndpointUrl;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

    public void loadAuthentication(String accessToken) {

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

        formData.add(tokenName, accessToken);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", getAuthorizationHeader(clientId, clientSecret));

        ServiceInstance serviceInstance = loadBalancerClient.choose("auth");
        if (serviceInstance == null) {
            throw new RuntimeException("Failed to choose an auth instance.");
        }

        Map<String, Object> map = postForMap(serviceInstance.getUri().toString() + checkTokenEndpointUrl, formData, headers);

        if (map.containsKey(ERROR)) {
            logger.debug("check_token returned error: " + map.get(ERROR));
            Object status = map.get("status");
            if (status != null && status.equals(HttpStatus.BAD_REQUEST.value())) {
                throw new ServerException(HttpStatus.BAD_REQUEST, new ErrorCode(400, "bad request!", "pls check your params!"));
            }

            HttpStatus code = (HttpStatus) map.get(ERROR);
            if (code == HttpStatus.UNAUTHORIZED) {
                //TODO:sendErrorFilter findZuulException会查看FilterRuntimeException中zuulException的code和message
                throw new ServerException(HttpStatus.BAD_REQUEST, new ErrorCode(401, "UNAUTHORIZED", "your identity id illegal!"));
            } else {
                throw new ServerException(HttpStatus.BAD_REQUEST, new ErrorCode(403, "not permitted!", "you do not have permission to operate!"));
            }
        }

        Assert.state(map.containsKey("client_id"), "Client id must be present in response from auth server");
        return;
    }

    public void setLoadBalancerClient(LoadBalancerClient loadBalancerClient) {
        this.loadBalancerClient = loadBalancerClient;
    }

    private String getAuthorizationHeader(String clientId, String clientSecret) {
        String creds = String.format("%s:%s", clientId, clientSecret);
        try {
            return "Basic " + new String(Base64.encode(creds.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Could not convert String");
        }
    }

    private Map<String, Object> postForMap(String path, MultiValueMap<String, String> formData, HttpHeaders headers) {
        if (headers.getContentType() == null) {
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        }
        @SuppressWarnings("rawtypes")
        Map map = new HashMap();
        try {
            map = restTemplate.exchange(path, HttpMethod.POST,
                    new HttpEntity<MultiValueMap<String, String>>(formData, headers), Map.class).getBody();
        } catch (HttpClientErrorException e1) {
            logger.error("catch token exception when check token!", e1);
            map.put(ERROR, e1.getStatusCode());

        } catch (HttpServerErrorException e2) {
            logger.error("catch no permission exception when check token!", e2);
            map.put(ERROR, e2.getStatusCode());

        } catch (Exception e) {
            logger.error("catch common exception when check token!", e);
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> result = map;
        return result;
    }

}
