package com.smallmanseries.metalib.network.packet;

import com.smallmanseries.metalib.MetaLib;
import com.smallmanseries.metalib.network.handler.ClientHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record FakeCrashReportPacket(String title, String report) implements CustomPacketPayload {

    public static final Type<FakeCrashReportPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(MetaLib.MODID, "fake_crash_report"));

    public static final StreamCodec<ByteBuf, FakeCrashReportPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, FakeCrashReportPacket::title,
            ByteBufCodecs.STRING_UTF8, FakeCrashReportPacket::report,
            FakeCrashReportPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleClient(FakeCrashReportPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> ClientHandler.handleFakeCrashReport(packet));
    }
}
