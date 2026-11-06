package io.github.chakyl.bugsnatch;

import dev.shadowsoffire.placebo.tabs.TabFillingRegistry;
import io.github.chakyl.bugsnatch.data.BugTypeRegistry;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(Bugsnatch.MODID)
public class Bugsnatch {
    public static final String MODID = "bugsnatch";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public Bugsnatch(ModContainer container) {
        IEventBus modEventBus = container.getEventBus();
        BugTypeRegistry.INSTANCE.registerToBus();
        modEventBus.register(this);
        ModElements.bootstrap(modEventBus);
    }

    @SubscribeEvent
    public void setup(FMLCommonSetupEvent e) {
        e.enqueueWork(() -> {
            TabFillingRegistry.register(
                    ModElements.Tabs.TAB_KEY,
                    ModElements.Items.CATCHING_NET,
                    ModElements.Items.BUG
            );
        });
    }

    public static ResourceLocation loc(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

}