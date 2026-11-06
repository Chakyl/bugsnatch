package io.github.chakyl.bugsnatch.registry;

import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import static io.github.chakyl.bugsnatch.Bugsnatch.loc;

public class BugsnatchTags {
    public static final TagKey<Item> CATCHING_NET = tag("catching_net");

    public static TagKey<Item> tag(String name) {
        return ItemTags.create(loc(name));
    }
}
