package org.hatter.epitaphFrost.hullmods;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI;
import com.fs.starfarer.api.impl.campaign.skills.BaseSkillEffectDescription;

public class IntegratedOrdnanceGrid extends BaseHullMod {

    public static final float RANGE_BONUS  = 20f;
    public static final int COST_REDUCTION  = 4;
    public static final int DISSIPATION_PER_OP = 2;
    public static final int CAPACITY_PER_OP = 40;



    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getBallisticWeaponRangeBonus().modifyPercent(id, RANGE_BONUS);
        stats.getEnergyWeaponRangeBonus().modifyPercent(id, RANGE_BONUS);
        stats.getDynamic().getMod(Stats.LARGE_BALLISTIC_MOD).modifyFlat(id, -COST_REDUCTION);
        stats.getDynamic().getMod(Stats.MEDIUM_BALLISTIC_MOD).modifyFlat(id, -COST_REDUCTION);
        stats.getDynamic().getMod(Stats.SMALL_BALLISTIC_MOD).modifyFlat(id, -COST_REDUCTION);
        stats.getDynamic().getMod(Stats.LARGE_ENERGY_MOD).modifyFlat(id, -COST_REDUCTION);
        stats.getDynamic().getMod(Stats.MEDIUM_ENERGY_MOD).modifyFlat(id, -COST_REDUCTION);
        stats.getDynamic().getMod(Stats.SMALL_ENERGY_MOD).modifyFlat(id, -COST_REDUCTION);
        stats.getDynamic().getMod(Stats.LARGE_MISSILE_MOD).modifyFlat(id, -COST_REDUCTION);
        stats.getDynamic().getMod(Stats.MEDIUM_MISSILE_MOD).modifyFlat(id, -COST_REDUCTION);
        stats.getDynamic().getMod(Stats.SMALL_MISSILE_MOD).modifyFlat(id, -COST_REDUCTION);
        if (stats.getVariant() != null) {
            MutableCharacterStatsAPI cStats = BaseSkillEffectDescription.getCommanderStats(stats);
            float dissipation = DISSIPATION_PER_OP * stats.getVariant().computeWeaponOPCost(cStats);
            float capacity = CAPACITY_PER_OP * stats.getVariant().computeWeaponOPCost(cStats);
            stats.getFluxDissipation().modifyFlat(id, dissipation);
            stats.getFluxCapacity().modifyFlat(id, capacity);

        }
    }

    public String getDescriptionParam(int index, HullSize hullSize) {
        if (index == 0) return "" + (int) RANGE_BONUS + "%";
        if (index == 1) return "" + (int) COST_REDUCTION + "";
        if (index == 2) return "" + (int) DISSIPATION_PER_OP + "";
        if (index == 3) return "" + (int) CAPACITY_PER_OP + "";
        return null;
    }

    @Override
    public boolean affectsOPCosts() {
        return true;
    }

}