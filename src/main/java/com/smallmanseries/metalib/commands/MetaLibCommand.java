package com.smallmanseries.metalib.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.smallmanseries.metalib.api.MetaPopup;
import com.smallmanseries.metalib.client.fakecrash.FakeCrashReportBuilder;
import com.smallmanseries.metalib.network.packet.FakeCrashReportPacket;
import com.smallmanseries.metalib.network.packet.ForceCrashOrQuitPacket;
import com.smallmanseries.metalib.network.packet.MetaInfoPacket;
import com.smallmanseries.metalib.network.packet.MetaPopupPacket;
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
                                                .executes(context -> {
                                                    Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
                                                    String title = StringArgumentType.getString(context, "description");
                                                    String msg = StringArgumentType.getString(context, "message");
                                                    MetaPopup.Type type = context.getArgument("type", MetaPopup.Type.class);
                                                    for (ServerPlayer player : players) {
                                                        PacketDistributor.sendToPlayer(player, new MetaPopupPacket(title, msg, type, false, ""));
                                                    }
                                                    context.getSource().sendSuccess(() -> Component.literal("Popup(s) have been shown to " + players.size() + " players"), true);
                                                    return players.size();
                                                })
                                                .then(Commands.argument("stack", StringArgumentType.string())
                                                        .executes(context -> {
                                                            Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
                                                            String title = StringArgumentType.getString(context, "description");
                                                            String msg = StringArgumentType.getString(context, "message");
                                                            MetaPopup.Type type = context.getArgument("type", MetaPopup.Type.class);
                                                            String stack = StringArgumentType.getString(context, "stack");
                                                            for (ServerPlayer player : players) {
                                                                PacketDistributor.sendToPlayer(player, new MetaPopupPacket(title, msg, type, true, stack));
                                                            }
                                                            context.getSource().sendSuccess(() -> Component.literal("Popup(s) have been shown to " + players.size() + " players"), true);
                                                            return players.size();
                                                        })
                                                )
                                        )
                                )
                        )
                );
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
}