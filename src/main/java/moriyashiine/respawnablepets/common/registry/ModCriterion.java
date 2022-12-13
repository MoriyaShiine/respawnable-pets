package moriyashiine.respawnablepets.common.registry;

import moriyashiine.respawnablepets.common.RespawnablePets;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.TickCriterion;

public class ModCriterion {
	public static TickCriterion MAKE_PET_RESPAWNABLE;

	public static void init() {
		MAKE_PET_RESPAWNABLE = Criteria.register(new TickCriterion(RespawnablePets.id("make_pet_respawnable")));
	}
}
