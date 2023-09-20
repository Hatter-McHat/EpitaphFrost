package org.hatter.epitaphFrost.campaign.bar

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.InteractionDialogAPI
import com.fs.starfarer.api.campaign.StarSystemAPI
import com.fs.starfarer.api.campaign.econ.MarketAPI
import com.fs.starfarer.api.campaign.rules.MemoryAPI
import com.fs.starfarer.api.impl.campaign.intel.bar.PortsideBarData
import com.fs.starfarer.api.impl.campaign.intel.bar.events.BaseBarEvent
import com.fs.starfarer.api.util.Misc
import lunalib.lunaDelegates.LunaMemory
import lunalib.lunaExtensions.getSystemsWithTag
import org.hatter.epitaphFrost.campaign.intel.AnomalousGateIntel
import org.hatter.epitaphFrost.strings.EFTags

class VestigeGateBarEvent : BaseBarEvent() {


    var gateSystem: StarSystemAPI? = null
    var finished = false

    override fun shouldShowAtMarket(market: MarketAPI?): Boolean {

        //check if main needed

        var systems = Global.getSector().getSystemsWithTag(EFTags.VESTIGE_GATE)

        var givenIntel: Boolean? by LunaMemory("ef_gave_vestige_intel", false)

        if (systems.isNullOrEmpty() || givenIntel!!) return false
        gateSystem = systems.first()
        return true
    }

    override fun isDialogFinished(): Boolean {
        return finished
    }

    override fun addPromptAndOption(dialog: InteractionDialogAPI?, memoryMap: MutableMap<String, MemoryAPI>?) {
        super.addPromptAndOption(dialog, memoryMap)
        /*
        dialog!!.textPanel.addPara("A table with men spreading rumors about some strange gate.")
        dialog!!.optionPanel.addOption("Rumors about a strange gate", this)
         */
        dialog!!.textPanel.addPara("A flickering holo-sign on the wall")
        dialog!!.optionPanel.addOption("Look closer at the holo-sign", this)
    }

    override fun init(dialog: InteractionDialogAPI?, memoryMap: MutableMap<String, MemoryAPI>?) {
        super.init(dialog, memoryMap)
        /*
        dialog!!.optionPanel.clearOptions()
        dialog!!.textPanel.addPara("As you stroll around the bar, you encounter a group of drunks telling others about some kind of gate they discovered during their salvage expeditions in the fringe-worlds." +
                "\n\nThey blabber around about how they noted some tri-tach fleets moving around there. According to them this happened in the ${gateSystem!!.name}. Despite all their talk, they cant provide any information on whats " +
                "so strange about it to them, all just their intution, according to them.",
                Misc.getTextColor(), Misc.getHighlightColor(), "${gateSystem!!.name}")
         */

        dialog!!.optionPanel.clearOptions()
        dialog!!.textPanel.addPara("" +
                "On the bar wall is a holo-sign. It's seen better days, local advertisements nearly choking out whatever was on the screen." +
                "\n\n Past that the announcement of a spacer who claims to have found a working gate in ${gateSystem!!.name} overflowing with secret Tri-Tach drone-ships waiting start the Third War is barely discernible." +
                "The announcement vanishes before much else can be made out.",
                Misc.getTextColor(), Misc.getHighlightColor(), "${gateSystem!!.name}")


        var intel = AnomalousGateIntel(gateSystem!!)
        Global.getSector().intelManager.addIntel(intel)

        intel.sendUpdate(null, dialog.textPanel)

        var givenIntel: Boolean? by LunaMemory("ef_gave_vestige_intel", false)
        givenIntel = true

        PortsideBarData.getInstance().removeEvent(this)
        finished = true




        // dialog.optionPanel.addOption("Leave", "LEAVE")

    }


    override fun optionSelected(optionText: String?, optionData: Any?) {
        super.optionSelected(optionText, optionData)

        if (optionData is String)
        {
            if (optionData == "LEAVE")
            {
                finished = true
            }
        }
    }

}