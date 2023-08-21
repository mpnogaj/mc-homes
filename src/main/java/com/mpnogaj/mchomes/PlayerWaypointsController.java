package com.mpnogaj.mchomes;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mpnogaj.mchomes.data.ListDataController;
import com.mpnogaj.mchomes.dto.PlayerWaypoints;
import com.mpnogaj.mchomes.dto.Waypoint;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PlayerWaypointsController extends ListDataController<PlayerWaypoints> implements SuggestionProvider<ServerCommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) throws CommandSyntaxException {
        final var player = ctx.getSource().getPlayerOrThrow();
        final var playerUUID = player.getUuid();
        final var start = builder.getRemainingLowerCase();
        data.stream()
                .filter(x -> x.getPlayerUuid().equals(playerUUID))
                .flatMap(x -> x.getHomes().stream().map(Waypoint::getName))
                .sorted(String::compareToIgnoreCase)
                .filter(x -> x.toLowerCase().startsWith(start))
                .forEach(builder::suggest);
        return builder.buildFuture();
    }

    @Override
    public List<PlayerWaypoints> defaultData() {
        return new ArrayList<PlayerWaypoints>();
    }

    @Override
    public String getFilename() {
        return "PlayerHomesData";
    }

    @Override
    public Class<PlayerWaypoints[]> getArrayClassType() {
        return PlayerWaypoints[].class;
    }

    enum AddHomeResult {
        SUCCESS,
        NAME_TAKEN,
    }

    List<Waypoint> findPlayerHomes(PlayerEntity player) {
        final var uuid = player.getUuid();

        for (final var playerWithHomes : data) {
            final var playerUuid = playerWithHomes.getPlayerUuid();
            if (playerUuid.equals(uuid)) {
                return playerWithHomes.getHomes();
            }
        }

        return List.of();
    }

    Waypoint findHome(PlayerEntity player, String name) {
        for (Waypoint waypoint : findPlayerHomes(player)) {
            final var homeName = waypoint.getName();
            if (homeName.equals(name)) {
                return waypoint;
            }
        }

        return null;
    }

    boolean removeHome(PlayerEntity player, String name) {
        final var playerUuid = player.getUuid();
        return data
                .stream()
                .filter(v -> v.getPlayerUuid().equals(playerUuid))
                .findAny()
                .map(v -> v.waypoints.removeIf(waypoint -> waypoint.name.equals(name)))
                .orElse(false);
    }

    AddHomeResult addHome(PlayerEntity player, Waypoint waypoint) {
        final var uuid = player.getUuid();
        final var homes = findPlayerHomes(player);

        if (homes.stream().map(Waypoint::getName).anyMatch(v -> v.equals(waypoint.getName()))) {
            return AddHomeResult.NAME_TAKEN;
        }

        if (data.stream().noneMatch(v -> v.getPlayerUuid().equals(uuid))) {
            data.add(new PlayerWaypoints(uuid));
        }

        for (PlayerWaypoints playerWaypoints : data) {
            if (playerWaypoints.getPlayerUuid().equals(uuid)) {
                playerWaypoints.getHomes().add(waypoint);
                write();
                return AddHomeResult.SUCCESS;
            }
        }

        write();
        return AddHomeResult.SUCCESS;
    }
}
