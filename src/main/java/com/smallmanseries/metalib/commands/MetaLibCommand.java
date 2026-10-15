package com.smallmanseries.metalib.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.smallmanseries.metalib.api.MetaPopup;
import com.smallmanseries.metalib.network.packet.MetaInfoPacket;
import com.smallmanseries.metalib.network.packet.MetaPopupPacket;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Collection;

public class MetaLibCommand {

    /* /metalib <subCommand> <target(s)> [operation]... */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(
                Commands.literal("metalib").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .then(info())
                        .then(popup())
        );
    }

    /* /metalib info <targets> <username|time|ip|os> */ //Todo meta text component
    private static LiteralArgumentBuilder<CommandSourceStack> info() {
        return Commands.literal("info")
                .then(Commands.argument("targets", EntityArgument.players())
                        .then(Commands.literal("username")
                                .executes(context -> {
                                            Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
                                            for (ServerPlayer player : players) {
                                                PacketDistributor.sendToPlayer(player, new MetaInfoPacket(MetaInfoPacket.InfoType.SYS_USERNAME));
                                            }
                                            return players.size();
                                        }
                                )
                        )
                        .then(Commands.literal("time")
                                .executes(context -> {
                                            Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
                                            for (ServerPlayer player : players) {
                                                PacketDistributor.sendToPlayer(player, new MetaInfoPacket(MetaInfoPacket.InfoType.SYS_TIME));
                                            }
                                            return players.size();
                                        }
                                )
                        )
                        .then(Commands.literal("ip")
                                .executes(context -> {
                                            Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
                                            for (ServerPlayer player : players) {
                                                PacketDistributor.sendToPlayer(player, new MetaInfoPacket(MetaInfoPacket.InfoType.IP_ADDRESS));
                                            }
                                            return players.size();
                                        }
                                )
                        )
                        .then(Commands.literal("os")
                                .executes(context -> {
                                            Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
                                            for (ServerPlayer player : players) {
                                                PacketDistributor.sendToPlayer(player, new MetaInfoPacket(MetaInfoPacket.InfoType.OS_NAME));
                                            }
                                            return players.size();
                                        }
                                )
                        )
                );
    }

    /* /metalib popup <targets> <title> <message> <type> [<stack>] */
    private static LiteralArgumentBuilder<CommandSourceStack> popup() {
        return Commands.literal("popup")
                .then(Commands.argument("targets", EntityArgument.players())
                        .then(Commands.argument("title", StringArgumentType.string())
                                .then(Commands.argument("message", StringArgumentType.string())
                                        .then(Commands.argument("type", StringArgumentType.word())
                                                .suggests((c, b) -> {
                                                    for (MetaPopup.Type t : MetaPopup.Type.values())
                                                        b.suggest(t.name().toLowerCase());
                                                    return b.buildFuture();
                                                })
                                                .executes(context -> {
                                                    Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
                                                    String title = StringArgumentType.getString(context, "title");
                                                    String msg = StringArgumentType.getString(context, "message");
                                                    MetaPopup.Type type = MetaPopup.Type.valueOf(StringArgumentType.getString(context, "type").toUpperCase());
                                                    for (ServerPlayer player : players) {
                                                        PacketDistributor.sendToPlayer(player, new MetaPopupPacket(title, msg, type, false, ""));
                                                    }
                                                    context.getSource().sendSuccess(() -> Component.literal("Popup(s) have been shown to " + players.size() + " players"), true);
                                                    return players.size();
                                                })
                                                .then(Commands.argument("stack", StringArgumentType.string())
                                                        .executes(context -> {
                                                            Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
                                                            String title = StringArgumentType.getString(context, "title");
                                                            String msg = StringArgumentType.getString(context, "message");
                                                            MetaPopup.Type type = MetaPopup.Type.valueOf(StringArgumentType.getString(context, "type").toUpperCase());
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
}