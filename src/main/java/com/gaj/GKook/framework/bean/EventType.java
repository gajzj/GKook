package com.gaj.GKook.framework.bean;

import java.util.HashMap;
import java.util.Map;

public enum EventType {
    ADDED_REACTION("added_reaction"),
    DELETED_REACTION("deleted_reaction"),
    UPDATED_MESSAGE("updated_message"),
    DELETED_MESSAGE("deleted_message"),
    ADDED_CHANNEL("added_channel"),
    UPDATED_CHANNEL("updated_channel"),
    DELETED_CHANNEL("deleted_channel"),
    PINNED_MESSAGE("pinned_message"),
    UNPINNED_MESSAGE("unpinned_message"),
    UPDATED_GUILD_MEMBER("updated_guild_member"),
    GUILD_MEMBER_ONLINE("guild_member_online"),
    GUILD_MEMBER_OFFLINE("guild_member_offline"),
    ADDED_ROLE("added_role"),
    DELETED_ROLE("deleted_role"),
    UPDATED_ROLE("updated_role"),
    UPDATED_GUILD("updated_guild"),
    DELETED_GUILD("deleted_guild"),
    ADDED_BLOCK_LIST("added_block_list"),
    DELETED_BLOCK_LIST("deleted_block_list"),
    ADDED_EMOJI("added_emoji"),
    DELETED_EMOJI("deleted_emoji"),
    UPDATED_EMOJI("updated_emoji"),
    UPDATED_PRIVATE_MESSAGE("updated_private_message"),
    DELETED_PRIVATE_MESSAGE("deleted_private_message"),
    PRIVATE_ADDED_REACTION("private_added_reaction"),
    PRIVATE_DELETED_REACTION("private_deleted_reaction"),
    JOINED_GUILD("joined_guild"),
    EXITED_GUILD("exited_guild"),
    JOINED_CHANNEL("joined_channel"),
    EXITED_CHANNEL("exited_channel"),
    MESSAGE_BTN_CLICK("message_btn_click"),
    USER_UPDATED("user_updated"),
    SELF_JOINED_GUILD("self_joined_guild"),
    SELF_EXITED_GUILD("self_exited_guild");

    private static final Map<String, EventType> values = new HashMap<>();

    static {
        for (EventType value : values()) {
            values.put(value.getValue(), value);
        }
    }

    private final String value;

    EventType(String value) {
        this.value = value;
    }

    public static EventType value(String name) {
        return values.get(name);
    }

    public String getValue() {
        return value;
    }
}
