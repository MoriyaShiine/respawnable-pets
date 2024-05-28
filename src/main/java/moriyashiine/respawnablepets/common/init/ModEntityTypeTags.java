/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.respawnablepets.common.init;

import moriyashiine.respawnablepets.common.RespawnablePets;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class ModEntityTypeTags {
	public static final TagKey<EntityType<?>> CANNOT_RESPAWN = TagKey.of(RegistryKeys.ENTITY_TYPE, RespawnablePets.id("cannot_respawn"));
}
