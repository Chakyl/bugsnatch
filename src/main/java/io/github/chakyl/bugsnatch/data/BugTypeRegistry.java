package io.github.chakyl.bugsnatch.data;

import com.google.common.collect.ImmutableMap;
import dev.shadowsoffire.placebo.reload.DynamicRegistry;
import io.github.chakyl.bugsnatch.Bugsnatch;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

import static io.github.chakyl.bugsnatch.Bugsnatch.loc;

public class BugTypeRegistry extends DynamicRegistry<BugType> {
    public static final BugTypeRegistry INSTANCE = new BugTypeRegistry();

    private Map<EntityType<?>, BugType> bugsByEntityType = new HashMap<>();

    public BugTypeRegistry() {
        super(Bugsnatch.LOGGER, "bugs", true, false);
    }

    @Override
    protected void registerBuiltinCodecs() {
        this.registerDefaultCodec(loc("bugs"), BugType.CODEC);
    }

    @Override
    protected void beginReload(ReloadType type) {
        super.beginReload(type);
        this.bugsByEntityType = new HashMap<>();
    }

    @Override
    protected void onReload(ReloadType type) {
        super.onReload(type);
        this.bugsByEntityType = ImmutableMap.copyOf(this.bugsByEntityType);
    }

    @Override
    protected void validateItem(ResourceLocation key, BugType BugType) {
        BugType.validate(key);
        if (this.bugsByEntityType.containsKey(BugType.entity())) {
            String msg = "Attempted to register two bugs (%s and %s) for Type %s!";
            throw new UnsupportedOperationException(String.format(msg, key, this.getKey(this.bugsByEntityType.get(BugType.entity())), BugType.entity()));
        }
        this.bugsByEntityType.put(BugType.entity(), BugType);
    }

    @Nullable
    public BugType getForEntity(EntityType<?> type) {
        return this.bugsByEntityType.get(type);
    }


}
