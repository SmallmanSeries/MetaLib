package com.smallmanseries.metalib.event;

import com.smallmanseries.metalib.MetaLib;
import com.smallmanseries.metalib.api.MetaWindowAnimate;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

@EventBusSubscriber(modid = MetaLib.MODID, value = Dist.CLIENT)
public class EventHandlerClient {
    @SubscribeEvent
    public static void onDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
        MetaWindowAnimate.stopAllAnimations();
    }
}
