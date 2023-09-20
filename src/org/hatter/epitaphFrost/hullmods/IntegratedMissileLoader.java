package org.hatter.epitaphFrost.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.loading.WeaponSpecAPI;

import java.awt.*;
import java.util.EnumSet;


public class IntegratedMissileLoader extends BaseHullMod {
    //ef_integrated_missile_loader
    public static final int ROF_BOOST  = 2;
    public static final float HP_MALUS =0.6f;
    public static final int FLUX_PENALTY  = 2;

    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getMissileRoFMult().modifyMult(id, ROF_BOOST);
        stats.getMissileHealthBonus().modifyMult(id, ROF_BOOST);
        if(!isSMod(stats)) {
            stats.getFluxDissipation().modifyFlat(id, (-1) * (FLUX_PENALTY) * (MissileOP(stats)));
        }
    }
    public void advanceInCombat(ShipAPI ship, float amount) {
        ship.setWeaponGlow(0.5f, Color.GREEN, EnumSet.of(WeaponAPI.WeaponType.MISSILE));
    }

    private int MissileOP(MutableShipStatsAPI stats) {
        int calc_Missile_OP=0;
        if(!isSMod(stats) && stats != null) {
            FleetMemberAPI member = stats.getFleetMember();
            MutableCharacterStatsAPI charStats = Global.getSector().getPlayerStats();;
            for (String slotId : stats.getVariant().getFittedWeaponSlots()) {
                WeaponSpecAPI weaponSpec = stats.getVariant().getWeaponSpec(slotId);
                if (weaponSpec.getType() == WeaponAPI.WeaponType.MISSILE && member != null) {
                    calc_Missile_OP += weaponSpec.getOrdnancePointCost(charStats, member.getStats());
                }

            }
        }
        return calc_Missile_OP;
    }
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize, ShipAPI ship) {
        int calc_Missile_op = 0;
        if( ship != null){
            calc_Missile_op = MissileOP(ship.getMutableStats());
        }
        if (index == 0) return "" + (int) ((ROF_BOOST-1)*100)  + "%";
        if (index == 1) return "" +  Math.round((1-HP_MALUS)*100)  + "%";
        if (index == 2) return "" +  (int) (FLUX_PENALTY)  + "";
        if (index == 3) return "" + calc_Missile_op * FLUX_PENALTY + "";
        return null;
    }


}