/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.respawnablepets.common.init;

import moriyashiine.respawnablepets.common.RespawnablePets;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModCriterion {
	public static TickCriterion MAKE_PET_RESPAWNABLE = new TickCriterion();

	public static void init() {
		Registry.register(Registries.CRITERION, RespawnablePets.id("make_pet_respawnable"), MAKE_PET_RESPAWNABLE);
	}
}
