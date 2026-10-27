package com.smallmanseries.metalib.event;

import com.smallmanseries.metalib.MetaLib;
import com.smallmanseries.metalib.api.MetaWindowAnimate;
import com.smallmanseries.metalib.client.window.WindowAnimationEngine;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;

@EventBusSubscriber(modid = MetaLib.MODID, value = Dist.CLIENT)
public class EventHandlerClient {
    @SubscribeEvent
    public static void onDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
        MetaWindowAnimate.stopAllAnimations();
    }

    @SubscribeEvent
    public static void onRenderFrame(RenderFrameEvent.Post event) {
        WindowAnimationEngine.INSTANCE.tick();
    }
}
