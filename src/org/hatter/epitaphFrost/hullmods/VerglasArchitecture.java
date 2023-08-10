package org.hatter.epitaphFrost.hullmods;



import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.DamageTakenModifier;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;


public class VerglasArchitecture extends BaseHullMod {


	public static final float ROF_FACTOR  = 3f;
	public static final float FLUX_MULT = 0.5f;
	public static final float HE_SHIELD_MULT = 2f;
	public static final float SHIELD_REDUCT_FLAT = 250f;
	public static final float SHIELD_REDUCT  = 0.9f;
	public static final float ARMOR_DAMAGE_CUTOFF  = 250f;
	public static final float ARMOR_REDUCT = 0.9f;
	public static final float OVERLOAD_MULT = 0.33f;
	public static final float  SHIELD_SPEED = 3f;


	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getBallisticRoFMult().modifyMult(id,ROF_FACTOR);
		stats.getBallisticAmmoRegenMult().modifyMult(id,ROF_FACTOR);
		stats.getBallisticWeaponFluxCostMod().modifyMult(id,1/ROF_FACTOR);
		stats.getEnergyRoFMult().modifyMult(id,ROF_FACTOR);
		stats.getEnergyAmmoRegenMult().modifyMult(id,ROF_FACTOR);
		stats.getEnergyWeaponFluxCostMod().modifyMult(id,1/ROF_FACTOR);
		stats.getMissileRoFMult().modifyMult(id,ROF_FACTOR);
		stats.getMissileAmmoRegenMult().modifyMult(id,ROF_FACTOR);
		stats.getMissileWeaponFluxCostMod().modifyMult(id,1/ROF_FACTOR);

		stats.getFluxDissipation().modifyMult(id, FLUX_MULT);
		stats.getFluxCapacity().modifyMult(id, FLUX_MULT);
		//prevent hitting base stats
		stats.getFluxDissipation().modifyFlat(id, (stats.getFluxDissipation().getBaseValue())*((1/FLUX_MULT)-1));
		stats.getFluxCapacity().modifyFlat(id, (stats.getFluxCapacity().getBaseValue())*((1/FLUX_MULT)-1));

		stats.getHighExplosiveShieldDamageTakenMult().modifyMult(id,HE_SHIELD_MULT);
		stats.getOverloadTimeMod().modifyMult(id,OVERLOAD_MULT);
		stats.getShieldTurnRateMult().modifyMult(id,SHIELD_SPEED);
		stats.getShieldUnfoldRateMult().modifyMult(id,SHIELD_SPEED);


	}
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id)
	{

		if (!ship.hasListenerOfClass(VerglasDamageListener.class))
			ship.addListener(new VerglasDamageListener(ship));
	}

	//originally ApexShieldListener and Elite Damage Control-
	public static class VerglasDamageListener implements DamageTakenModifier
	{
		protected ShipAPI ship;

		public VerglasDamageListener(ShipAPI ship)
		{
			this.ship = ship;
		}
		public String modifyDamageTaken(Object param, CombatEntityAPI target, DamageAPI damage, Vector2f point, boolean shieldHit)
		{
			if (damage.getDamage() <= 0)
				return null;
			if (shieldHit) {
				float originalDamage = damage.computeDamageDealt(Global.getCombatEngine().getElapsedInLastFrame());
				//reduce by SHIELD_REDUCT_FLAT by a minimum of SHIELD_REDUCT
				float reduction = Math.min(originalDamage*SHIELD_REDUCT,SHIELD_REDUCT_FLAT);
				if (reduction > originalDamage*SHIELD_REDUCT)
					reduction = originalDamage*SHIELD_REDUCT;
				damage.getModifier().modifyMult("efVerglasDamageListener", 1-reduction / originalDamage);
			}
			if (!shieldHit) {
				ship.setNextHitHullDamageThresholdMult(ARMOR_DAMAGE_CUTOFF, (1-ARMOR_REDUCT));
			}
			return null;
		}
	}
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) ((ROF_FACTOR - 1) * (100)) + "%";
		if (index == 1) return "" + SHIELD_REDUCT_FLAT;
		if (index == 2) return "" + (int) ((SHIELD_REDUCT ) * (100)) + "%";
		if (index == 3) return "" + (int) ((HE_SHIELD_MULT-1) * (100)) + "%";
		if (index == 4) return "" + ARMOR_DAMAGE_CUTOFF;
		if (index == 5) return "" + (int) ((ARMOR_REDUCT ) * (100)) + "%";
		if (index == 6) return "" + (int) ((1-FLUX_MULT) * (100)) + "%";
		return null;
	}
}
