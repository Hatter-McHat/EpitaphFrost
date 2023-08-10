package data.missions.ef_demo;

import com.fs.starfarer.api.fleet.FleetGoal;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.mission.MissionDefinitionAPI;
import com.fs.starfarer.api.mission.MissionDefinitionPlugin;

public class MissionDefinition implements MissionDefinitionPlugin {

  @Override
	public void defineMission(MissionDefinitionAPI api) {

		// Set up the fleets
		api.initFleet(FleetSide.PLAYER, "ISS", FleetGoal.ATTACK, false);
		api.initFleet(FleetSide.ENEMY, "ISS", FleetGoal.ATTACK, true);

		// Set a blurb for each fleet
		api.setFleetTagline(FleetSide.PLAYER, "Demo Ships.");
		api.setFleetTagline(FleetSide.ENEMY, "Test Fleet.");
		
		// These show up as items in the bulleted list under 
		// "Tactical Objectives" on the mission detail screen
		api.addBriefingItem("Demo");
		
		// Set up the player's fleet.
	    api.addToFleet(FleetSide.PLAYER, "ef_quest_Standard", FleetMemberType.SHIP, "Demo", true);
	    api.addToFleet(FleetSide.PLAYER, "ef_reinforcer_Standard", FleetMemberType.SHIP, "Demo", false);
	    api.addToFleet(FleetSide.PLAYER, "ef_fluorescence_Attack", FleetMemberType.SHIP, "Demo", false);
		api.addToFleet(FleetSide.PLAYER, "ef_necropolis_Standard", FleetMemberType.SHIP, "Demo", false);
		api.addToFleet(FleetSide.PLAYER, "ef_graveyard_Standard", FleetMemberType.SHIP, "Demo", false);
		api.addToFleet(FleetSide.PLAYER, "ef_crypt_Standard", FleetMemberType.SHIP, "Demo", false);
	    api.addToFleet(FleetSide.PLAYER, "ef_sepulchre_Defence", FleetMemberType.SHIP, "Demo", false);
	    api.addToFleet(FleetSide.PLAYER, "ef_tombstone_Standard", FleetMemberType.SHIP, "Demo", false);
	    api.addToFleet(FleetSide.PLAYER, "ef_lamina_Attack", FleetMemberType.SHIP, "Demo", false);
		// Set up the enemy fleet.
		api.addToFleet(FleetSide.ENEMY, "kite_Standard", FleetMemberType.SHIP, "Hapless Bystander", false);
		
		
		
		// api.defeatOnShipLoss("When Not Provided");
		
		// Set up the map.
		float width = 12000f;
		float height = 12000f;
		api.initMap((float)-width/2f, (float)width/2f, (float)-height/2f, (float)height/2f);
		
		float minX = -width/2;
		float minY = -height/2;
		
		// Add an asteroid field
		api.addAsteroidField(minX, minY + height / 2, 0, 8000f,
							 20f, 70f, 100);
		
	}

}
