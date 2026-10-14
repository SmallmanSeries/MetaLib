package com.smallmanseries.metalib.network.packet;

import com.smallmanseries.metalib.MetaLib;
import com.smallmanseries.metalib.network.handler.ClientHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record MetaInfoPacket(InfoType infoType) implements CustomPacketPayload {
    public enum InfoType {SYS_USERNAME, SYS_TIME, IP_ADDRESS, OS_NAME}

    public static final Type<MetaInfoPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(MetaLib.MODID, "meta_info"));

    public static final StreamCodec<ByteBuf, MetaInfoPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.idMapper(i -> InfoType.values()[i], InfoType::ordinal), MetaInfoPacket::infoType,
            MetaInfoPacket::new
    );

    public static void handleClient(MetaInfoPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> ClientHandler.handleInfo(packet));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
