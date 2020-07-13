package com.upgrad.quora.service.type;

import java.util.HashMap;
import java.util.Map;

public enum Role {
    Admin("admin"), NonAdmin("nonadmin");
    private final String value;

    Role(final String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }



    //Lookup table
    private static final Map<String, Role> lookup = new HashMap<>();

    //Populate the lookup table on loading time
    static {
        for (Role env : Role.values()) {
            lookup.put(env.getValue(), env);
        }
    }

    //This method can get the role of the user
    public static Role get(String url) {
        return lookup.get(url);
    }
}

