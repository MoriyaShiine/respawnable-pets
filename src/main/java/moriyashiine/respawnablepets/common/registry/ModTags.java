/*
 * All Rights Reserved (c) 2022 MoriyaShiine
 */

package moriyashiine.respawnablepets.common.registry;

import moriyashiine.respawnablepets.common.RespawnablePets;
import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.entity.EntityType;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class ModTags {
	public static final Tag<EntityType<?>> BLACKLISTED = TagFactory.ENTITY_TYPE.create(new Identifier(RespawnablePets.MOD_ID, "blacklisted"));
}
