/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.respawnablepets.common.init;

import moriyashiine.respawnablepets.common.RespawnablePets;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;

public class ModEntityTypeTags {
	public static final TagKey<EntityType<?>> CANNOT_RESPAWN = TagKey.of(Registries.ENTITY_TYPE.getKey(), RespawnablePets.id("cannot_respawn"));
}
