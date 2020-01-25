package com.pigumer.adapter;

import org.slf4j.Logger;

import java.net.URI;

public interface TokenAdapter {
    URI getTokenUri();
    String getClientId();
    String getRedirectUri();
    Logger getLogger();
    String getClientSecret();

    default String validateAuthorizationCode(String code) {
        return null;
    }
}
