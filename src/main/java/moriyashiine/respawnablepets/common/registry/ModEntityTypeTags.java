/*
 * All Rights Reserved (c) 2022 MoriyaShiine
 */

package moriyashiine.respawnablepets.common.registry;

import moriyashiine.respawnablepets.common.RespawnablePets;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModEntityTypeTags {
	public static final TagKey<EntityType<?>> CANNOT_RESPAWN = TagKey.of(Registries.ENTITY_TYPE.getKey(), new Identifier(RespawnablePets.MOD_ID, "cannot_respawn"));
}
