package com.upgrad.quora.api.constants;

public enum TokenPrefixes {
    BearerToken("Bearer"), BasicToken("Basic");

    final String tokenPrefixes;

    TokenPrefixes(final String tokenPrefixes) {
        this.tokenPrefixes = tokenPrefixes;
    }

    public String getTokenPrefixes() {
        return this.tokenPrefixes;
    }
}
