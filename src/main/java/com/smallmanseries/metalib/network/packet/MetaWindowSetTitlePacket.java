package com.smallmanseries.metalib.network.packet;

import com.smallmanseries.metalib.MetaLib;
import com.smallmanseries.metalib.api.MetaPopup;
import com.smallmanseries.metalib.network.handler.ClientHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;


public record MetaWindowSetTitlePacket(String title) implements CustomPacketPayload {

    public static final Type<MetaWindowSetTitlePacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(MetaLib.MODID, "meta_window_settitle"));

    public static final StreamCodec<ByteBuf, MetaWindowSetTitlePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, MetaWindowSetTitlePacket::title,
            MetaWindowSetTitlePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleClient(MetaWindowSetTitlePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> ClientHandler.handleWindowSetTitle(packet));
    }
}