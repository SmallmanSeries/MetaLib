package com.smallmanseries.metalib.network.handler;

import com.smallmanseries.metalib.api.MetaInfo;
import com.smallmanseries.metalib.api.MetaPopup;
import com.smallmanseries.metalib.network.packet.MetaInfoPacket;
import com.smallmanseries.metalib.network.packet.MetaPopupPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class ClientHandler {
    public static void handleInfo(MetaInfoPacket packet) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        switch (packet.infoType()) {
            case SYS_USERNAME -> player.sendSystemMessage(MetaInfo.getSystemUsername());
            case SYS_TIME -> player.sendSystemMessage(MetaInfo.getSystemTime("HH:mm:ss"));
            case IP_ADDRESS -> {
                Component ip = MetaInfo.getLocalIPAddress();
                player.sendSystemMessage(Objects.requireNonNullElseGet(ip, () -> Component.literal("Unknown Address")));
            }
            case OS_NAME -> player.sendSystemMessage(MetaInfo.getOSName());
        }
    }

    public static void handlePopup(MetaPopupPacket packet) {
        CompletableFuture.runAsync(() -> {
            if (packet.withStack() && packet.stackTrace() != null) {
                MetaPopup.showPopup(packet.title(), packet.message(), packet.dialogType(), packet.stackTrace());
            } else {
                MetaPopup.showPopup(packet.title(), packet.message(), packet.dialogType());
            }
        });
    }
}
