/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */

package moriyashiine.respawnablepets.common.tag;

import moriyashiine.respawnablepets.common.RespawnablePets;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class ModEntityTypeTags {
	public static final TagKey<EntityType<?>> CANNOT_RESPAWN = TagKey.create(Registries.ENTITY_TYPE, RespawnablePets.id("cannot_respawn"));
}
