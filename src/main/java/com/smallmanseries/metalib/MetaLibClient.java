package com.smallmanseries.metalib;

import com.smallmanseries.metalib.client.fakecrash.FakeCrashReportBuilder;
import com.smallmanseries.metalib.client.window.WindowAnimationEngine;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = MetaLib.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = MetaLib.MODID, value = Dist.CLIENT)
public class MetaLibClient {
    public MetaLibClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        Minecraft.getInstance().execute(FakeCrashReportBuilder::getRenderInfo);
        WindowAnimationEngine.startFrameDriver();
    }
}
