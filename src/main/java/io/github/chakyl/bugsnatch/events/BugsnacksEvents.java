package io.github.chakyl.bugsnatch.events;

import io.github.chakyl.bugsnatch.Bugsnatch;
import io.github.chakyl.bugsnatch.ModElements;
import io.github.chakyl.bugsnatch.data.BugType;
import io.github.chakyl.bugsnatch.data.BugTypeRegistry;
import io.github.chakyl.bugsnatch.item.BugItem;
import io.github.chakyl.bugsnatch.registry.BugsnatchTags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.items.ItemHandlerHelper;

@EventBusSubscriber(modid = Bugsnatch.MODID)
public class BugsnacksEvents {

    @SubscribeEvent
    public static void onEntityInteracted(PlayerInteractEvent.EntityInteract event) {
        if (!event.getItemStack().is(BugsnatchTags.CATCHING_NET)) return;
        if (event.getEntity().getUsedItemHand() == InteractionHand.OFF_HAND) return;
        Player player = event.getEntity();
        if (event.getItemStack().is(ModElements.Items.CATCHING_NET)) {
            player.swing(InteractionHand.MAIN_HAND);
        }
        if (event.getLevel().isClientSide()) return;
        BugType bugType = BugTypeRegistry.INSTANCE.getForEntity(event.getTarget().getType());
        if (bugType == null) return;
        Entity target = event.getTarget();
        CompoundTag entityNbt = new CompoundTag();
        target.saveWithoutId(entityNbt);

        if (target.hasCustomName()) {
            entityNbt.putString("CustomName", Component.Serializer.toJson(target.getCustomName(), target.registryAccess()));
        }

        target.discard();
        ItemStack filledBug = ModElements.Items.BUG.value().getDefaultInstance();
        BugItem.setStoredBug(filledBug, bugType);

        filledBug.update(ModElements.Components.ENTITY_DATA, new CompoundTag(), tag -> {
            for (String tagToSave : bugType.savedTags()) {
                if (entityNbt.contains(tagToSave)) tag.put(tagToSave, entityNbt.get(tagToSave));
            }
            if (entityNbt.contains("CustomName")) {
                tag.put("CustomName", entityNbt.get("CustomName"));
            }
            return tag;
        });
        ItemHandlerHelper.giveItemToPlayer(player, filledBug);
    }
}