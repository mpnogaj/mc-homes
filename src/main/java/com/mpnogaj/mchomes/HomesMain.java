package com.mpnogaj.mchomes;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mpnogaj.mchomes.dto.Waypoint;
import com.mpnogaj.mchomes.dto.WorldPosition;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class HomesMain implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("mpnogaj_homes");
    private static PlayerWaypointsController playerWaypointsController;
    private static GlobalWaypointsController globalWaypointsController;
    private static PlayerDefaultsController playerDefaultsController;

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        playerWaypointsController = new PlayerWaypointsController();
        globalWaypointsController = new GlobalWaypointsController();
        playerDefaultsController = new PlayerDefaultsController();
        registerCommands();
    }

    private void registerCommands() {
        final var commands = new LiteralArgumentBuilder[]{
                literal("home")
                        .executes(ctx -> {
                            final var player = ctx.getSource().getPlayerOrThrow();
                            final var homeName = playerDefaultsController.getDefaultHomeName(player.getUuid());
                            if(homeName == null) {
                                player.sendMessage(Text.literal(Resources.getDefaultObjectNotSet(Resources.HOME)));
                                return Command.SINGLE_SUCCESS;
                            } else {
                                return this.runHome(ctx, homeName);
                            }
                        })
                        .then(
                        (argument("name", StringArgumentType.string())
                                .suggests(playerWaypointsController)
                                .executes(ctx -> this.runHome(ctx, StringArgumentType.getString(ctx, "name"))))
                ),
                literal("setHome").then(
                        (argument("name", StringArgumentType.string())
                                .executes(this::runSetHome))
                ),
                literal("setDefaultHome").then(
                        (argument("name", StringArgumentType.string())
                                .suggests(playerWaypointsController)
                                .executes(this::runSetDefaultHome))
                ),
                literal("removeHome").then(
                        (argument("name", StringArgumentType.string())
                                .suggests(playerWaypointsController)
                                .executes(this::runRemoveHome))
                ),

                literal("waypoint")
                        .executes(ctx -> {
                            final var player = ctx.getSource().getPlayerOrThrow();
                            final var waypointName = playerDefaultsController.getDefaultWaypointName(player.getUuid());
                            if(waypointName == null) {
                                player.sendMessage(Text.literal(Resources.getDefaultObjectNotSet(Resources.WAYPOINT)));
                                return Command.SINGLE_SUCCESS;
                            } else {
                                return this.runWaypoint(ctx, waypointName);
                            }
                        })
                        .then(
                        argument("name", StringArgumentType.string())
                                .suggests(globalWaypointsController)
                                .executes(ctx -> runWaypoint(ctx, StringArgumentType.getString(ctx, "name")))
                ),
                literal("setWaypoint").then(
                        argument("name", StringArgumentType.string())
                                .executes(this::runSetWaypoint)
                ),
                literal("setDefaultWaypoint").then(
                        (argument("name", StringArgumentType.string())
                                .suggests(globalWaypointsController)
                                .executes(this::runSetDefaultWaypoint))
                ),
                literal("removeWaypoint").then(
                        argument("name", StringArgumentType.string())
                                .suggests(globalWaypointsController)
                                .executes(this::runRemoveWaypoint)
                )
        };

        CommandRegistrationCallback.EVENT.register((dispatcher, commandRegistryAccess, registrationEnvironment) -> {
            Arrays.stream(commands).forEach(dispatcher::register);
        });
    }

    @Nullable
    public static Waypoint getDefaultHome(PlayerEntity player) {
        final var playerDefaults = playerDefaultsController.getPlayerDefaults(player.getUuid());
        if(playerDefaults == null) return null;
        return playerWaypointsController.findHome(player, playerDefaults.defaultHome);
    }

    @Nullable
    public static Waypoint getDefaultWaypoint(ServerPlayerEntity player) {
        final var playerDefaults = playerDefaultsController.getPlayerDefaults(player.getUuid());
        if(playerDefaults == null) return null;
        return globalWaypointsController.findWaypoint(playerDefaults.defaultWaypoint);
    }

    private int runHome(CommandContext<ServerCommandSource> ctx, String homeName) throws CommandSyntaxException {
        final var player = ctx.getSource().getPlayerOrThrow();
        final var server = ctx.getSource().getServer();

        final var home = playerWaypointsController.findHome(player, homeName);

        if(home == null) {
            player.sendMessage(Text.literal(Resources.getObjectNotFound(Resources.HOME, homeName)));
            return Command.SINGLE_SUCCESS;
        }

        home.teleportPlayer(server, player);

        return Command.SINGLE_SUCCESS;
    }

    private int runSetHome(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        final var homeName = StringArgumentType.getString(ctx, "name");
        final var player = ctx.getSource().getPlayerOrThrow();

        final var home = new Waypoint(homeName, new WorldPosition(player));
        final var res = playerWaypointsController.addHome(player, home);


        final var msg = switch (res) {
            case SUCCESS -> Resources.getObjectSuccessfullyAdded(Resources.HOME, homeName);
            case NAME_TAKEN -> Resources.getNameAlreadyTaken(homeName);
        };

        player.sendMessage(Text.literal(msg));


        return Command.SINGLE_SUCCESS;
    }

    private int runSetDefaultHome(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        final var homeName = StringArgumentType.getString(ctx, "name");
        final var player = ctx.getSource().getPlayerOrThrow();
        final var playerUUID = player.getUuid();

        if(playerWaypointsController.findHome(player, homeName) == null) {
            player.sendMessage(Text.literal(Resources.getObjectNotFound(Resources.HOME, homeName)));
            return Command.SINGLE_SUCCESS;
        }

        playerDefaultsController.setDefaultHomeName(playerUUID, homeName);
        return Command.SINGLE_SUCCESS;
    }

    private int runRemoveHome(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        final var homeName = StringArgumentType.getString(ctx, "name");
        final var player = ctx.getSource().getPlayerOrThrow();
        final var playerUUID = player.getUuid();
        final var res = playerWaypointsController.removeHome(player, homeName);


        final var msg = res
                ? Resources.getObjectSuccessfullyRemoved(Resources.HOME, homeName)
                : Resources.getObjectNotFound(Resources.HOME, homeName);

        final var defaultHome = playerDefaultsController.getDefaultHomeName(playerUUID);
        if(defaultHome != null && defaultHome.equals(homeName)) {
            playerDefaultsController.removeDefaultHomeName(playerUUID);
        }

        player.sendMessage(Text.literal(msg));

        return Command.SINGLE_SUCCESS;
    }

    private int runWaypoint(CommandContext<ServerCommandSource> ctx, String waypointName) throws CommandSyntaxException {
        final var player = ctx.getSource().getPlayerOrThrow();
        final var server = ctx.getSource().getServer();

        final var waypoint = globalWaypointsController.findWaypoint(waypointName);

        if(waypoint == null) {
            player.sendMessage(Text.of(Resources.getObjectNotFound(Resources.WAYPOINT, waypointName)));
            return Command.SINGLE_SUCCESS;
        }
        waypoint.teleportPlayer(server, player);

        return Command.SINGLE_SUCCESS;
    }

    private int runSetWaypoint(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        final var waypointName = StringArgumentType.getString(ctx, "name");
        final var player = ctx.getSource().getPlayerOrThrow();

        final var waypoint = new Waypoint(waypointName, new WorldPosition(player));
        final var res = globalWaypointsController.addWaypoint(waypoint);

        final var msg = switch (res) {
            case SUCCESS -> Resources.getObjectSuccessfullyAdded(Resources.WAYPOINT, waypointName);
            case NAME_TAKEN -> Resources.getNameAlreadyTaken(waypointName);
        };

        player.sendMessage(Text.literal(msg));

        return Command.SINGLE_SUCCESS;
    }

    private int runSetDefaultWaypoint(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        final var waypointName = StringArgumentType.getString(ctx, "name");
        final var player = ctx.getSource().getPlayerOrThrow();
        final var playerUUID = player.getUuid();


        if(globalWaypointsController.findWaypoint(waypointName) == null) {
            player.sendMessage(Text.literal(Resources.getObjectNotFound(Resources.WAYPOINT, waypointName)));
            return Command.SINGLE_SUCCESS;
        }

        playerDefaultsController.setDefaultWaypointName(playerUUID, waypointName);
        return Command.SINGLE_SUCCESS;
    }

    private int runRemoveWaypoint(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        final var waypointName = StringArgumentType.getString(ctx, "name");
        final var player = ctx.getSource().getPlayerOrThrow();
        final var playerUUID = player.getUuid();
        final var res = globalWaypointsController.removeWaypoint(waypointName);

        final var msg = res
                ? Resources.getObjectSuccessfullyRemoved(Resources.WAYPOINT, waypointName)
                : Resources.getObjectNotFound(Resources.WAYPOINT, waypointName);

        final var defaultWaypoint = playerDefaultsController.getDefaultWaypointName(playerUUID);
        if(defaultWaypoint != null && defaultWaypoint.equals(waypointName)) {
            playerDefaultsController.removeDefaultWaypointName(playerUUID);
        }

        player.sendMessage(Text.literal(msg));

        return Command.SINGLE_SUCCESS;
    }
}