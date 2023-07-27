package org.hatter.epitaphFrost.shipsystems;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.Global;
//mix of temporal shell / damper 
public class PhaseShell extends BaseShipSystemScript {

	public static final float DAMAGE_FRACTION = 0.4f;
	public static final float EMP_FRACTION = 0.1f;
	public static final float MAX_TIME_MULT = 3f;
	public static final float EFFECTIVE_MOBILITY = 1.1f;

	
	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
		float shipTimeMult = 1f + (MAX_TIME_MULT - 1f) * effectLevel;
		stats.getTimeMult().modifyMult(id, shipTimeMult);
		ShipAPI ship = null;
		boolean player = false;
		if (stats.getEntity() instanceof ShipAPI) {
			ship = (ShipAPI) stats.getEntity();
			player = ship == Global.getCombatEngine().getPlayerShip();
			id = id + "_" + ship.getId();
		} else {
			return;
		}
		if (player) {
			Global.getCombatEngine().getTimeMult().modifyMult(id, 1f / shipTimeMult);
		} else {
			Global.getCombatEngine().getTimeMult().unmodify(id);
		}
		stats.getHullDamageTakenMult().modifyMult(id, 1f - (1f - DAMAGE_FRACTION) * effectLevel);
		stats.getArmorDamageTakenMult().modifyMult(id, 1f - (1f - DAMAGE_FRACTION) * effectLevel);
		stats.getEmpDamageTakenMult().modifyMult(id, 1f - (1f - EMP_FRACTION) * effectLevel);
		stats.getMaxSpeed().modifyMult(id, 1f + ((EFFECTIVE_MOBILITY/MAX_TIME_MULT) - 1f) * effectLevel);
		stats.getAcceleration().modifyMult(id, 1f + ((EFFECTIVE_MOBILITY/MAX_TIME_MULT) - 1f) * effectLevel);
		//stats.getDeceleration().modifyMult(id, 1f + ((EFFECTIVE_MOBILITY/MAX_TIME_MULT) - 1f) * effectLevel);
		stats.getMaxTurnRate().modifyMult(id, 1f + ((EFFECTIVE_MOBILITY/MAX_TIME_MULT) - 1f) * effectLevel);
		stats.getTurnAcceleration().modifyMult(id, 1f + ((EFFECTIVE_MOBILITY/MAX_TIME_MULT) - 1f) * effectLevel);
		
		
	}
	public void unapply(MutableShipStatsAPI stats, String id) {
		ShipAPI ship = null;
		boolean player = false;
		if (stats.getEntity() instanceof ShipAPI) {
			ship = (ShipAPI) stats.getEntity();
			player = ship == Global.getCombatEngine().getPlayerShip();
			id = id + "_" + ship.getId();
		} else {
			return;
		}
		Global.getCombatEngine().getTimeMult().unmodify(id);
		stats.getTimeMult().unmodify(id);
		stats.getHullDamageTakenMult().unmodify(id);
		stats.getArmorDamageTakenMult().unmodify(id);
		stats.getEmpDamageTakenMult().unmodify(id);
		stats.getMaxSpeed().unmodify(id);
		stats.getAcceleration().unmodify(id);
		//stats.getDeceleration().unmodify(id);
		stats.getMaxTurnRate().unmodify(id);
		stats.getTurnAcceleration().unmodify(id);
	}
	
	public StatusData getStatusData(int index, State state, float effectLevel) {

		if (index == 0) {
			return new StatusData("Phase Shell " + (int) (100*(effectLevel)) + "%", false);
		}

		return null;
	}
}
