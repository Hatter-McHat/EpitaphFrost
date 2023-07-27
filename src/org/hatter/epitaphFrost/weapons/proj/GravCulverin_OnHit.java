//Onhit copied from BadDragn
package org.hatter.epitaphFrost.weapons.proj;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.util.Misc;
import org.lwjgl.util.vector.Vector2f;
import com.fs.starfarer.api.combat.ShipAPI;

public class GravCulverin_OnHit implements OnHitEffectPlugin {

    public static final float SOFT_FLUX_MULT = 2f;

    @Override
    public void onHit(DamagingProjectileAPI proj, CombatEntityAPI target, Vector2f point, boolean shieldhit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
        if (shieldhit && target instanceof ShipAPI) {
            float damage;
            damage = SOFT_FLUX_MULT * (proj.getDamageAmount()) * (((ShipAPI) target).getMutableStats().getShieldDamageTakenMult().getModifiedValue()) * (((ShipAPI) target).getMutableStats().getKineticShieldDamageTakenMult().getModifiedValue()) * getDamageTypeMult(proj.getSource(), (ShipAPI) target);
            ((ShipAPI) target).getFluxTracker().increaseFlux(damage, false);
            if (Misc.shouldShowDamageFloaty(proj.getSource(), (ShipAPI) target)) {
                engine.addFloatingDamageText(point, damage, Misc.FLOATY_SHIELD_DAMAGE_COLOR, target, proj.getSource());
            }
        }
    }

    public static float getDamageTypeMult(ShipAPI source, ShipAPI target) {
        if (source == null || target == null) return 1f;

        float damageTypeMult = 1f;
        switch (target.getHullSize()) {
            case CAPITAL_SHIP:
                damageTypeMult *= source.getMutableStats().getDamageToCapital().getModifiedValue();
                break;
            case CRUISER:
                damageTypeMult *= source.getMutableStats().getDamageToCruisers().getModifiedValue();
                break;
            case DESTROYER:
                damageTypeMult *= source.getMutableStats().getDamageToDestroyers().getModifiedValue();
                break;
            case FRIGATE:
                damageTypeMult *= source.getMutableStats().getDamageToFrigates().getModifiedValue();
                break;
            case FIGHTER:
                damageTypeMult *= source.getMutableStats().getDamageToFighters().getModifiedValue();
                break;
        }
        return damageTypeMult;
    }
}