/*
 * All Rights Reserved (c) 2022 MoriyaShiine
 */

package moriyashiine.respawnablepets.common.registry;

import moriyashiine.respawnablepets.common.RespawnablePets;
import net.minecraft.entity.EntityType;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModEntityTypeTags {
	public static final TagKey<EntityType<?>> BLACKLISTED = TagKey.of(Registry.ENTITY_TYPE_KEY, new Identifier(RespawnablePets.MOD_ID, "blacklisted"));
}
