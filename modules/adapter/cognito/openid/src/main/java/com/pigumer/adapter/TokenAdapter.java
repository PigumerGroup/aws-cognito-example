package com.pigumer.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public interface TokenAdapter {
    URI getTokenUri();
    String getClientId();
    String getRedirectUri();
    Logger getLogger();
    String getClientSecret();

    default TokenResponse parseTokenResponse(HttpResponse response) {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream is = response.getEntity().getContent()) {
            return mapper.readValue(is, TokenResponse.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    default String validateAuthorizationCode(String code) {
        Logger logger = getLogger();

        HttpPost post = new HttpPost(getTokenUri());

        String authorization = "Basic " + Base64.getEncoder().encodeToString((getClientId() + ":" + getClientSecret()).getBytes(StandardCharsets.UTF_8));
        post.setHeader("Authorization", authorization);

        String grantType = "authorization_code";
        List<NameValuePair> form = new ArrayList<>();
        form.add(new BasicNameValuePair("grant_type", grantType));
        form.add(new BasicNameValuePair("code", code));
        form.add(new BasicNameValuePair("client_id", getClientId()));
        form.add(new BasicNameValuePair("redirect_uri", getRedirectUri()));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form, StandardCharsets.UTF_8);
        post.setEntity(entity);

        logger.info("post: " + post.toString());
        for (Header header : post.getAllHeaders()) {
            logger.info("post: " + header.toString());
        }
        logger.info("post: " + post.getEntity().toString());

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            try (CloseableHttpResponse response = client.execute(post)) {
                logger.info("response: " + response.toString());
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    TokenResponse tokenResponse = parseTokenResponse(response);

                    ObjectMapper mapper = new ObjectMapper();
                    logger.info(mapper.writeValueAsString(tokenResponse));

                    logger.info("AccessToken: " + tokenResponse.getAccessToken());

                    return tokenResponse.getIdToken();
                }
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
