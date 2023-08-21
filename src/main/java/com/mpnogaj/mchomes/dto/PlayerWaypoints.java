package com.mpnogaj.mchomes.dto;

import java.util.ArrayList;
import java.util.UUID;

public class PlayerWaypoints {
    public UUID playerUuid;
    public ArrayList<Waypoint> waypoints;

    public PlayerWaypoints(UUID playerUuid) {
        this.playerUuid = playerUuid;
        waypoints = new ArrayList<>();
    }

    public UUID getPlayerUuid(){
        return playerUuid;
    }

    public ArrayList<Waypoint> getHomes(){
        return waypoints;
    }
}
