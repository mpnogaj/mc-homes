package com.mpnogaj.mchomes.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class HomesClientMain implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        final var homeKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.mpnogaj.mchomes.home_keybinding",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F23,
                "category.mpnogaj.mchomes.keybindings"
        ));

        final var waypointKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.mpnogaj.mchomes.waypoint_keybinding",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F24,
                "category.mpnogaj.mchomes.keybindings"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(client.player != null) {
                if(homeKeyBinding.wasPressed()) {
                    final var player = client.player;
                    player.networkHandler.sendChatCommand("home");
                }
                if(waypointKeyBinding.wasPressed()) {
                    final var player = client.player;
                    player.networkHandler.sendChatCommand("waypoint");
                }
            }
        });
    }
}
