package org.hatter.epitaphFrost.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;


public class AuxiliaryTurbines extends BaseHullMod {

    public static final int SPEED_BOOST  = 50;
    public static final float ACCEL_BOOST  = 1.5f;
    public static final float FLUX_LEVEL  = 0.01f;
    public static final float ENGINE_SIZE  = 0.5f;
    //public static final float FLUX_PENALTY  = 0.5f;
    /*
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
    }
    */
    public void advanceInCombat(ShipAPI ship, float amount) {
        // Refit screen check
        if((ship.getFluxLevel() < FLUX_LEVEL) && !(ship.getOriginalOwner() == -1)){

            ship.getMutableStats().getMaxSpeed().modifyFlat("Aux Turbine",SPEED_BOOST);
            ship.getMutableStats().getAcceleration().modifyMult("Aux Turbine",ACCEL_BOOST);
            ship.getMutableStats().getTurnAcceleration().modifyMult("Aux Turbine",ACCEL_BOOST);
            ship.getMutableStats().getDeceleration().modifyMult("Aux Turbine",ACCEL_BOOST);
            ship.getEngineController().extendFlame(this, ENGINE_SIZE, ENGINE_SIZE, ENGINE_SIZE);
            if (ship == Global.getCombatEngine().getPlayerShip()) {
                Global.getCombatEngine().maintainStatusForPlayerShip(
                        "Aux Turbine",
                        "graphics/EpitaphFrost/icons/hullsys/superconductive_cycler.png",
                        "Aux Turbine",
                        "Low flux boost: " +SPEED_BOOST+" su",
                        false);
            }
        }
        else{
            ship.getMutableStats().getMaxSpeed().unmodify("Aux Turbine");
            ship.getMutableStats().getAcceleration().unmodify("Aux Turbine");
            ship.getMutableStats().getTurnAcceleration().unmodify("Aux Turbine");
            ship.getMutableStats().getDeceleration().unmodify("Aux Turbine");
        }
    }

    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index == 0) return "" + (SPEED_BOOST)  + " su";
        if (index == 1) return "" +  (int) ((ACCEL_BOOST-1)*100)  + "%";
        if (index == 2) return "" +  (int) ((FLUX_LEVEL)*100)  + "%";
        return null;
    }
    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        return getUnapplicableReason(ship) == null;
    }

    public String getUnapplicableReason(ShipAPI ship) {
        if (ship != null && (ship.getHullSize()!= ShipAPI.HullSize.CAPITAL_SHIP &&
                ship.getHullSize() != ShipAPI.HullSize.CRUISER)) {
            return "Can only be installed on cruiser-class hulls and larger";
        }
        return null;
    }

}


