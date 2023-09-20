package org.hatter.epitaphFrost.campaign.interactions

import org.hatter.epitaphFrost.campaign.plugins.entities.VestigeGate
import org.hatter.epitaphFrost.misc.EFInteractionPlugin
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.JumpPointAPI

//copied from RAT
class VestigeGateInteraction : EFInteractionPlugin() {
    override fun init() {

        var plugin = interactionTarget.customPlugin as VestigeGate
        /*
        //not messing around with activating it.
        if (!plugin.active)
        {
            var planet: SectorEntityToken? = null
            var stations = interactionTarget.starSystem.customEntities.filter { it.customEntitySpec.id == "rat_chiral_station1" }
            for (station in stations)
            {
                planet = Misc.findNearestPlanetTo(station, false, false)
            }

            textPanel.addPara("You close on to what appears to be a gate, but its specifications seem different to that of Domain Built ones. There is no sign of it being active.")

            if (planet != null)
            {
                textPanel.addPara("Despite that, there seem to be solar-powered antenna attempting to receive signals from somewhere close to the ${planet.name} planet.").apply {
                    setHighlight("${planet.name}")
                    setHighlightColor(Misc.getHighlightColor())
                }
            }
        }
        else
        {
         */
            textPanel.addPara("The gate stands silent.")

            createOption("Enter.") {
                var plugin = interactionTarget.customPlugin as VestigeGate
                plugin.showBeingUsed(15f)
                (plugin.teleportLocation!!.customPlugin as VestigeGate).showBeingUsed(15f)
                Global.getSector().doHyperspaceTransition(Global.getSector().playerFleet, interactionTarget, JumpPointAPI.JumpDestination(plugin.teleportLocation, ""), 2f)
                closeDialog()
           // }
        }


        addLeaveOption()
    }
}