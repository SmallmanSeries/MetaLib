package com.smallmanseries.metalib.network.packet;

import com.smallmanseries.metalib.MetaLib;
import com.smallmanseries.metalib.client.window.Easing;
import com.smallmanseries.metalib.network.handler.ClientHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record MetaWindowPacket(Operation op, int x, int y, int duration, Easing easing) implements CustomPacketPayload {
    public enum Operation {FULLSCREEN, WINDOWED, SCALE_TO, SCALE_BY, SCALE, MOVE_TO, MOVE_BY, MOVE, FIX, CURSOR_MOVE_TO, CURSOR_MOVE_BY, CURSOR_MOVE, CURSOR_FIX, SHAKE, SHAKE_CURSOR, STOP, PAUSE, RESUME}
    public enum WindAnimOperation {scaleto, scaleby, scale, moveto, moveby, move}
    public enum CursorOperation {moveto, moveby, move}

    public static final Type<MetaWindowPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(MetaLib.MODID, "meta_window"));

    public static final StreamCodec<ByteBuf, MetaWindowPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.idMapper(i -> MetaWindowPacket.Operation.values()[i], MetaWindowPacket.Operation::ordinal), MetaWindowPacket::op,
            ByteBufCodecs.INT, MetaWindowPacket::x,
            ByteBufCodecs.INT, MetaWindowPacket::y,
            ByteBufCodecs.INT, MetaWindowPacket::duration,
            ByteBufCodecs.idMapper(i -> Easing.values()[i], Easing::ordinal), MetaWindowPacket::easing,
            MetaWindowPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleClient(MetaWindowPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if(packet.duration() == 0 && packet.op() != Operation.MOVE && packet.op() != Operation.SCALE){
                ClientHandler.handleWindow(packet);
            } else {
                ClientHandler.handleWindowAnimated(packet);
            }
        });
    }
}
