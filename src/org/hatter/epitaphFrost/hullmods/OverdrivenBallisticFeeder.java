package org.hatter.epitaphFrost.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI;
import com.fs.starfarer.api.loading.WeaponSpecAPI;
import com.fs.starfarer.api.loading.ProjectileWeaponSpecAPI;

import java.util.HashMap;
import java.util.Map;

public class OverdrivenBallisticFeeder extends BaseHullMod {

	public static final int LARGE_PENALTY_BALLISTIC  = 10;
	public static final int MEDIUM_PENALTY_BALLISTIC  = 5;
	public static final int SMALL_PENALTY_BALLISTIC  = 2;
	public static final float ROF_BOOST  = 2f;
	public static final float RECOIL_PENALTY  = 1.5f;
	public static final float RECOIL_MAX_MULT = 1.25f;
	//public static final float DEFAULT_INACCURACY = 12f;
	//public static final float DEFAULT_RECOIL_FACTOR = 3f;
	//public static final float DEFAULT_DECAY_FACTOR = 6f;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getDynamic().getMod(Stats.LARGE_BALLISTIC_MOD).modifyFlat(id, LARGE_PENALTY_BALLISTIC);
		stats.getDynamic().getMod(Stats.MEDIUM_BALLISTIC_MOD).modifyFlat(id, MEDIUM_PENALTY_BALLISTIC);
		stats.getDynamic().getMod(Stats.SMALL_BALLISTIC_MOD).modifyFlat(id,  SMALL_PENALTY_BALLISTIC);
		stats.getBallisticRoFMult().modifyMult(id,ROF_BOOST);
		stats.getBallisticAmmoRegenMult().modifyMult(id,ROF_BOOST);
		if(!isSMod(stats)) {
			stats.getRecoilPerShotMult().modifyMult(id, RECOIL_PENALTY);
			stats.getRecoilDecayMult().modifyMult(id, 1 / RECOIL_PENALTY);
			stats.getMaxRecoilMult().modifyMult(id, RECOIL_MAX_MULT);
		}
		/*
		for (String slotId : stats.getVariant().getFittedWeaponSlots()) {
			WeaponSpecAPI weaponSpec = stats.getVariant().getWeaponSpec(slotId);
			if ((weaponSpec instanceof ProjectileWeaponSpecAPI) && (weaponSpec.getType() == WeaponAPI.WeaponType.BALLISTIC)) {
				//todo- adjust to hit higher accuracy weapons harder, modulate recoil gain in proportion to fire-rate
				if(weaponSpec.getMaxSpread() == 0) {
					float current_recoil = weaponSpec.getMaxSpread();
					float calculated_recoil = current_recoil + DEFAULT_INACCURACY*(1 - ((current_recoil)/(current_recoil + DEFAULT_INACCURACY)));
					weaponSpec.setMaxSpread(calculated_recoil);
				}
				if(weaponSpec.getSpreadBuildup() == 0) {
					weaponSpec.setSpreadBuildup(DEFAULT_RECOIL_FACTOR);
				}
				if(weaponSpec.getSpreadDecayRate() == 0) {
					weaponSpec.setSpreadDecayRate(DEFAULT_DECAY_FACTOR);
				}
			}
		}
		*/
	}
	//following courtesy of Tomatopaste, DarkRevenant. See EF_OEM
	private static final Map<WeaponAPI.WeaponSize, Integer> BALLISTIC_PENALTIES = new HashMap<WeaponAPI.WeaponSize, Integer>();
	{
		BALLISTIC_PENALTIES.put(WeaponAPI.WeaponSize.SMALL, SMALL_PENALTY_BALLISTIC);
		BALLISTIC_PENALTIES.put(WeaponAPI.WeaponSize.MEDIUM, MEDIUM_PENALTY_BALLISTIC);
		BALLISTIC_PENALTIES.put(WeaponAPI.WeaponSize.LARGE, LARGE_PENALTY_BALLISTIC);
	}
	private boolean enoughOPToInstallBallistic(ShipAPI ship) {
		int opRequiredToInstall = spec.getCostFor(ship.getHullSize());
		FleetMemberAPI member = ship.getFleetMember();
		MutableCharacterStatsAPI stats = null;
		if ((member != null) && (member.getFleetCommanderForStats() != null)) {
			stats = member.getFleetCommanderForStats().getFleetCommanderStats();
		}
		int unused_OP_Ballistic = ship.getVariant().getUnusedOP(stats);
		for (String slotId : ship.getVariant().getFittedWeaponSlots()) {
			WeaponSpecAPI weaponSpec = ship.getVariant().getWeaponSpec(slotId);
			if (weaponSpec.getType() == WeaponAPI.WeaponType.BALLISTIC) {
				//does not account for hull mods that multiply OP.
				opRequiredToInstall += BALLISTIC_PENALTIES.get(weaponSpec.getSize());
				//weaponSpec.getOrdnancePointCost()
			}

		}
		return unused_OP_Ballistic >= opRequiredToInstall;
	}

	@Override
	public String getUnapplicableReason(ShipAPI ship) {
		if (!enoughOPToInstallBallistic(ship) && !ship.getVariant().hasHullMod("ef_overdriven_ballistic_feeder")) {
			return "Insufficient ordnance points due to increased cost";
		}
		return super.getUnapplicableReason(ship);
		//return getUnapplicableReason(ship);
	}
	@Override
	public boolean isApplicableToShip(ShipAPI ship) {
		if (!enoughOPToInstallBallistic(ship) && !ship.getVariant().hasHullMod("ef_overdriven_ballistic_feeder")) {
			return false;
		}
		return super.isApplicableToShip(ship);
		//return isApplicableToShip(ship);
	}


	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) (ROF_BOOST-1)*(100) + "%";
		if (index == 1) return "" +  SMALL_PENALTY_BALLISTIC + "/"+  MEDIUM_PENALTY_BALLISTIC + "/"+  LARGE_PENALTY_BALLISTIC + "";
		if (index == 2) return "" + (int) (100*RECOIL_PENALTY-100) + "%";
		return null;
	}

	@Override
	public boolean affectsOPCosts() {
		return true;
	}

}
