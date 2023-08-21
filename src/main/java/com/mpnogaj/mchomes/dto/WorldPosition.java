package com.mpnogaj.mchomes.dto;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class WorldPosition {
    public String worldString;
    public double x, y, z;
    public float yaw, pitch;

    public WorldPosition(ServerWorld world, double x, double y, double z, float yaw, float pitch) {
        this.worldString = world.getRegistryKey().getValue().toString();
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public WorldPosition(ServerWorld world, Vec3d pos, float yaw, float pitch) {
        this(world, pos.x, pos.y, pos.z, yaw, pitch);
    }

    public WorldPosition(PlayerEntity player) {
        this((ServerWorld) player.getWorld(), player.getPos(), player.getYaw(), player.getPitch());
    }

    public ServerWorld getServerWorld(MinecraftServer server) {
        return  server.getWorld(RegistryKey.of(RegistryKeys.WORLD, new Identifier(this.worldString)));
    }

    @Override
    public String toString() {
        return "WorldPosition {world=%s, x=%f, y=%f, z=%f, yaw=%f, pitch=%f"
                .formatted(worldString, x, y, z, yaw, pitch);
    }
}