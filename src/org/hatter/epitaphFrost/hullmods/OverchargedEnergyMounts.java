package org.hatter.epitaphFrost.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.WeaponAPI;
//import com.fs.starfarer.api.combat.WeaponAPI.WeaponType;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI;
import java.util.HashMap;
import java.util.Map;

import com.fs.starfarer.api.loading.WeaponSpecAPI;
public class OverchargedEnergyMounts extends BaseHullMod {

    public static final int LARGE_PENALTY = 8;
    public static final int MEDIUM_PENALTY = 4;
    public static final int SMALL_PENALTY = 1;
    public static final float DAMAGE_BOOST = 1.5f;
    public static final float FLUX_COST = 1.65f;
    public static final float SMOD_FLUX_COST = 1.5f;
    public static final int SMOD_SMALL_PENALTY = -1;

    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getDynamic().getMod(Stats.LARGE_ENERGY_MOD).modifyFlat(id, LARGE_PENALTY);
        stats.getDynamic().getMod(Stats.MEDIUM_ENERGY_MOD).modifyFlat(id, MEDIUM_PENALTY);
        stats.getEnergyWeaponDamageMult().modifyMult(id, DAMAGE_BOOST);
        if (isSMod(stats)){
            stats.getEnergyWeaponFluxCostMod().modifyMult(id, SMOD_FLUX_COST);
            stats.getDynamic().getMod(Stats.SMALL_ENERGY_MOD).modifyFlat(id,  SMOD_SMALL_PENALTY);
        }
        else {
            stats.getEnergyWeaponFluxCostMod().modifyMult(id, FLUX_COST);
            stats.getDynamic().getMod(Stats.SMALL_ENERGY_MOD).modifyFlat(id, SMALL_PENALTY);
        }
    }
    /*
    //following code is largely from DarkRevenant's PDAC.
    private void enoughOPToInstall(ShipAPI ship) {
        if ((ship == null) || (ship.getVariant() == null)) {
            return true;
        }
        if (ship.getVariant().hasHullMod(spec.getId())) {
            return true;
        }
        FleetMemberAPI member = ship.getFleetMember();
        MutableCharacterStatsAPI stats = null;
        if ((member != null) && (member.getFleetCommanderForStats() != null)) {
            stats = member.getFleetCommanderForStats().getFleetCommanderStats();
        }
        int unusedOP = ship.getVariant().getUnusedOP(stats);
        int opRequiredToInstall = spec.getCostFor(ship.getHullSize());
        //for (WeaponAPI weaponSpec : ship.getAllWeapons()) {
        for (String slotId : ship.getVariant().getFittedWeaponSlots()) {
            WeaponSpecAPI weaponSpec = ship.getVariant().getWeaponSpec(slotId);
            if (weaponSpec.getType() == WeaponType.ENERGY) {
                switch (weaponSpec.getSize()) {
                    case SMALL:
                        opRequiredToInstall += SMALL_PENALTY;
                        break;
                    case MEDIUM:
                        opRequiredToInstall += MEDIUM_PENALTY;
                        break;
                    case LARGE:
                        opRequiredToInstall += LARGE_PENALTY;
                        break;
                    default:
                        break;
                }
            }
        }
         return unusedOP >= opRequiredToInstall;
    }

     */

    private static final Map<WeaponAPI.WeaponSize, Integer> PENALTIES = new HashMap<WeaponAPI.WeaponSize, Integer>();
    static {
        PENALTIES.put(WeaponAPI.WeaponSize.SMALL, SMALL_PENALTY);
        PENALTIES.put(WeaponAPI.WeaponSize.MEDIUM, MEDIUM_PENALTY);
        PENALTIES.put(WeaponAPI.WeaponSize.LARGE, LARGE_PENALTY);
    }

    private boolean enoughOPToInstall(ShipAPI ship) {
        int opRequiredToInstall = spec.getCostFor(ship.getHullSize());
        FleetMemberAPI member = ship.getFleetMember();
        MutableCharacterStatsAPI stats = null;
        if ((member != null) && (member.getFleetCommanderForStats() != null)) {
            stats = member.getFleetCommanderForStats().getFleetCommanderStats();
        }
        int unusedOP = ship.getVariant().getUnusedOP(stats);
        for (String slotId : ship.getVariant().getFittedWeaponSlots()) {
            WeaponSpecAPI weaponSpec = ship.getVariant().getWeaponSpec(slotId);
            if (weaponSpec.getType() == WeaponAPI.WeaponType.ENERGY) {
                opRequiredToInstall += PENALTIES.get(weaponSpec.getSize());

            }
        }
        return unusedOP >= opRequiredToInstall;
    }

    //private boolean enoughOPToInstall(ShipAPI ship) { return true; }
    @Override
    public String getUnapplicableReason(ShipAPI ship) {
        if (!enoughOPToInstall(ship) && !ship.getVariant().hasHullMod("ef_overcharged_energy_mounts")) {
            return "Insufficient ordnance points due to increased cost";
        }
        return super.getUnapplicableReason(ship);
        //return getUnapplicableReason(ship);
    }
    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        if (!enoughOPToInstall(ship) && !ship.getVariant().hasHullMod("ef_overcharged_energy_mounts")) {
            return false;
        }
        return super.isApplicableToShip(ship);
        //return isApplicableToShip(ship)
    }

    public String getDescriptionParam(int index, HullSize hullSize) {
        if (index == 0) return "" + (int) (DAMAGE_BOOST * 100 - 100) + "%";
        if (index == 1) return "" + SMALL_PENALTY + "/" + MEDIUM_PENALTY + "/" + LARGE_PENALTY + "";
        if (index == 2) return "" + (int) (FLUX_COST * 100 - 100) + "%";
        if (index == 3) return "" + (int) (SMOD_FLUX_COST * 100 - 100) + "%";
        return null;
    }

    @Override
    public boolean affectsOPCosts() {
        return true;
    }
}