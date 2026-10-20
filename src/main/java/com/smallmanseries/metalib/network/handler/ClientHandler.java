package com.smallmanseries.metalib.network.handler;

import com.smallmanseries.metalib.api.*;
import com.smallmanseries.metalib.network.packet.*;
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

    public static void handleFakeCrashReport(FakeCrashReportPacket packet) {
        CompletableFuture.runAsync(() -> {
            MetaCrash.showFakeCrashReport(packet.title(), packet.report(), null);
        });
    }

//    public static void handleFakeCrashImage(FakeCrashImagePacket packet) {
//        CompletableFuture.runAsync(() -> {
//
//        });
//    }

    public static void handleForceCrash(ForceCrashOrQuitPacket packet) {
        if (packet.silent()) {
            MetaCrash.silentQuitAfter(packet.delayTime());
        } else {
            MetaCrash.forceCrash(packet.description(), packet.report());
        }
    }

    public static void handleWindow(MetaWindowPacket packet) {
        switch (packet.op()) {
            case FULLSCREEN -> MetaWindow.setFullscreen();
            case WINDOWED -> MetaWindow.setWindowed(packet.x(), packet.y());
            case MOVE_TO -> MetaWindow.setWindowPos(packet.x(), packet.y());
            case SCALE_TO -> MetaWindow.setWindowSize(packet.x(), packet.y());
            case MOVE_BY -> MetaWindow.moveWindow(packet.x(), packet.y());
            case SCALE_BY -> MetaWindow.scaleWindow(packet.x(), packet.y());
            case CURSOR_MOVE_TO -> MetaWindow.setCursorPos(packet.x(), packet.y());
            case CURSOR_MOVE_BY -> MetaWindow.moveCursor(packet.x(), packet.y());
        }
    }

    public static void handleWindowAnimated(MetaWindowPacket packet) {
        switch (packet.op()) {
            case MOVE_TO -> MetaWindowAnimate.moveTo(packet.x(), packet.y(), packet.duration(), packet.easing());
            case MOVE_BY -> MetaWindowAnimate.moveBy(packet.x(), packet.y(), packet.duration(), packet.easing());
            case SCALE_TO -> MetaWindowAnimate.scaleTo(packet.x(), packet.y(), packet.duration(), packet.easing());
            case SCALE_BY -> MetaWindowAnimate.scaleBy(packet.x(), packet.y(), packet.duration(), packet.easing());
            case MOVE -> MetaWindowAnimate.startDrift(packet.x(), packet.y(), packet.duration(), packet.easing());
            case SCALE -> MetaWindowAnimate.startScale(packet.x(), packet.y(), packet.duration(), packet.easing());
            case SHAKE -> MetaWindowAnimate.shakeWindow(packet.duration(), packet.x());
            case FIX -> MetaWindowAnimate.fix(packet.duration());
            case CURSOR_MOVE_TO -> MetaWindowAnimate.moveCursorTo(packet.x(), packet.y(), packet.duration(), packet.easing());
            case CURSOR_MOVE_BY -> MetaWindowAnimate.moveCursorBy(packet.x(), packet.y(), packet.duration(), packet.easing());
            case CURSOR_MOVE -> MetaWindowAnimate.startCursorDrift(packet.x(), packet.y(), packet.duration(), packet.easing());
            case CURSOR_FIX -> MetaWindowAnimate.fixCursor(packet.duration());
            case SHAKE_CURSOR -> MetaWindowAnimate.shakeCursor(packet.duration(), packet.x());
            case STOP -> MetaWindowAnimate.stopAllAnimations();
            case PAUSE -> MetaWindowAnimate.pauseAnimations();
            case RESUME -> MetaWindowAnimate.resumeAnimations();
        }
    }

    public static void handleWindowSetTitle(MetaWindowSetTitlePacket packet) {
        MetaWindow.setWindowTitle(packet.title());
    }
}
