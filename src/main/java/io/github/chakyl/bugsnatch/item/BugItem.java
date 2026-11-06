package io.github.chakyl.bugsnatch.item;


import dev.shadowsoffire.placebo.reload.DynamicHolder;
import dev.shadowsoffire.placebo.tabs.ITabFiller;
import io.github.chakyl.bugsnatch.ModElements;
import io.github.chakyl.bugsnatch.data.BugType;
import io.github.chakyl.bugsnatch.data.BugTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

public class BugItem extends Item implements ITabFiller {
    public BugItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public Component getName(ItemStack pStack) {
        DynamicHolder<BugType> bugType = getStoredBug(pStack);
        Component bugName;
        if (!bugType.isBound()) {
            bugName = Component.translatable("item.society.bug");
        } else bugName = bugType.get().entity().getDescription().copy();
        return Component.translatable(this.getDescriptionId(pStack), bugName);
    }


    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        InteractionHand hand = context.getHand();
        BlockPos pos = context.getClickedPos();
        player.playSound(SoundEvents.TADPOLE_FLOP, 1.0F, 1.0F);
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            DynamicHolder<BugType> bugType = getStoredBug(stack);
            if (bugType.isBound()) {
                Entity entity = bugType.get().entity().create(level);
                if (entity != null) {
                    entity.setPos(pos.getX(), pos.getY() + 1, pos.getZ());
                    entity.setYRot(player.getYRot());
                    entity.setXRot(player.getXRot());
                    CompoundTag tag = stack.get(ModElements.Components.ENTITY_DATA);
                    if (tag != null && !tag.isEmpty()) {
                        CompoundTag entityTag = new CompoundTag();
                        entity.saveWithoutId(entityTag);
                        entityTag.merge(tag);
                        entity.load(entityTag);
                    }
                    if (tag != null && tag.contains("CustomName")) {
                        Component customName = Component.Serializer.fromJson(tag.getString("CustomName"), level.registryAccess());
                        if (customName != null) {
                            entity.setCustomName(customName);
                        }
                    }
                    if (entity instanceof Mob mob)  mob.setPersistenceRequired();

                    level.addFreshEntity(entity);
                    if (!player.isCreative()) stack.shrink(1);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);

        if (entity instanceof Player player && !level.isClientSide()) {
            if (isSelected) {
                DynamicHolder<BugType> bugType = getStoredBug(stack);
                if (!bugType.isBound() || !bugType.get().bites()) return;
                if (player.tickCount % 20 == 0) {
                    BugType bug = bugType.get();
                    player.hurt(level.damageSources().generic(), bug.biteDamage());
                    for (MobEffectInstance effectInstance : bug.biteEffects()) {
                        if (effectInstance != null) player.addEffect(copyEffect(effectInstance));
                    }
                }
            }
        }
    }

    public static MobEffectInstance copyEffect(MobEffectInstance effect) {
        return new MobEffectInstance(effect.getEffect(), effect.getDuration(), effect.getAmplifier(), effect.isAmbient(), effect.isVisible());
    }

    public static DynamicHolder<BugType> getStoredBug(ItemStack stack) {
        return stack.getOrDefault(ModElements.Components.BUG_TYPE, BugTypeRegistry.INSTANCE.emptyHolder());
    }

    public static void setStoredBug(ItemStack stack, BugType bug) {
        setStoredBug(stack, BugTypeRegistry.INSTANCE.holder(bug));
    }

    public static void setStoredBug(ItemStack stack, DynamicHolder<BugType> bug) {
        stack.set(ModElements.Components.BUG_TYPE, bug);
    }


    @Override
    public void fillItemCategory(CreativeModeTab creativeModeTab, BuildCreativeModeTabContentsEvent event) {
        BugTypeRegistry.INSTANCE.getKeys().stream().sorted().map(BugTypeRegistry.INSTANCE::holder).forEach(holder -> {
            ItemStack s = new ItemStack(this);
            setStoredBug(s, holder);
            event.accept(s);
        });
    }

}