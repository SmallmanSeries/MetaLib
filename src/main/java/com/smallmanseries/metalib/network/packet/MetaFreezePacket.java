package com.smallmanseries.metalib.network.packet;

import com.smallmanseries.metalib.MetaLib;
import com.smallmanseries.metalib.network.handler.ClientHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record MetaFreezePacket(int durationMs) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MetaFreezePacket> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(MetaLib.MODID, "meta_freeze"));

    public static final StreamCodec<ByteBuf, MetaFreezePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, MetaFreezePacket::durationMs,
            MetaFreezePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleClient(MetaFreezePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> ClientHandler.handleFreeze(packet));
    }
}
