package org.hatter.epitaphFrost.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.WeaponAPI;

import java.awt.*;
import java.lang.Math;
import java.util.EnumSet;

public class SuperconductiveCycler extends BaseHullMod {
	public static final float MAX_ROF_BOOST  = 3f;
	public static final float MIN_FLUX_THRESHOLD  = 0.1f;
	public static final float MAX_FLUX_THRESHOLD  = 0.15f;
	public static final float ENERGY_FLUX_REDUCTION = 0.9f;

	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		if (isSMod(stats)){
			stats.getEnergyWeaponFluxCostMod().modifyMult(id, ENERGY_FLUX_REDUCTION);
		}
	}
	public void advanceInCombat(ShipAPI ship, float amount) {

		if (!ship.isAlive()) {
			return;
		}
		if ((ship.getFluxLevel() - ship.getHardFluxLevel()) < MAX_FLUX_THRESHOLD) {
			float partial_boost = MAX_ROF_BOOST-(ship.getFluxLevel() - ship.getHardFluxLevel() -MIN_FLUX_THRESHOLD)*((MAX_ROF_BOOST-1)/(MAX_FLUX_THRESHOLD-MIN_FLUX_THRESHOLD));
			float rof_boost = Math.min(MAX_ROF_BOOST,partial_boost);
			ship.setWeaponGlow((float) 0.1*(rof_boost / MAX_ROF_BOOST), Color.CYAN, EnumSet.of(WeaponAPI.WeaponType.ENERGY));
			ship.getMutableStats().getEnergyRoFMult().modifyMult("Cycler", rof_boost);
			ship.getMutableStats().getEnergyAmmoRegenMult().modifyMult("Cycler", rof_boost);
			if (ship == Global.getCombatEngine().getPlayerShip()) {
				Global.getCombatEngine().maintainStatusForPlayerShip("Cycler", "graphics/EpitaphFrost/icons/hullsys/superconductive_cycler.png", "Cycler", Math.round(100*rof_boost)+"%", false);
			}
		}
		else if ((ship.getFluxLevel() - ship.getHardFluxLevel()) < MAX_FLUX_THRESHOLD)  {

		}
		else {
			ship.getMutableStats().getEnergyRoFMult().unmodify("Cycler");
			ship.getMutableStats().getEnergyAmmoRegenMult().unmodify("Cycler");
		}
	}
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) (MAX_ROF_BOOST-1)*(100) + "%";
		if (index == 1) return "" + MIN_FLUX_THRESHOLD*100 +"%";
		if (index == 2) return "" + (int)(MAX_FLUX_THRESHOLD*100) +"%";
		return null;
}
	public String getSModDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int)((1- ENERGY_FLUX_REDUCTION)*100) +"%";
		return null;
	}
}