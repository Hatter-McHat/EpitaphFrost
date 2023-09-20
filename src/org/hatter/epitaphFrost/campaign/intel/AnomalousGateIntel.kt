package org.hatter.epitaphFrost.campaign.intel;

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.SectorEntityToken
import com.fs.starfarer.api.campaign.StarSystemAPI
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin
import com.fs.starfarer.api.impl.campaign.ids.Tags
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin
import com.fs.starfarer.api.loading.Description
import com.fs.starfarer.api.ui.SectorMapAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
//copied from RAT
class AnomalousGateIntel(var system: StarSystemAPI) : BaseIntelPlugin() {

        override fun createIntelInfo(info: TooltipMakerAPI, mode: IntelInfoPlugin.ListInfoMode?) {
        super.createIntelInfo(info, mode)

        val c = getTitleColor(mode)
        val tc = getBulletColorForMode(mode)
        info.addPara("Dangerous gate", c, 0f)
        }

        override fun createSmallDescription(info: TooltipMakerAPI?, width: Float, height: Float) {
        //cross-reference with description
        /*
        val desc = Global.getSettings().getDescription(EFEntities.OUTPOST_WARNING_BEACON, Description.Type.CUSTOM)
        info!!.addPara(desc.text1FirstPara, 0f)
         */
        val desc = ""
        info!!.addPara(desc, 0f)
        var label = info.addPara("You briefly saw a mention of a strange gate and dangerous ships in the ${system.name}. " +
        "you noted it down for later verification.", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "${system.name}")
        }

        override fun getIcon(): String {
        return Global.getSettings().getSpriteName("intel", "gate_inactive")
        }

        override fun getIntelTags(map: SectorMapAPI?): MutableSet<String> {
        val tags = super.getIntelTags(map)
        tags.add(Tags.INTEL_EXPLORATION)

        return tags
        }

        override fun getMapLocation(map: SectorMapAPI?): SectorEntityToken {
        return system.hyperspaceAnchor
        }

        override fun shouldRemoveIntel(): Boolean {
        return false
        }

        override fun getCommMessageSound(): String? {
        return "ui_discovered_entity"
        }
}
