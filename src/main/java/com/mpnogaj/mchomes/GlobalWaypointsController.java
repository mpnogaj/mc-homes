package com.mpnogaj.mchomes;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mpnogaj.mchomes.data.ListDataController;
import com.mpnogaj.mchomes.dto.Waypoint;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GlobalWaypointsController extends ListDataController<Waypoint> implements SuggestionProvider<ServerCommandSource> {
    @Override
    public List<Waypoint> defaultData() {
        return new ArrayList<Waypoint>();
    }

    @Override
    public String getFilename() {
        return "GlobalWaypointsData";
    }

    @Override
    public Class<Waypoint[]> getArrayClassType() {
        return Waypoint[].class;
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) throws CommandSyntaxException {
        final var start = builder.getRemainingLowerCase();
        data.stream()
                .map(Waypoint::getName)
                .filter(x -> x.toLowerCase().startsWith(start))
                .forEach(builder::suggest);
        return builder.buildFuture();
    }

    enum AddWaypointResult {
        SUCCESS,
        NAME_TAKEN,
    }

    @Nullable
    Waypoint findWaypoint(String name) {
        for (final var waypoint : data) {
            if(waypoint.name.equals(name)) {
                return waypoint;
            }
        }
        return null;
    }

    boolean removeWaypoint(String name) {
        final var prevLen = data.size();
        data.removeIf(x -> x.name.equals(name));

        write();
        return data.size() < prevLen;
    }

    AddWaypointResult addWaypoint(Waypoint waypoint) {

        if (data.stream().map(Waypoint::getName).anyMatch(v -> v.equals(waypoint.getName()))) {
            return AddWaypointResult.NAME_TAKEN;
        }

        data.add(waypoint);

        write();
        return AddWaypointResult.SUCCESS;
    }
}
