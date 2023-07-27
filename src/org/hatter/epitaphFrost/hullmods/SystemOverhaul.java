package org.hatter.epitaphFrost.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;


public class SystemOverhaul extends BaseHullMod {

    public static final float SYSTEM_REGEN = 1.8f;
    public static final int SYSTEM_CHARGES  = 2;
    public static final int SYSTEM_RANGE  = 50;
    public static final float SYSTEM_REGEN_PHASE = 1.4f;
    public static final int SYSTEM_CHARGES_PHASE  = 1;
    public static final int SYSTEM_RANGE_PHASE  = 25;

    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        float system_regen_bonus =  SYSTEM_REGEN;
        float system_range_bonus = SYSTEM_CHARGES;
        float system_charge_bonus = SYSTEM_CHARGES;
        if(stats.getVariant().getHullSpec().isPhase()){
            system_regen_bonus =  SYSTEM_REGEN_PHASE;
            system_range_bonus = SYSTEM_CHARGES_PHASE;
            system_charge_bonus = SYSTEM_CHARGES_PHASE;
        }
        stats.getSystemCooldownBonus().modifyMult(id,(1/system_regen_bonus));
        stats.getSystemRegenBonus().modifyMult(id,system_regen_bonus);
        stats.getSystemRangeBonus().modifyPercent(id,system_range_bonus);
        stats.getSystemUsesBonus().modifyFlat(id,system_charge_bonus);
    }
/*
    public void advanceInCombat(ShipAPI ship, float amount) {
        Global.getCombatEngine().maintainStatusForPlayerShip("diag", "", "SR", ship.getMutableStats().getSystemCooldownBonus().getBonusMult() + " " + ship.getMutableStats().getSystemRegenBonus().getBonusMult(), true);
        if (!ship.isAlive()) {
            return;
        }
        if (!ship.isPhased()) {
            ship.getMutableStats().getSystemCooldownBonus().modifyMult("system_phase",(1/SYSTEM_REGEN));
            ship.getMutableStats().getSystemRegenBonus().modifyMult("system_phase",SYSTEM_REGEN);
            if (ship == Global.getCombatEngine().getPlayerShip()) {
                Global.getCombatEngine().maintainStatusForPlayerShip("system_phase", "graphics/EpitaphFrost/icons/hullsys/superconductive_cycler.png", "System acceleration normal", "" , true);
            }
        }
        else {
            ship.getMutableStats().getSystemCooldownBonus().unmodify("system_phase");
            ship.getMutableStats().getSystemRegenBonus().unmodify("system_phase");
            if (ship == Global.getCombatEngine().getPlayerShip()) {
                Global.getCombatEngine().maintainStatusForPlayerShip("system_phase", "graphics/EpitaphFrost/icons/hullsys/superconductive_cycler.png", "System acceleration halted", "", true);
            }
        }
    }
    //can't advance in combat, use -
    ship.getSystem().setCooldown(ship.getSystem().getCooldown()-(amount*whateveryournumberis)) - from Timid
 */
    public String getDescriptionParam(int index, HullSize hullSize) {
        if (index == 0) return "" + (int) (SYSTEM_REGEN*100-100) + "%" + "/"  + (int) (SYSTEM_REGEN_PHASE*100-100) + "%";
        if (index == 1) return "" +   + SYSTEM_CHARGES + ""+ "/" + SYSTEM_CHARGES_PHASE + "";
        if (index == 2) return "" +  SYSTEM_RANGE + "%"+ "/" + SYSTEM_RANGE_PHASE + "%" ;


        return null;
    }
    /*
    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        return !ship.getHullSpec().isPhase();
    }

    @Override
    public String getUnapplicableReason(ShipAPI ship) {
        if (!ship.getHullSpec().isPhase()) {
            return "Cannot be installed on phase ships";
        }
        return super.getUnapplicableReason(ship);
    }
*/
    @Override
    public boolean affectsOPCosts() {
        return false;
    }

}