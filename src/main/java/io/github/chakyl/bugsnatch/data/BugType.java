package io.github.chakyl.bugsnatch.data;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;

import java.util.List;
import java.util.Optional;

public record BugType(EntityType<?> entity, float renderScale, boolean bites, int biteDamage, List<MobEffectInstance> biteEffects, List<String> savedTags) implements AbstractBugType {
    public static final Codec<BugType> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                    BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entity").forGetter(BugType::entity),
                    Codec.FLOAT.optionalFieldOf("render_scale", 1.0f).forGetter(BugType::renderScale),
                    Codec.BOOL.optionalFieldOf("bites", false).forGetter(BugType::bites),
                    Codec.INT.optionalFieldOf("bite_damage", 1).forGetter(BugType::biteDamage),
                    MobEffectInstance.CODEC.listOf().optionalFieldOf("bite_effects", List.of()).forGetter(BugType::biteEffects),
                    Codec.STRING.listOf().optionalFieldOf("saved_tags", List.of()).forGetter(BugType::savedTags)
            )
            .apply(inst, BugType::new));

    public BugType(BugType other) {
        this(other.entity, other.renderScale, other.bites, other.biteDamage, other.biteEffects, other.savedTags);
    }


    public BugType validate(ResourceLocation key) {
        Preconditions.checkNotNull(this.entity, "Invalid Entity Type!");
        return this;
    }


    @Override
    public Codec<? extends BugType> getCodec() {
        return CODEC;
    }

}