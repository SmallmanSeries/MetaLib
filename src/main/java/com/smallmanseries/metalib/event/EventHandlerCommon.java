package com.smallmanseries.metalib.event;

import com.smallmanseries.metalib.MetaLib;
import com.smallmanseries.metalib.commands.MetaLibCommand;
import com.smallmanseries.metalib.network.packet.MetaInfoPacket;
import com.smallmanseries.metalib.network.packet.MetaPopupPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = MetaLib.MODID)
public class EventHandlerCommon {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        MetaLibCommand.register(event.getDispatcher(), event.getBuildContext());
    }

    @SubscribeEvent
    public static void registerPayloadHandler(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(
                MetaInfoPacket.TYPE,
                MetaInfoPacket.STREAM_CODEC,
                MetaInfoPacket::handleClient
        );

        registrar.playToClient(
                MetaPopupPacket.TYPE,
                MetaPopupPacket.STREAM_CODEC,
                MetaPopupPacket::handleClient
        );
    }
}
