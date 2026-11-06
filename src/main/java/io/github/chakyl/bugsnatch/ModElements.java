package io.github.chakyl.bugsnatch;


import dev.shadowsoffire.placebo.registry.DeferredHelper;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import io.github.chakyl.bugsnatch.data.BugType;
import io.github.chakyl.bugsnatch.data.BugTypeRegistry;
import io.github.chakyl.bugsnatch.item.BugItem;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;

import static io.github.chakyl.bugsnatch.Bugsnatch.loc;

public class ModElements {
    private static final DeferredHelper R = DeferredHelper.create(Bugsnatch.MODID);

    public static class Items {
        public static final Holder<Item> CATCHING_NET = R.item("catching_net", () -> new Item(new Item.Properties().stacksTo(1)));
        public static final Holder<Item> BUG = R.item("bug", () -> new BugItem(new Item.Properties().stacksTo(64)));

        private static void bootstrap() {
        }
    }

    public static class Components {
        /**
         * Stored Bug type, used by {@link BugItem}. The underlying holder may be unbound.
         */
        public static final DataComponentType<DynamicHolder<BugType>> BUG_TYPE = R.component("bug_type", b -> b
                .persistent(BugTypeRegistry.INSTANCE.holderCodec())
                .networkSynchronized(BugTypeRegistry.INSTANCE.holderStreamCodec()));

        public static final DataComponentType<CompoundTag> ENTITY_DATA = R.component("entity_data", b -> b
                .persistent(CompoundTag.CODEC)
                .networkSynchronized(ByteBufCodecs.COMPOUND_TAG));

        private static void bootstrap() {
        }
    }


    public static class Tabs {
        public static final ResourceKey<CreativeModeTab> TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, loc("tab"));
        public static final Holder<CreativeModeTab> TAB = R.creativeTab("tab", b -> b.title(Component.translatable("itemGroup." + Bugsnatch.MODID)).icon(() -> Items.CATCHING_NET.value().getDefaultInstance()));

        private static void bootstrap() {
        }
    }

    static void bootstrap(IEventBus bus) {
        bus.register(R);
        Items.bootstrap();
        Components.bootstrap();
        Tabs.bootstrap();
    }
}