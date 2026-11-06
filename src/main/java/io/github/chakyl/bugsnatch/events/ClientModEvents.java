package io.github.chakyl.bugsnatch.events;

import io.github.chakyl.bugsnatch.Bugsnatch;
import io.github.chakyl.bugsnatch.ModElements;
import io.github.chakyl.bugsnatch.client.BugModelRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

import static io.github.chakyl.bugsnatch.Bugsnatch.loc;

@EventBusSubscriber(modid = Bugsnatch.MODID)
public class ClientModEvents {

    @SubscribeEvent
    public static void mrl(ModelEvent.RegisterAdditional e) {
        e.register(ModelResourceLocation.standalone(loc("item/bug")));
    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
                               private final BugModelRenderer bugModelRenderer = new BugModelRenderer();

                               @Override
                               public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                                   return this.bugModelRenderer;
                               }
                           },
                ModElements.Items.BUG.value());
    }
}