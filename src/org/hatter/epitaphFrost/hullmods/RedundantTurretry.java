package org.hatter.epitaphFrost.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.listeners.WeaponBaseRangeModifier;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class RedundantTurretry extends BaseHullMod {
	/*
	public static final int LARGE_DISCOUNT = -16;
	public static final int MEDIUM_DISCOUNT = -8;
	public static final int SMALL_DISCOUNT = -4;
	public static final float ROF_FACTOR  = 0.5f;
	public static final float FLUX_FACTOR  = 2f;
	*/
	public static final float ARMOR_MULT = 5f;
	public static final int RANGE_BOOST  = 200;

	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		/*
		stats.getDynamic().getMod(Stats.LARGE_BALLISTIC_MOD).modifyFlat(id, LARGE_DISCOUNT);
		stats.getDynamic().getMod(Stats.MEDIUM_BALLISTIC_MOD).modifyFlat(id, MEDIUM_DISCOUNT);
		stats.getDynamic().getMod(Stats.SMALL_BALLISTIC_MOD).modifyFlat(id, SMALL_DISCOUNT);
		stats.getDynamic().getMod(Stats.LARGE_ENERGY_MOD).modifyFlat(id, LARGE_DISCOUNT);
		stats.getDynamic().getMod(Stats.MEDIUM_ENERGY_MOD).modifyFlat(id, MEDIUM_DISCOUNT);
		stats.getDynamic().getMod(Stats.SMALL_ENERGY_MOD).modifyFlat(id, SMALL_DISCOUNT);

		stats.getBallisticRoFMult().modifyMult(id,ROF_FACTOR);
		stats.getBallisticAmmoRegenMult().modifyMult(id,ROF_FACTOR);
		stats.getBallisticWeaponFluxCostMod().modifyMult(id,1/ROF_FACTOR);

		stats.getEnergyRoFMult().modifyMult(id,ROF_FACTOR);
		stats.getEnergyAmmoRegenMult().modifyMult(id,ROF_FACTOR);
		stats.getEnergyWeaponFluxCostMod().modifyMult(id,1/ROF_FACTOR);

		stats.getFluxDissipation().modifyMult(id, FLUX_FACTOR);
		stats.getFluxCapacity().modifyMult(id, FLUX_FACTOR);
		//prevent hitting base stats
		stats.getFluxDissipation().modifyFlat(id, (stats.getFluxDissipation().getBaseValue())*(-1+1/FLUX_FACTOR));
		stats.getFluxCapacity().modifyFlat(id, (stats.getFluxCapacity().getBaseValue())*(-1+1/FLUX_FACTOR));
		*/
		stats.getArmorBonus().modifyMult(id, ARMOR_MULT);
		stats.getEffectiveArmorBonus().modifyMult(id, 1/ARMOR_MULT);
		stats.getMinArmorFraction().modifyMult(id, 1/ARMOR_MULT);

	}
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		int bonus = RANGE_BOOST;
		ship.addListener(new RedundantTurretry.RangefinderRangeModifier(bonus));
	}
	public static class RangefinderRangeModifier implements WeaponBaseRangeModifier {
		public float bonus, max;
		public RangefinderRangeModifier(int bonus) {
			this.bonus = bonus;
		}
		public float getWeaponBaseRangePercentMod(ShipAPI ship, WeaponAPI weapon) {
			return 0;
		}
		public float getWeaponBaseRangeMultMod(ShipAPI ship, WeaponAPI weapon) {
			return 1f;
		}
		public float getWeaponBaseRangeFlatMod(ShipAPI ship, WeaponAPI weapon) {
			if (weapon.getSlot() == null && weapon.getType() != WeaponAPI.WeaponType.MISSILE) {
				return 0f;
			}
			return bonus;
		}
	}



	public String getDescriptionParam(int index, HullSize hullSize) {
		//if (index == 0) return "" + -1*SMALL_DISCOUNT + "/" + -1*MEDIUM_DISCOUNT + "/" + -1*LARGE_DISCOUNT;
		//if (index == 1) return "" + (int) (1-ROF_FACTOR)*(100) + "%";
		//if (index == 2) return "" + (int) (FLUX_FACTOR-1)*(100) + "%";
		if (index == 0) return "" + (int) (ARMOR_MULT)+"";
		if (index == 1) return "" + (int) (RANGE_BOOST) + " su";
		return null;
	}
/*
	@Override
	public boolean affectsOPCosts() {
		return true;
	}
*/
}
