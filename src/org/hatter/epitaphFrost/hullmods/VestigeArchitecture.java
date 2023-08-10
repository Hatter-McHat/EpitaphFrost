package org.hatter.epitaphFrost.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponType;
import com.fs.starfarer.api.combat.listeners.WeaponBaseRangeModifier;
import com.fs.starfarer.api.combat.ArmorGridAPI;
import org.lazywizard.lazylib.combat.DefenseUtils;
import com.fs.starfarer.api.combat.listeners.AdvanceableListener;


public class VestigeArchitecture extends BaseHullMod {

    public static final float BASE_REGENERATION_PER_SEC_PERCENT = 3f;
    public static final float LOW_FLUX_REGENERATION_PER_SEC_PERCENT = 15f;
    public static final float KINETIC_ARMOR_DAMAGE_MULT= 2f;
    public static final float VENT_RATE_MULT= 1.5f;
    public static final float BASE_DISSIPITATION_MULT= 0.5f;
    public static final float DISSIPITATION_PER_PERCENT_FLUX_MULT= 2f;
    public static final float BALLISTIC_RANGE_REDUCTION  = -200f;
    public static final float BALLISTIC_MIN_RANGE  = 500f;
    public static final float MIN_ARMOR_REDUCTION  = 0.5f;
    public static final float RECOIL_MAX_REDUCTION = 0.2f;
    public static final float HULL_ARMOR_CONVERSION_FACTOR = 0.7f;

    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getKineticDamageTakenMult().modifyMult(id, KINETIC_ARMOR_DAMAGE_MULT);
        stats.getVentRateMult().modifyMult(id, VENT_RATE_MULT);
        stats.getFluxDissipation().modifyMult(id, BASE_DISSIPITATION_MULT);
        stats.getMinArmorFraction().modifyMult(id, MIN_ARMOR_REDUCTION);
        stats.getMaxRecoilMult().modifyMult(id, RECOIL_MAX_REDUCTION );
    }
    public void advanceInCombat(ShipAPI ship, float amount) {
        ship.getMutableStats().getFluxDissipation().modifyMult("Vestige Dissipitation",1+DISSIPITATION_PER_PERCENT_FLUX_MULT*ship.getFluxLevel());
    }
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        super.applyEffectsAfterShipCreation(ship, id);
        float reduction = BALLISTIC_RANGE_REDUCTION;
        float min = BALLISTIC_MIN_RANGE;
        ship.removeListenerOfClass(VestigeArmorRegen.class);
        ship.addListener(new VestigeArmorRegen(ship));
        ship.addListener(new VestigeRangeModifier(reduction,min));
    }
    public static class VestigeRangeModifier implements WeaponBaseRangeModifier {
        public float reduction, min;
        public VestigeRangeModifier(float reduction, float min) {
            this.reduction = reduction;
            this.min = min;
        }

        public float getWeaponBaseRangePercentMod(ShipAPI ship, WeaponAPI weapon) {
            return 0;
        }
        public float getWeaponBaseRangeMultMod(ShipAPI ship, WeaponAPI weapon) {
            return 1f;
        }
        public float getWeaponBaseRangeFlatMod(ShipAPI ship, WeaponAPI weapon) {
            if (weapon.getSlot() == null || weapon.getType() != WeaponType.BALLISTIC) {
                return 0f;
            }

            float base = weapon.getSpec().getMaxRange();
            if (base + reduction < min) {
                reduction = min - base;
            }
            /*
            if (reduction > 0){
                reduction = 0;
            }
            */
            return reduction;
        }
    }
    //code from SirHartley Substance Abuse
    public static class VestigeArmorRegen implements AdvanceableListener {
        protected ShipAPI ship;
        //protected float fraction;

        public VestigeArmorRegen(ShipAPI ship) {
            this.ship = ship;
        }

        public void advance(float amount) {
            if (!DefenseUtils.hasArmorDamage(ship)) return;
            if (ship.isHulk()) return;
            if (ship.getHitpoints()<0.01*ship.getMaxHitpoints()) return; // no regen for dying ships

            ArmorGridAPI armorGrid = ship.getArmorGrid();
            final float[][] grid = armorGrid.getGrid();
            final float max = armorGrid.getMaxArmorInCell();
            float regenState = BASE_REGENERATION_PER_SEC_PERCENT;
            if(ship.getFluxLevel() < 0.01){
                regenState = LOW_FLUX_REGENERATION_PER_SEC_PERCENT;
            }
            float repairAmount = (max * amount / 100 )*(regenState) ;

            // Iterate through all armor cells and find any that aren't at max
            for (int x = 0; x < grid.length; x++) {
                for (int y = 0; y < grid[0].length; y++) {
                    if (grid[x][y] < max) {
                        float regen = Math.min(grid[x][y] + repairAmount, max);
                        armorGrid.setArmorValue(x, y, regen);
                        //
                        ship.setHitpoints(ship.getHitpoints()-(HULL_ARMOR_CONVERSION_FACTOR*repairAmount));
                    }
                }
            }
        }
    }
    public String getDescriptionParam(int index, HullSize hullSize) {
        if (index == 0) return "" + (int) (BASE_REGENERATION_PER_SEC_PERCENT)*(100) + "%";
        if (index == 1) return "" + (int) (HULL_ARMOR_CONVERSION_FACTOR )*(100) + "%";
        if (index == 2) return "" + (int) (KINETIC_ARMOR_DAMAGE_MULT-1)*(100) + "%";
        if (index == 3) return "" + (int) (LOW_FLUX_REGENERATION_PER_SEC_PERCENT)*(100) + "%";
        if (index == 4) return "" + (int) (BALLISTIC_RANGE_REDUCTION)*(-1) + " su";
        if (index == 5) return "" + (int) (BALLISTIC_MIN_RANGE) + " su";
        if (index == 6) return "" + Math.round((1-RECOIL_MAX_REDUCTION)*(100)) + "%";
        //if (index == 7) return "" + (int) (1-BASE_DISSIPITATION_MULT)*(100) + "%";
        //if (index == 8) return "" + (int) (BASE_DISSIPITATION_MULT)*(1+DISSIPITATION_PER_PERCENT_FLUX_MULT*100)*(100) + "%";
        //if (index == 9) return "" + (int) (1-VENT_RATE_MULT)*(100) + "%";
        if (index == 7) return "" + "50" + "%";
        if (index == 8) return "" +  "150" + "%";
        if (index == 9) return "" + "50" + "%";
        return null;
    }
}