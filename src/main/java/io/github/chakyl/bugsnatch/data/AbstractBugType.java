package io.github.chakyl.bugsnatch.data;

import dev.shadowsoffire.placebo.codec.CodecProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;

import java.util.List;

public sealed interface AbstractBugType extends CodecProvider<BugType> permits BugType {

    EntityType<?> entity();

    float renderScale();

    boolean bites();

    int biteDamage();

    List<MobEffectInstance> biteEffects();

    List<String> savedTags();


}