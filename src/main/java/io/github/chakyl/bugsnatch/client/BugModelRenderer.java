package io.github.chakyl.bugsnatch.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import dev.shadowsoffire.placebo.reload.DynamicHolder;
import io.github.chakyl.bugsnatch.ModElements;
import io.github.chakyl.bugsnatch.data.BugType;
import io.github.chakyl.bugsnatch.item.BugItem;
import io.github.chakyl.bugsnatch.util.ClientEntityCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import static io.github.chakyl.bugsnatch.Bugsnatch.loc;

public class BugModelRenderer extends BlockEntityWithoutLevelRenderer {

    public BugModelRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    private static final MultiBufferSource.BufferSource GHOST_ENTITY_BUF = MultiBufferSource.immediate(new ByteBufferBuilder(256));;
    private static final ResourceLocation BASE = loc("item/bug");

    @Override
    @SuppressWarnings("deprecation")
    public void renderByItem(ItemStack stack, ItemDisplayContext type, PoseStack matrix, MultiBufferSource buf, int light, int overlay) {
        ItemRenderer irenderer = Minecraft.getInstance().getItemRenderer();
//        BakedModel base = irenderer.getItemModelShaper().getModelManager().getModel(ModelResourceLocation.inventory(BASE));
        matrix.pushPose();
        if (type == ItemDisplayContext.FIXED) {
            matrix.translate(1, 1, 0);
            float scale = 0.5F;
            matrix.scale(scale, scale, scale);
            matrix.translate(-1.5F, -0.5F, 0.5F);
            matrix.mulPose(Axis.XP.rotationDegrees(90));
            matrix.mulPose(Axis.XP.rotationDegrees(90));
            matrix.translate(0, 0, -1);
        }
        else if (type != ItemDisplayContext.GUI) {
            matrix.translate(1, 1, 0);
            float scale = 0.5F;
            matrix.scale(scale, scale, scale);
            matrix.translate(-1.5F, -0.5F, 0.5F);
            matrix.mulPose(Axis.XP.rotationDegrees(90));
        }
        else {
            matrix.translate(0, -.5F, -.5F);
            matrix.mulPose(Axis.XN.rotationDegrees(75));
            matrix.mulPose(Axis.ZP.rotationDegrees(45));
            float scale = 0.9F;
            matrix.scale(scale, scale, scale);
            matrix.translate(0.775, 0, -0.0825);
        }
//        irenderer.renderModelLists(base, stack, light, overlay, matrix, ItemRenderer.getFoilBufferDirect(GHOST_ENTITY_BUF, ItemBlockRenderTypes.getRenderType(stack, true), true, false));
        GHOST_ENTITY_BUF.endBatch();
        matrix.popPose();
        DynamicHolder<BugType> model = BugItem.getStoredBug(stack);
        if (model.isBound()) {
            LivingEntity ent = ClientEntityCache.computeIfAbsent(Minecraft.getInstance().level, new CompoundTag(), model.get().entity());
            if (Minecraft.getInstance().player != null) ent.tickCount = Minecraft.getInstance().player.tickCount;
            if (ent != null) {
                this.renderEntityInInventory(matrix, type, ent, stack, model.get().renderScale());
            }
        }
    }

    @SuppressWarnings("deprecation")
    public void renderEntityInInventory(PoseStack matrix, ItemDisplayContext type, LivingEntity pLivingEntity, ItemStack stack, float scale) {
        matrix.pushPose();
        matrix.translate(0.5, 0.25, 0.5);
        if (type == ItemDisplayContext.FIXED) {
            matrix.translate(0, -0.5, 0);
            matrix.scale(scale, scale, scale);
            matrix.translate(0, 1.45, 0);
            matrix.mulPose(Axis.XN.rotationDegrees(90));
            matrix.mulPose(Axis.YN.rotationDegrees(180));
        }
        else if (type == ItemDisplayContext.GUI) {
            matrix.translate(0.05, -0.25, 0);
            matrix.scale(scale, scale, scale);
            matrix.translate(0, 0.2, 0);
        }
        else {
            matrix.translate(type == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || type == ItemDisplayContext.THIRD_PERSON_LEFT_HAND ?  -0.3 : 0.3, 0, 0);
            matrix.scale(scale, scale, scale);
            matrix.translate(0, 0.12 + 0.05 * Math.sin((pLivingEntity.tickCount + Minecraft.getInstance().getTimer().getGameTimeDeltaTicks()) / 32), 0);
        }

        float rotation = -30;
        if (type == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || type == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) rotation = 30;
        if (type == ItemDisplayContext.FIXED) rotation = 180;
        matrix.mulPose(Axis.YP.rotationDegrees(rotation));
        pLivingEntity.setYRot(0);
        pLivingEntity.yBodyRot = pLivingEntity.getYRot();
        pLivingEntity.yHeadRot = pLivingEntity.getYRot();
        pLivingEntity.yHeadRotO = pLivingEntity.getYRot();
        CompoundTag tag = stack.get(ModElements.Components.ENTITY_DATA);
        if (tag != null && !tag.isEmpty()) {
            CompoundTag entityTag = new CompoundTag();
            pLivingEntity.saveWithoutId(entityTag);
            entityTag.merge(tag);
            pLivingEntity.load(entityTag);
        }
        EntityRenderDispatcher entityrenderermanager = Minecraft.getInstance().getEntityRenderDispatcher();
        entityrenderermanager.setRenderShadow(false);
        MultiBufferSource.BufferSource rtBuffer = GHOST_ENTITY_BUF;
        RenderSystem.runAsFancy(() -> {
            entityrenderermanager.render(pLivingEntity, 0, 0, 0, 0.0F, Minecraft.getInstance().getTimer().getGameTimeDeltaTicks(), matrix, new WrappedRTBuffer(rtBuffer), 15728880);
        });
        rtBuffer.endBatch();
        entityrenderermanager.setRenderShadow(true);
        matrix.popPose();
    }

}