package com.smallmanseries.metalib.network.packet;

import com.smallmanseries.metalib.MetaLib;
import com.smallmanseries.metalib.network.handler.ClientHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record MetaStutterPacket(int durationMs, int intervalMs, int times) implements CustomPacketPayload {
    public static final Type<MetaStutterPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(MetaLib.MODID, "meta_stutter"));

    public static final StreamCodec<ByteBuf, MetaStutterPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, MetaStutterPacket::durationMs,
            ByteBufCodecs.INT, MetaStutterPacket::intervalMs,
            ByteBufCodecs.INT, MetaStutterPacket::times,
            MetaStutterPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleClient(MetaStutterPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> ClientHandler.handleStutter(packet));
    }
}
