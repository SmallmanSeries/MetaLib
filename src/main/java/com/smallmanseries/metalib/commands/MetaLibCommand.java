package com.smallmanseries.metalib.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.smallmanseries.metalib.network.packet.MetaInfoPacket;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Collection;

public class MetaLibCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(
                Commands.literal("metalib").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .then(info())
        );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> info() {
        return Commands.literal("info")
                .then(
                        Commands.argument("targets", EntityArgument.players())
                                .then(
                                        Commands.literal("username")
                                                .executes(context -> {
                                                            Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
                                                            for (ServerPlayer player : players) {
                                                                PacketDistributor.sendToPlayer(player, new MetaInfoPacket(MetaInfoPacket.InfoType.SYS_USERNAME));
                                                            }
                                                            return players.size();
                                                        }
                                                )
                                )
                                .then(
                                        Commands.literal("time")
                                                .executes(context -> {
                                                            Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
                                                            for (ServerPlayer player : players) {
                                                                PacketDistributor.sendToPlayer(player, new MetaInfoPacket(MetaInfoPacket.InfoType.SYS_TIME));
                                                            }
                                                            return players.size();
                                                        }
                                                )
                                )
                                .then(
                                        Commands.literal("ip")
                                                .executes(context -> {
                                                            Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
                                                            for (ServerPlayer player : players) {
                                                                PacketDistributor.sendToPlayer(player, new MetaInfoPacket(MetaInfoPacket.InfoType.IP_ADDRESS));
                                                            }
                                                            return players.size();
                                                        }
                                                )
                                )
                                .then(
                                        Commands.literal("os")
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
}
