package com.smallmanseries.metalib.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.smallmanseries.metalib.api.MetaPopup;
import com.smallmanseries.metalib.client.fakecrash.FakeCrashReportBuilder;
import com.smallmanseries.metalib.client.window.Easing;
import com.smallmanseries.metalib.network.packet.*;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.command.EnumArgument;

import java.util.Collection;

public class MetaLibCommand {

    /* /metalib <subCommand> <target(s)> [operation]... */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(
                Commands.literal("metalib").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .then(info())
                        .then(popup())
                        .then(crash())
                        .then(window())
        );
    }

    /* /metalib info <targets> <type> */ //Todo meta text component
    private static LiteralArgumentBuilder<CommandSourceStack> info() {
        return Commands.literal("info")
                .then(Commands.argument("targets", EntityArgument.players())
                        .then(Commands.argument("type", EnumArgument.enumArgument(MetaInfoPacket.InfoType.class))
                                .executes(context -> {
                                    Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
                                    MetaInfoPacket.InfoType infoType = context.getArgument("type", MetaInfoPacket.InfoType.class);
                                    for (ServerPlayer player : players) {
                                        PacketDistributor.sendToPlayer(player, new MetaInfoPacket(infoType));
                                    }
                                    return players.size();
                                })
                        )
                );
    }

    /* /metalib popup <targets> <description> <message> <type> [<stack>] */
    private static LiteralArgumentBuilder<CommandSourceStack> popup() {
        return Commands.literal("popup")
                .then(Commands.argument("targets", EntityArgument.players())
                        .then(Commands.argument("description", StringArgumentType.string())
                                .then(Commands.argument("message", StringArgumentType.string())
                                        .then(Commands.argument("type", EnumArgument.enumArgument(MetaPopup.Type.class))
                                                .executes(context -> popupOperations(context,
                                                        null,
                                                        null
                                                ))
                                                .then(Commands.literal("run")
                                                        .then(Commands.argument("command", StringArgumentType.string())
                                                                .executes(context -> popupOperations(context,
                                                                        null,
                                                                        StringArgumentType.getString(context, "command")
                                                                ))
                                                        )
                                                )
                                                .then(Commands.argument("stack", StringArgumentType.string())
                                                        .executes(context -> popupOperations(context,
                                                                StringArgumentType.getString(context, "stack"),
                                                                null
                                                        ))
                                                        .then(Commands.literal("run")
                                                                .then(Commands.argument("command", StringArgumentType.string())
                                                                        .executes(context -> popupOperations(context,
                                                                                StringArgumentType.getString(context, "stack"),
                                                                                StringArgumentType.getString(context, "command")
                                                                        ))
                                                                )
                                                        )
                                                )

                                        )
                                )
                        )
                );
    }

    private static int popupOperations(CommandContext<CommandSourceStack> context, String stack, String runAfter) throws CommandSyntaxException {
        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
        String title = StringArgumentType.getString(context, "description");
        String msg = StringArgumentType.getString(context, "message");
        MetaPopup.Type type = context.getArgument("type", MetaPopup.Type.class);
        for (ServerPlayer player : players) {
            PacketDistributor.sendToPlayer(player, new MetaPopupPacket(title, msg, type, stack != null, stack == null ? "" : stack));
        }
        context.getSource().sendSuccess(() -> Component.literal("Popup(s) have been shown to " + players.size() + " players"), true);
        return players.size();
    }

    /* /metalib crash <targets> fake report <report> [<addinfo>] */
    /* /metalib crash <targets> fake image <path> */
    /* /metalib crash <targets> real <description> <report> */
    /* /metalib crash <targets> silent */
    private static LiteralArgumentBuilder<CommandSourceStack> crash() {
        return Commands.literal("crash")
                .then(Commands.argument("targets", EntityArgument.players())
                        .then(Commands.literal("fake")
                                        .then(Commands.literal("report")
                                                .then(Commands.argument("report", StringArgumentType.string())
                                                        .executes(context -> {
                                                            Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
                                                            String report = StringArgumentType.getString(context, "report");
                                                            for (ServerPlayer player : players) {
                                                                PacketDistributor.sendToPlayer(player, new FakeCrashReportPacket("Crash Report", report));
                                                            }
                                                            context.getSource().sendSuccess(() -> Component.literal("Fake crash report(s) have been shown to " + players.size() + " players"), true);
                                                            return players.size();
                                                        })
                                                        .then(Commands.literal("addinfo")
                                                                .executes(context -> {
                                                                    Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
                                                                    String report = StringArgumentType.getString(context, "report");
                                                                    String newReport = FakeCrashReportBuilder.classicReport("[your_email]", report);

                                                                    for (ServerPlayer player : players) {
                                                                        PacketDistributor.sendToPlayer(player, new FakeCrashReportPacket("Crash Report", newReport));
                                                                    }
                                                                    context.getSource().sendSuccess(() -> Component.literal("Fake crash report(s) have been shown to " + players.size() + " players"), true);
                                                                    return players.size();
                                                                })
                                                        )
                                                )
                                        )
                                //.then(Commands.literal("image"))
                        )
                        .then(Commands.literal("real")
                                .then(Commands.argument("description", StringArgumentType.string())
                                        .then(Commands.argument("report", StringArgumentType.string())
                                                .executes(context -> {
                                                    Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
                                                    String description = StringArgumentType.getString(context, "description");
                                                    String report = StringArgumentType.getString(context, "report");
                                                    for (ServerPlayer player : players) {
                                                        PacketDistributor.sendToPlayer(player, new ForceCrashOrQuitPacket(description, report, 0, false));
                                                    }
                                                    context.getSource().sendSuccess(() -> Component.literal("Forced " + players.size() + " players' client to crash"), true);
                                                    return players.size();
                                                })
                                        )
                                )
                        )
                        .then(Commands.literal("silent")
                                .executes(context -> {
                                    Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
                                    for (ServerPlayer player : players) {
                                        PacketDistributor.sendToPlayer(player, new ForceCrashOrQuitPacket("", "", 0, true));
                                    }
                                    context.getSource().sendSuccess(() -> Component.literal("Forced " + players.size() + " players' client to quit"), true);
                                    return players.size();
                                })
                                .then(Commands.argument("delay", IntegerArgumentType.integer(0))
                                        .executes(context -> {
                                            Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
                                            int delay = IntegerArgumentType.getInteger(context, "delay");
                                            for (ServerPlayer player : players) {
                                                PacketDistributor.sendToPlayer(player, new ForceCrashOrQuitPacket("", "", delay, true));
                                            }
                                            context.getSource().sendSuccess(() -> Component.literal("Forced " + players.size() + " players' client to quit in " + delay + "ms"), true);
                                            return players.size();
                                        })
                                )
                        )
                );
    }

    /* /metalib window <targets> fullscreen */
    /* /metalib window <targets> windowed <width> <height> */
    /* /metalib window <targets> animate <operation> <x> <y> [duration] [easing] */
    /* /metalib window <targets> animate <stop|pause|resume> */
    /* /metalib window <targets> cursor <move|setpos> <x> <y> */
    private static LiteralArgumentBuilder<CommandSourceStack> window() {
        return Commands.literal("window")
                .then(Commands.argument("targets", EntityArgument.players())
                        .then(Commands.literal("fullscreen")
                                .executes(context -> {
                                    int size = windowOperations(context, MetaWindowPacket.Operation.FULLSCREEN, 0, 0, 0, Easing.LINEAR);
                                    context.getSource().sendSuccess(() -> Component.literal("Forced fullscreen for " + size + " players"), true);
                                    return size;
                                })
                        )
                        .then(Commands.literal("windowed")
                                .then(Commands.argument("width", IntegerArgumentType.integer(0))
                                        .then(Commands.argument("height", IntegerArgumentType.integer(0))
                                                .executes(context -> {
                                                    int size = windowOperations(context,
                                                            MetaWindowPacket.Operation.WINDOWED,
                                                            IntegerArgumentType.getInteger(context, "width"),
                                                            IntegerArgumentType.getInteger(context, "height"),
                                                            0,
                                                            Easing.LINEAR);
                                                    context.getSource().sendSuccess(() -> Component.literal("Forced Windowed for " + size + " players"), true);
                                                    return size;
                                                })
                                        )
                                )
                        )
                        .then(Commands.literal("animate")
                                .then(Commands.argument("operation", EnumArgument.enumArgument(MetaWindowPacket.WindAnimOperation.class))
                                        .then(Commands.argument("x", IntegerArgumentType.integer())
                                                .then(Commands.argument("y", IntegerArgumentType.integer())
                                                        .executes(context -> windowOperations(context,
                                                                context.getArgument("operation", MetaWindowPacket.WindAnimOperation.class),
                                                                IntegerArgumentType.getInteger(context, "x"),
                                                                IntegerArgumentType.getInteger(context, "y"),
                                                                0,
                                                                Easing.LINEAR))
                                                        .then(Commands.argument("duration", IntegerArgumentType.integer())
                                                                .executes(context -> windowOperations(context,
                                                                        context.getArgument("operation", MetaWindowPacket.WindAnimOperation.class),
                                                                        IntegerArgumentType.getInteger(context, "x"),
                                                                        IntegerArgumentType.getInteger(context, "y"),
                                                                        IntegerArgumentType.getInteger(context, "duration"),
                                                                        Easing.LINEAR))
                                                                .then(Commands.argument("easing", EnumArgument.enumArgument(Easing.class))
                                                                        .executes(context -> windowOperations(context,
                                                                                context.getArgument("operation", MetaWindowPacket.WindAnimOperation.class),
                                                                                IntegerArgumentType.getInteger(context, "x"),
                                                                                IntegerArgumentType.getInteger(context, "y"),
                                                                                IntegerArgumentType.getInteger(context, "duration"),
                                                                                context.getArgument("easing", Easing.class)))
                                                                )
                                                        )
                                                )
                                        )
                                )
                                .then(Commands.literal("shake")
                                        .then(Commands.argument("duration", IntegerArgumentType.integer())
                                                .then(Commands.argument("intensity", IntegerArgumentType.integer())
                                                        .executes(context -> windowOperations(context,
                                                                MetaWindowPacket.Operation.SHAKE,
                                                                IntegerArgumentType.getInteger(context, "intensity"),
                                                                0,
                                                                IntegerArgumentType.getInteger(context, "duration"),
                                                                Easing.LINEAR))
                                                )
                                        )
                                )
                                .then(Commands.literal("fix")
                                        .then(Commands.argument("duration", IntegerArgumentType.integer())
                                                .executes(context -> windowOperations(context,
                                                        MetaWindowPacket.Operation.FIX,
                                                        0,
                                                        0,
                                                        IntegerArgumentType.getInteger(context, "duration"),
                                                        Easing.LINEAR))
                                        )
                                )
                        )
                        .then(Commands.literal("cursor")
                                .then(Commands.argument("operation", EnumArgument.enumArgument(MetaWindowPacket.CursorOperation.class))
                                        .then(Commands.argument("x", IntegerArgumentType.integer())
                                                .then(Commands.argument("y", IntegerArgumentType.integer())
                                                        .executes(context -> cursorOperations(context,
                                                                context.getArgument("operation", MetaWindowPacket.CursorOperation.class),
                                                                IntegerArgumentType.getInteger(context, "x"),
                                                                IntegerArgumentType.getInteger(context, "y"),
                                                                0,
                                                                Easing.LINEAR))
                                                        .then(Commands.argument("duration", IntegerArgumentType.integer())
                                                                .executes(context -> cursorOperations(context,
                                                                        context.getArgument("operation", MetaWindowPacket.CursorOperation.class),
                                                                        IntegerArgumentType.getInteger(context, "x"),
                                                                        IntegerArgumentType.getInteger(context, "y"),
                                                                        IntegerArgumentType.getInteger(context, "duration"),
                                                                        Easing.LINEAR))
                                                                .then(Commands.argument("easing", EnumArgument.enumArgument(Easing.class))
                                                                        .executes(context -> cursorOperations(context,
                                                                                context.getArgument("operation", MetaWindowPacket.CursorOperation.class),
                                                                                IntegerArgumentType.getInteger(context, "x"),
                                                                                IntegerArgumentType.getInteger(context, "y"),
                                                                                0,
                                                                                context.getArgument("easing", Easing.class))
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                                .then(Commands.literal("shake")
                                        .then(Commands.argument("duration", IntegerArgumentType.integer())
                                                .then(Commands.argument("intensity", IntegerArgumentType.integer())
                                                        .executes(context -> windowOperations(context,
                                                                MetaWindowPacket.Operation.SHAKE_CURSOR,
                                                                IntegerArgumentType.getInteger(context, "intensity"),
                                                                0,
                                                                IntegerArgumentType.getInteger(context, "duration"),
                                                                Easing.LINEAR))
                                                )
                                        )
                                )
                                .then(Commands.literal("fix")
                                        .then(Commands.argument("duration", IntegerArgumentType.integer())
                                                .executes(context -> windowOperations(context,
                                                        MetaWindowPacket.Operation.CURSOR_FIX,
                                                        0,
                                                        0,
                                                        IntegerArgumentType.getInteger(context, "duration"),
                                                        Easing.LINEAR))
                                        )
                                )
                        )
                        .then(Commands.literal("settitle")
                                .then(Commands.argument("title", StringArgumentType.string())
                                        .executes(context -> {
                                            Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
                                            String title = StringArgumentType.getString(context, "title");
                                            for (ServerPlayer player : players) {
                                                PacketDistributor.sendToPlayer(player, new MetaWindowSetTitlePacket(title));
                                            }
                                            context.getSource().sendSuccess(() -> Component.literal("Set " + players.size() + " players' game window title to " + title), true);
                                            return players.size();
                                        })
                                )
                        )
                        .then(Commands.literal("stop")
                                .executes(context -> {
                                    int size = windowOperations(context, MetaWindowPacket.Operation.STOP, 0, 0, 1, Easing.LINEAR);
                                    context.getSource().sendSuccess(() -> Component.literal("Stopped window animations for " + size + " players"), true);
                                    return size;
                                })
                        )
                        .then(Commands.literal("pause")
                                .executes(context -> {
                                    int size = windowOperations(context, MetaWindowPacket.Operation.PAUSE, 0, 0, 1, Easing.LINEAR);
                                    context.getSource().sendSuccess(() -> Component.literal("Paused window animations for " + size + " players"), true);
                                    return size;
                                })
                        )
                        .then(Commands.literal("resume")
                                .executes(context -> {
                                    int size = windowOperations(context, MetaWindowPacket.Operation.RESUME, 0, 0, 1, Easing.LINEAR);
                                    context.getSource().sendSuccess(() -> Component.literal("Resumed window animations for " + size + " players"), true);
                                    return size;
                                })
                        )
                );
    }

    private static int cursorOperations(CommandContext<CommandSourceStack> context, MetaWindowPacket.CursorOperation op, int x, int y, int duration, Easing easing) throws CommandSyntaxException {
        MetaWindowPacket.Operation operation = switch (op) {
            case moveby -> MetaWindowPacket.Operation.CURSOR_MOVE_BY;
            case moveto -> MetaWindowPacket.Operation.CURSOR_MOVE_TO;
            case move -> MetaWindowPacket.Operation.CURSOR_MOVE;
        };
        int size = windowOperations(context, operation, x, y, duration, easing);
        context.getSource().sendSuccess(() -> Component.literal("Animated " + size + " players' cursor by operation " + operation), true);
        return size;
    }

    private static int windowOperations(CommandContext<CommandSourceStack> context, MetaWindowPacket.WindAnimOperation op, int x, int y, int duration, Easing easing) throws CommandSyntaxException {
        MetaWindowPacket.Operation operation = switch (op) {
            case scaleto -> MetaWindowPacket.Operation.SCALE_TO;
            case scaleby -> MetaWindowPacket.Operation.SCALE_BY;
            case scale -> MetaWindowPacket.Operation.SCALE;
            case moveto -> MetaWindowPacket.Operation.MOVE_TO;
            case moveby -> MetaWindowPacket.Operation.MOVE_BY;
            case move -> MetaWindowPacket.Operation.MOVE;
        };
        int size = windowOperations(context, operation, x, y, duration, easing);
        context.getSource().sendSuccess(() -> Component.literal("Animated " + size + " players' game window by operation " + operation), true);
        return size;
    }

    private static int windowOperations(CommandContext<CommandSourceStack> context, MetaWindowPacket.Operation op, int x, int y, int duration, Easing easing) throws CommandSyntaxException {
        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
        for (ServerPlayer player : players) {
            PacketDistributor.sendToPlayer(player, new MetaWindowPacket(op, x, y, duration, easing));
        }
        return players.size();
    }
}