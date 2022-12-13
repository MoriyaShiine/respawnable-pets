/*
 * All Rights Reserved (c) 2022 MoriyaShiine
 */

package moriyashiine.respawnablepets.common.registry;

import moriyashiine.respawnablepets.common.RespawnablePets;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSoundEvents {
	public static final SoundEvent ENTITY_GENERIC_TELEPORT = SoundEvent.of(new Identifier(RespawnablePets.MOD_ID, "entity.generic.teleport"));

	public static void init() {
		Registry.register(Registries.SOUND_EVENT, ENTITY_GENERIC_TELEPORT.getId(), ENTITY_GENERIC_TELEPORT);
	}
}
