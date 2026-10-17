package com.smallmanseries.metalib.network.packet;

import com.smallmanseries.metalib.MetaLib;
import com.smallmanseries.metalib.network.handler.ClientHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ForceCrashOrQuitPacket(String description, String report, int delayTime,
                                     boolean silent) implements CustomPacketPayload {

    public static final Type<ForceCrashOrQuitPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(MetaLib.MODID, "force_crash"));

    public static final StreamCodec<ByteBuf, ForceCrashOrQuitPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ForceCrashOrQuitPacket::description,
            ByteBufCodecs.STRING_UTF8, ForceCrashOrQuitPacket::report,
            ByteBufCodecs.INT, ForceCrashOrQuitPacket::delayTime,
            ByteBufCodecs.BOOL, ForceCrashOrQuitPacket::silent,
            ForceCrashOrQuitPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleClient(ForceCrashOrQuitPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> ClientHandler.handleForceCrash(packet));
    }
}