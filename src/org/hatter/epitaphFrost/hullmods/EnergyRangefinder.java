package org.hatter.epitaphFrost.hullmods;

import java.awt.Color;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.WeaponAPI.AIHints;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponSize;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponType;
import com.fs.starfarer.api.combat.listeners.WeaponBaseRangeModifier;
import com.fs.starfarer.api.loading.WeaponSlotAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class EnergyRangefinder extends BaseHullMod {

	public static float BONUS_MAX_1 = 700;
	public static float BONUS_MAX_2 = 600;
	public static float BONUS_SMALL_1 =  200;
	public static float BONUS_SMALL_2 = 100;
	public static float BONUS_MEDIUM_1 = 100;


//This is essentially ballistic rangefinder, code is largely copied from it. What did you expect?
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
	}

	@Override
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		WeaponSize largest = null;
		for (WeaponSlotAPI slot : ship.getHullSpec().getAllWeaponSlotsCopy()) {
			if (slot.isDecorative() ) continue;
			// probably a shorter way to do this
			if (slot.getWeaponType() == WeaponType.ENERGY || slot.getWeaponType() == WeaponType.SYNERGY || slot.getWeaponType() == WeaponType.HYBRID || slot.getWeaponType() == WeaponType.UNIVERSAL) {
				if (largest == null || largest.ordinal() < slot.getSlotSize().ordinal()) {
					largest = slot.getSlotSize();
				}
			}
		}
		if (largest == null) return;
		float small = 0f;
		float medium = 0f;
		float max = 0f;
		switch (largest) {
		case LARGE:
			small = BONUS_SMALL_1;
			medium = BONUS_MEDIUM_1;
			max = BONUS_MAX_1;
			break;
		case MEDIUM:
		case SMALL:
			small = BONUS_SMALL_2;
			max = BONUS_MAX_2;
			break;
		}

		ship.addListener(new RangefinderRangeModifier(small, medium, max));
	}

	public static class RangefinderRangeModifier implements WeaponBaseRangeModifier {
		public float small, medium, max;
		public RangefinderRangeModifier(float small, float medium, float max) {
			this.small = small;
			this.medium = medium;
			this.max = max;
		}

		public float getWeaponBaseRangePercentMod(ShipAPI ship, WeaponAPI weapon) {
			return 0;
		}
		public float getWeaponBaseRangeMultMod(ShipAPI ship, WeaponAPI weapon) {
			return 1f;
		}
		public float getWeaponBaseRangeFlatMod(ShipAPI ship, WeaponAPI weapon) {
			if (weapon.getSlot() == null || weapon.getType() != WeaponType.ENERGY) {
				return 0f;
			}
			if (weapon.hasAIHint(AIHints.PD)) {
				return 0f;
			}
			float bonus = 0;
			if (weapon.getSize() == WeaponSize.SMALL) {
				bonus = small;
			} else if (weapon.getSize() == WeaponSize.MEDIUM) {
				bonus = medium;
			}

			if (bonus == 0f) return 0f;

			float base = weapon.getSpec().getMaxRange();
			if (base + bonus > max) {
				bonus = max - base;
			}
			if (bonus < 0) bonus = 0;
			return bonus;
		}
	}

	public String getDescriptionParam(int index, HullSize hullSize) {
		//if (index == 0) return "" + (int)RANGE_PENALTY_PERCENT + "%";
		return null;
	}

	@Override
	public boolean shouldAddDescriptionToTooltip(HullSize hullSize, ShipAPI ship, boolean isForModSpec) {
		return false;
	}

	@Override
	public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
		float pad = 3f;
		float opad = 10f;
		Color h = Misc.getHighlightColor();
		Color bad = Misc.getNegativeHighlightColor();

		LabelAPI label = tooltip.addPara("If the ship has a large slot capable of mounting an energy weapon:"
				+ " increases the base range of small energy weapons by %s,"
				+ " and of medium weapons by %s, up to %s maximum.", opad, h,
				"" + (int)BONUS_SMALL_1, "" + (int)BONUS_MEDIUM_1, "" + (int)BONUS_MAX_1);
//		label.setHighlight("Ballistic", "base", "Ballistic", "" + (int)BONUS_SMALL_3, "" + (int)BONUS_MEDIUM_3, "" + (int)BONUS_MAX_3);
//		label.setHighlightColors(Misc.MOUNT_BALLISTIC, h, Misc.MOUNT_BALLISTIC, h, h, h);
		label.setHighlight("" + (int)BONUS_SMALL_1, "" + (int)BONUS_MEDIUM_1, "" + (int)BONUS_MAX_1);
		label.setHighlightColors(h, h, h);

		label = tooltip.addPara("Otherwise:"
				+ " increases the base range of small energy weapons slots by %s,"
				+ " up to %s maximum.", opad, h,
				"" + (int)BONUS_SMALL_2, "" + (int)BONUS_MAX_2);
//		label.setHighlight("base", "Ballistic", "" + (int)BONUS_SMALL_1, "" + (int)BONUS_MAX_1);
//		label.setHighlightColors(h, Misc.MOUNT_BALLISTIC, h, h);
		label.setHighlight("" + (int)BONUS_SMALL_2, "" + (int)BONUS_MAX_2);
		label.setHighlightColors(h, h);

/*
		tooltip.addSectionHeading("Exceptions", Alignment.MID, opad);
		label = tooltip.addPara("Does not affect point-defense weapons, "
						+ "or Ballistic weapons in Composite, Hybrid, and Universal slots.", opad);
//		label.setHighlight("Ballistic", "Composite", "Hybrid", "Universal");
//		label.setHighlightColors(Misc.MOUNT_BALLISTIC, Misc.MOUNT_COMPOSITE, Misc.MOUNT_HYBRID, Misc.MOUNT_UNIVERSAL);
		label.setHighlight("Composite", "Hybrid", "Universal");
		label.setHighlightColors(Misc.MOUNT_COMPOSITE, Misc.MOUNT_HYBRID, Misc.MOUNT_UNIVERSAL);
*/
		tooltip.addSectionHeading("Interactions with other modifiers", Alignment.MID, opad);
		tooltip.addPara("Since the base range is increased, this modifier"
				+ " - unlike most other flat modifiers in the game - "
				+ "is affected by percentage modifiers from other hullmods and skills.", opad);
	}

	@Override
	public boolean isApplicableToShip(ShipAPI ship) {
		return getUnapplicableReason(ship) == null;
	}

	public String getUnapplicableReason(ShipAPI ship) {
		if (ship != null &&
				ship.getHullSize() != HullSize.CAPITAL_SHIP &&
				ship.getHullSize() != HullSize.DESTROYER &&
				ship.getHullSize() != HullSize.CRUISER) {
			return "Can only be installed on destroyer-class hulls and larger";
		}
		return null;
	}

}
