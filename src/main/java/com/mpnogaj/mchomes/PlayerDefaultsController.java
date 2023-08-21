package com.mpnogaj.mchomes;

import com.mpnogaj.mchomes.data.ListDataController;
import com.mpnogaj.mchomes.dto.PlayerDefaults;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerDefaultsController extends ListDataController<PlayerDefaults> {
    @Override
    public List<PlayerDefaults> defaultData() {
        return new ArrayList<PlayerDefaults>();
    }

    @Override
    public String getFilename() {
        return "PlayerDefaults";
    }

    @Override
    public Class<PlayerDefaults[]> getArrayClassType() {
        return PlayerDefaults[].class;
    }

    @Nullable
    public PlayerDefaults getPlayerDefaults(UUID playerUUID) {
        return this.data.stream()
                .filter(x -> x.playerUUID.equals(playerUUID))
                .findFirst()
                .orElse(null);
    }

    @Nullable
    public String getDefaultWaypointName(UUID playerUUID) {
        final var playerDefaults = getPlayerDefaults(playerUUID);
        return playerDefaults == null ? null : playerDefaults.defaultWaypoint;
    }

    @Nullable
    public String getDefaultHomeName(UUID playerUUID) {
        final var playerDefaults = getPlayerDefaults(playerUUID);
        return playerDefaults == null ? null : playerDefaults.defaultHome;
    }

    public void setDefaultWaypointName(UUID playerUUID, String name) {
        final var playerDefaults = getPlayerDefaults(playerUUID);
        if(playerDefaults == null) {
            data.add(new PlayerDefaults(playerUUID).withDefaultWaypoint(name));
        } else {
            playerDefaults.defaultWaypoint = name;
        }
        write();
    }

    public void setDefaultHomeName(UUID playerUUID, String name) {
        final var playerDefaults = getPlayerDefaults(playerUUID);
        if(playerDefaults == null) {
            data.add(new PlayerDefaults(playerUUID).withDefaultHome(name));
        } else {
            playerDefaults.defaultHome = name;
        }
        write();
    }

    public void removeDefaultWaypointName(UUID playerUUID) {
        final var playerDefaults = getPlayerDefaults(playerUUID);
        if(playerDefaults != null) {
            playerDefaults.defaultWaypoint = null;
            write();
        }
    }

    public void removeDefaultHomeName(UUID playerUUID) {
        final var playerDefaults = getPlayerDefaults(playerUUID);
        if(playerDefaults != null) {
            playerDefaults.defaultHome = null;
            write();
        }
    }
}
