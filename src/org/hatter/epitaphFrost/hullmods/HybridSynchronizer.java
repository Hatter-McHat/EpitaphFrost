package org.hatter.epitaphFrost.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI;
import com.fs.starfarer.api.loading.WeaponSlotAPI;
import com.fs.starfarer.api.loading.WeaponSpecAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.listeners.WeaponBaseRangeModifier;

import com.fs.starfarer.api.combat.BeamAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponType;
import com.fs.starfarer.api.combat.listeners.DamageDealtModifier;
import com.fs.starfarer.api.util.Misc;

import org.lwjgl.util.vector.Vector2f;

import java.lang.Math;

import static com.fs.starfarer.api.impl.campaign.ids.Skills.BALLISTIC_MASTERY;
import static com.fs.starfarer.api.impl.campaign.ids.Skills.ENERGY_WEAPON_MASTERY;

public class HybridSynchronizer extends BaseHullMod {

    public static final int ENERGY_RANGE_BOOST  = 200;
    public static final int ENERGY_RANGE_MAX  = 900;
    public static final int ENERGY_PROJ_SPEED_BONUS = 33;
    public static final int ENERGY_DAMAGE_BONUS = 10;
    public static final int ENERGY_RANGE_BONUS = 10;

    public static final float BALLISTIC_FLUX_MULT = 0.9f;

    public static final int BALLISTIC_MIN_RANGE = 900;
    public static final int BALLISTIC_MAX_RANGE = 1300;

    public static final int BALLISTIC_DAMAGE_PERCENT = 30;
    public static final int BALLISTIC_DAMAGE_MIN_FLUX_LEVEL = 0;
    private static final String HYBRID_SYNCH = "Hybrid Synchronizer";
    private static final float BALLISTIC_FLUX_MAX_REDUCTION = 0.8f;
    //public static final float ENERGY_DAMAGE_MIN_FLUX_LEVEL = 0f;






    public void advanceInCombat(ShipAPI ship, float amount) {
        MutableShipStatsAPI stats = ship.getMutableStats();

        float reduction = 1;
        if(energy_op(ship) > ballistic_op(ship) && ballistic_op(ship)>0){
            reduction = BALLISTIC_FLUX_MAX_REDUCTION;
        }
        else if(ballistic_op(ship)>0){
            reduction = 1 - ((energy_op(ship) / ballistic_op(ship)) * (1-BALLISTIC_FLUX_MAX_REDUCTION));
        }
        ship.getMutableStats().getBallisticWeaponFluxCostMod().modifyMult(HYBRID_SYNCH, reduction);
        if (ship.getCaptain() != null) {
            PersonAPI officer = ship.getCaptain();
            if(officer.getStats().hasSkill(BALLISTIC_MASTERY)){
                stats.getEnergyWeaponDamageMult().modifyPercent(HYBRID_SYNCH, ENERGY_DAMAGE_BONUS);
                stats.getEnergyWeaponRangeBonus().modifyPercent(HYBRID_SYNCH, ENERGY_RANGE_BONUS);
                if(officer.getStats().getSkillLevel(BALLISTIC_MASTERY)>1){
                    stats.getEnergyProjectileSpeedMult().modifyPercent(HYBRID_SYNCH, ENERGY_PROJ_SPEED_BONUS);
                }
            }
            if(officer.getStats().hasSkill(ENERGY_WEAPON_MASTERY)) {
                stats.getEnergyWeaponFluxBasedBonusDamageMagnitude().modifyFlat(HYBRID_SYNCH, BALLISTIC_DAMAGE_PERCENT * .01f);
                stats.getEnergyWeaponFluxBasedBonusDamageMinLevel().modifyFlat(HYBRID_SYNCH, BALLISTIC_DAMAGE_MIN_FLUX_LEVEL);
                if (officer.getStats().getSkillLevel(ENERGY_WEAPON_MASTERY) > 1) {
                    stats.getBallisticWeaponFluxCostMod().modifyMult(HYBRID_SYNCH, BALLISTIC_FLUX_MULT);
                }
            }
        }
    }

    private float ballistic_op(ShipAPI ship) {

        if(ship == null) {
            return 0;
        }
        /*
        if(ship.getFleetMember() == null) {
            return 100002;
        }
        if(ship.getFleetMember().getFleetCommander() == null) {
            return 100003;
        }
        */
        MutableCharacterStatsAPI stats = Global.getSector().getPlayerStats();;
        int ballistic_op_subtotal = 0;
        for (String slotId : ship.getVariant().getFittedWeaponSlots()) {
            WeaponSpecAPI weaponSpec = ship.getVariant().getWeaponSpec(slotId);
            WeaponSlotAPI weaponSlot = ship.getVariant().getSlot(slotId);
            if (weaponSpec.getMountType()  == WeaponAPI.WeaponType.BALLISTIC || weaponSlot.getWeaponType() == WeaponAPI.WeaponType.BALLISTIC) {
                ballistic_op_subtotal += weaponSpec.getOrdnancePointCost(stats, ship.getMutableStats());

            }
        }
        return ballistic_op_subtotal;
    }
    private float energy_op(ShipAPI ship) {

        if(ship == null) {
            return 0;
        }
        /*
        if(ship.getFleetMember() == null) {
            return 100002;
        }
        if(ship.getFleetMember().getFleetCommander() == null) {
            return 100003;
        }
        */
        MutableCharacterStatsAPI stats = Global.getSector().getPlayerStats();
        int energy_op_subtotal = 0;
        for (String slotId : ship.getVariant().getFittedWeaponSlots()) {
            WeaponSpecAPI weaponSpec = ship.getVariant().getWeaponSpec(slotId);
            WeaponSlotAPI weaponSlot = ship.getVariant().getSlot(slotId);
            if (weaponSpec.getMountType() == WeaponAPI.WeaponType.ENERGY || weaponSlot.getWeaponType() == WeaponAPI.WeaponType.ENERGY) {
                energy_op_subtotal += weaponSpec.getOrdnancePointCost(stats, ship.getMutableStats());

            }
        }
        return energy_op_subtotal;
    }

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {

        float bonus = 0;
        int max = ENERGY_RANGE_MAX;
        if(energy_op(ship)>0) {
            bonus = Math.round(((float)ballistic_op(ship) / (float)energy_op(ship)) * (float)ENERGY_RANGE_BOOST);
        }
        bonus = Math.min((int)ENERGY_RANGE_BOOST,(int)bonus);
        ship.addListener(new RangefinderRangeModifier((int)bonus, max));
        ship.addListener(new EWMDamageDealtMod_Ballistic(ship));
    }


    public static class RangefinderRangeModifier implements WeaponBaseRangeModifier {
        public float bonus, max;
        public RangefinderRangeModifier(int bonus, int max) {
            this.bonus = bonus;
            this.max = max;
        }
        public float getWeaponBaseRangePercentMod(ShipAPI ship, WeaponAPI weapon) {
            return 0;
        }
        public float getWeaponBaseRangeMultMod(ShipAPI ship, WeaponAPI weapon) {
            return 1f;
        }
        public float getWeaponBaseRangeFlatMod(ShipAPI ship, WeaponAPI weapon) {
            if (weapon.getSlot() == null || weapon.getType() != WeaponAPI.WeaponType.ENERGY) {
                return 0f;
            }
            /*
            if (weapon.hasAIHint(WeaponAPI.AIHints.PD)) {
                return 0f;
            };
            */
            if (bonus == 0f) return 0f;

            float base = weapon.getSpec().getMaxRange();
            if (base + bonus > max) {
                bonus = max - base;
            }
            if (bonus < 0) bonus = 0;
            return bonus;
        }
    }
   // public static class EWMDamageDealtMod_Ballistic implements DamageDealtModifier, AdvanceableListener {
   public static class EWMDamageDealtMod_Ballistic implements DamageDealtModifier {
        protected ShipAPI ship;
        public EWMDamageDealtMod_Ballistic(ShipAPI ship) {
            this.ship = ship;
        }
        public String modifyDamageDealt(Object param,
                                        CombatEntityAPI target, DamageAPI damage,
                                        Vector2f point, boolean shieldHit) {
            Vector2f from = null;
            WeaponAPI weapon = null;
            if (param instanceof DamagingProjectileAPI) {
                from = ((DamagingProjectileAPI)param).getSpawnLocation();
                weapon = ((DamagingProjectileAPI)param).getWeapon();
            } else if (param instanceof BeamAPI) {
                from = ((BeamAPI)param).getFrom();
                weapon = ((BeamAPI)param).getWeapon();
            } else {
                return null;
            }

            if (weapon == null || ship == null) return null;
            if (weapon.getSpec().getType() != WeaponType.BALLISTIC) return null;

            float mag = ship.getFluxBasedEnergyWeaponDamageMultiplier() - 1f;
            if (mag <= 0) return null;

            float dist = Misc.getDistance(from, point);
            float f = 1f;
            if (dist > BALLISTIC_MAX_RANGE) {
                f = 0f;
            } else if (dist > BALLISTIC_MIN_RANGE) {
                f = 1f - (dist - BALLISTIC_MIN_RANGE) / (BALLISTIC_MAX_RANGE - BALLISTIC_MIN_RANGE);
            }
            if (f < 0) f = 0;
            if (f > 1) f = 1;
            String id = "ewm_dam_mod_ballistic";
            damage.getModifier().modifyPercent(id, (mag * f) );
            return id;
        }
    }
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize, ShipAPI ship)  {
        float reduct_actual = 1;
        if (energy_op(ship) > ballistic_op(ship) && ballistic_op(ship)>0){
            reduct_actual = BALLISTIC_FLUX_MAX_REDUCTION;
        }
        else if(ballistic_op(ship)>0)  {
            reduct_actual = 1- (( energy_op(ship) /ballistic_op(ship) ) * (1-BALLISTIC_FLUX_MAX_REDUCTION));
        }
        float range_actual = 0;
        if(energy_op(ship)>0) {
            range_actual = Math.round((ballistic_op(ship) / energy_op(ship)) * ENERGY_RANGE_BOOST);
        }
        range_actual = Math.min((int)ENERGY_RANGE_BOOST,(int)range_actual);
        if (index == 0) return ENERGY_RANGE_BOOST + " (" + range_actual +")";
        if (index == 1) return ENERGY_RANGE_MAX + " su";
        if (index == 2) return  Math.round((1-BALLISTIC_FLUX_MAX_REDUCTION)*100) + "% (" + Math.round((1-reduct_actual) * 100) + "%)";
        if (index == 3) return   (int)ballistic_op(ship) + "/" + (int)energy_op(ship);
        if (index == 4) return BALLISTIC_MIN_RANGE + "-"+ BALLISTIC_MAX_RANGE;
        return null;
    }
}