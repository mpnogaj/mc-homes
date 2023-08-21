package com.mpnogaj.mchomes.dto;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PlayerDefaults {
    public UUID playerUUID;
    @Nullable
    public String defaultHome;
    @Nullable
    public String defaultWaypoint;

    public PlayerDefaults(UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.defaultWaypoint = null;
        this.defaultHome = null;
    }

    public PlayerDefaults withDefaultHome(String name) {
        this.defaultHome = name;
        return this;
    }


    public PlayerDefaults withDefaultWaypoint(String name) {
        this.defaultWaypoint = name;
        return this;
    }
}
