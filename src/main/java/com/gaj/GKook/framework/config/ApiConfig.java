package com.gaj.GKook.framework.config;

import java.util.HashMap;
import java.util.Map;

public class ApiConfig {
    public final static int MAX_TIMES = 100;

    public enum ApiEndpoint {
        API_BASE_URL("https://www.kookapp.cn"),

        // gateway
        GATEWAY_INDEX("/api/v3/gateway/index"),

        // message
        MESSAGE_LIST("/api/v3/message/list"),
        MESSAGE_CREATE("/api/v3/message/create"),
        MESSAGE_DELETE("/api/v3/message/delete"),

        // channel

        // user
        USER_VIEW("/api/v3/user/view");

        private static final Map<String, ApiEndpoint> map = new HashMap<>();

        static {
            for (ApiEndpoint value : values()) {
                map.put(value.getPoint(), value);
            }
        }

        private final String point;

        ApiEndpoint(String route) {
            this.point = route;
        }

        public String getPoint() {
            return point;
        }

        public String toFullUrl() {
            return (this == API_BASE_URL ? "" : API_BASE_URL.getPoint()) + this.getPoint();
        }

        public static ApiEndpoint value(String route) {
            return map.get(route);
        }
    }
}
