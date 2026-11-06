package io.github.chakyl.bugsnatch.jei;

import dev.shadowsoffire.placebo.reload.DynamicHolder;
import io.github.chakyl.bugsnatch.ModElements;
import io.github.chakyl.bugsnatch.data.BugType;
import io.github.chakyl.bugsnatch.item.BugItem;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import static io.github.chakyl.bugsnatch.Bugsnatch.loc;


@JeiPlugin
public class BugsnatchJei implements IModPlugin {

    public static final ResourceLocation UID = loc("plugin");

    @Override
    public void registerItemSubtypes(ISubtypeRegistration reg) {
        reg.registerSubtypeInterpreter(ModElements.Items.BUG.value(), new BugItemSubtypes());
    }



    private static class BugItemSubtypes implements ISubtypeInterpreter<ItemStack> {

        @Override
        public DynamicHolder<BugType> getSubtypeData(ItemStack stack, UidContext context) {
            return BugItem.getStoredBug(stack);
        }

        @Override
        public String getLegacyStringSubtypeInfo(ItemStack stack, UidContext context) {
            DynamicHolder<BugType> bug = BugItem.getStoredBug(stack);
            if (!bug.isBound()) return "NULL";
            return bug.getId().toString();
        }

    }
    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }
}