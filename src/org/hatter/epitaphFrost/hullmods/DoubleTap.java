package org.hatter.epitaphFrost.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.loading.WeaponSpecAPI;
import com.fs.starfarer.api.loading.ProjectileWeaponSpecAPI;

public class DoubleTap extends BaseHullMod {

	public static final float FLUX_FACTOR  = 1.5f;
	public static final float PHASE_FACTOR  = 1.25f;
	public static final float VENT_FACTOR = 1.75f;

	//public static final int BURST_FACTOR  = 2;
	//public static final float MAX_BURST_DELAY  = 0.25f;
	//public static final float BURST_DELAY_PERCENT  = 0.1f; //for calculating burst delay from refire delay
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getFluxCapacity().modifyMult(id, FLUX_FACTOR);
		stats.getShieldDamageTakenMult().modifyMult(id, FLUX_FACTOR);
		stats.getPhaseCloakUpkeepCostBonus().modifyMult(id, PHASE_FACTOR);
		stats.getVentRateMult().modifyMult(id, VENT_FACTOR);
		/*
		for (String slotId : stats.getVariant().getFittedWeaponSlots()) {
			WeaponSpecAPI weaponSpec = stats.getVariant().getWeaponSpec(slotId);
			if (weaponSpec instanceof ProjectileWeaponSpecAPI) {
				((ProjectileWeaponSpecAPI) weaponSpec).setBurstSize(weaponSpec.getBurstSize() * BURST_FACTOR);
			}

		}
		*/
	}

	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + Math.round((FLUX_FACTOR-1)*(100)) + "%";
		if (index == 1) return "" + Math.round((PHASE_FACTOR-1)*(100)) + "%";
		if (index == 2) return "" + Math.round((VENT_FACTOR-1)*(100)) + "%";

		return null;
	}


}
