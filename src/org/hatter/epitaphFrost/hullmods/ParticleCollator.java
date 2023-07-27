package org.hatter.epitaphFrost.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;

public class ParticleCollator extends BaseHullMod {
    public static final int ENERGY_SPEED_BOOST = 35;
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getEnergyProjectileSpeedMult().modifyPercent(id, ENERGY_SPEED_BOOST );
    }

    public void advanceInCombat(ShipAPI ship, float amount) {

        if (!ship.isAlive()) {
            return;
        }
        float Beam_Factor =  ship.getMutableStats().getEnergyRoFMult().getModifiedValue() ;
        if (ship.isAlive()) {
            ship.getMutableStats().getBeamWeaponDamageMult().modifyMult("Collator", Beam_Factor);
            ship.getMutableStats().getBeamWeaponFluxCostMult().modifyMult("Collator", Beam_Factor);
            /*
            if (ship == Global.getCombatEngine().getPlayerShip()) {
                Global.getCombatEngine().maintainStatusForPlayerShip("Collator", "graphics/EpitaphFrost/icons/hullsys/superconductive_cycler.png", "Collator", "" + Beam_Factor*100 +"%", false);
            }
            */
        }
        //else {}
    }
    public String getDescriptionParam(int index, HullSize hullSize) {
        if (index == 0) return "" + ENERGY_SPEED_BOOST + "%";
        return null;
    }
}