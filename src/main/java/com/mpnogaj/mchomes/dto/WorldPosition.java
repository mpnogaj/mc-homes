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

    public WorldPosition(ServerWorld world, double x, double y, double z) {
        this.worldString = world.getRegistryKey().getValue().toString();
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public WorldPosition(ServerWorld world, Vec3d pos) {
        this(world, pos.x, pos.y, pos.z);
    }

    public WorldPosition(PlayerEntity player) {
        this((ServerWorld) player.getWorld(), player.getPos());
    }

    public ServerWorld getServerWorld(MinecraftServer server) {
        return  server.getWorld(RegistryKey.of(RegistryKeys.WORLD, new Identifier(this.worldString)));
    }

    @Override
    public String toString() {
        return "TeleportDestination{" +
                "world=" + worldString +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}