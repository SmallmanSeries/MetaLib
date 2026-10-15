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

/**
 * Send this packet to the specified player to display a pop-up window on their computer.
 *
 * @param title      Pop-up title
 * @param message    Pop-up content
 * @param dialogType Pop-up type ({@link MetaPopup.Type})
 * @param withStack  Whether to display stack trace information
 * @param stackTrace Fake stack trace information (if <code>withStack</code> is <code>false</code>, they will never be displayed)
 * @see MetaPopup
 */
public record MetaPopupPacket(String title, String message, MetaPopup.Type dialogType, boolean withStack,
                              String stackTrace) implements CustomPacketPayload {

    public static final Type<MetaPopupPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(MetaLib.MODID, "meta_dialog"));

    public static final StreamCodec<ByteBuf, MetaPopupPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, MetaPopupPacket::title,
            ByteBufCodecs.STRING_UTF8, MetaPopupPacket::message,
            ByteBufCodecs.idMapper(i -> MetaPopup.Type.values()[i], MetaPopup.Type::ordinal), MetaPopupPacket::dialogType,
            ByteBufCodecs.BOOL, MetaPopupPacket::withStack,
            ByteBufCodecs.STRING_UTF8, MetaPopupPacket::stackTrace,
            MetaPopupPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleClient(MetaPopupPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> ClientHandler.handlePopup(packet));
    }
}