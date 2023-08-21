package com.mpnogaj.mchomes.dto;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

import java.util.HashSet;

public class Waypoint {
    public final String name;
    public final WorldPosition worldPosition;

    public Waypoint(String name, WorldPosition worldPosition) {
        this.name = name;
        this.worldPosition = worldPosition;
    }

    public String getName() {
        return this.name;
    }

    public WorldPosition getWorldPosition() {
        return this.worldPosition;
    }

    public void teleportPlayer(MinecraftServer server, PlayerEntity player) {
        final var world = worldPosition.getServerWorld(server);

        player.teleport(world,
                worldPosition.x,
                worldPosition.y,
                worldPosition.z,
                new HashSet<>(),
                worldPosition.yaw,
                worldPosition.pitch);

        world.playSound(null,
                worldPosition.x,
                worldPosition.y,
                worldPosition.z,
                SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT,
                SoundCategory.PLAYERS, 1.0f, 1.0f);
        player.playSound(SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, 1.0f, 1.0f);
    }
}
