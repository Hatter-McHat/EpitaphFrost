package org.hatter.epitaphFrost.shipsystems;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import data.scripts.util.MagicAnim;
import org.lwjgl.util.vector.Vector2f;

public class BlastDrive extends BaseShipSystemScript {
	public static float SPEED_BONUS = 300f;
	public static float TURN_BONUS = 20f;
	@Override
	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {

		if (state == ShipSystemStatsScript.State.OUT) {
			stats.getMaxSpeed().unmodify(id); // to slow down ship to its regular top speed while powering drive down
			stats.getMaxTurnRate().unmodify(id);
		} else {
			stats.getMaxSpeed().modifyFlat(id, SPEED_BONUS);
			stats.getAcceleration().modifyPercent(id, SPEED_BONUS * 3f * effectLevel);
			stats.getDeceleration().modifyPercent(id, SPEED_BONUS * 3f * effectLevel);
			stats.getTurnAcceleration().modifyPercent(id, TURN_BONUS * 5f * effectLevel);
			stats.getTurnAcceleration().modifyFlat(id, 120);
			stats.getMaxTurnRate().modifyFlat(id, 15f);
			stats.getMaxTurnRate().modifyPercent(id, 100f);
			stats.getAcceleration().modifyFlat(id, SPEED_BONUS);
		}

//        stats.getAcceleration().modifyMult(id, 0);
//        stats.getDeceleration().modifyMult(id, 0);
		//stats.getTurnAcceleration().modifyMult(id, 20f);
		//stats.getMaxTurnRate().modifyFlat(id, MagicAnim.smoothReturnNormalizeRange(effectLevel, 0, 1)*180f);
	}
	@Override
	public void unapply(MutableShipStatsAPI stats, String id) {
		stats.getMaxSpeed().unmodify(id);
		stats.getMaxTurnRate().unmodify(id);
		stats.getTurnAcceleration().unmodify(id);
		stats.getAcceleration().unmodify(id);
		stats.getDeceleration().unmodify(id);
		//stats.getMaxTurnRate().unmodify(id);
		//stats.getTurnAcceleration().unmodify(id);
//        stats.getAcceleration().unmodify(id);
//        stats.getDeceleration().unmodify(id);

		//clamp mobility
		float turning = stats.getMaxTurnRate().getBaseValue();
		stats.getEntity().setAngularVelocity(Math.min(turning, Math.max(-turning, stats.getEntity().getAngularVelocity())));
        Vector2f velocity = stats.getEntity().getVelocity();
        velocity.set(0,0);
	}

	private final String TRANSLATION = "translation";
	private final String REPULSION = "repulsion";

	@Override
	public StatusData getStatusData(int index, State state, float effectLevel) {
		if (index == 0) {
			return new StatusData(TRANSLATION, false);
		} else if (index == 1) {
			return new StatusData(REPULSION, false);
		}
		return null;
	}
}