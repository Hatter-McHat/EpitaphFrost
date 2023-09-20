package org.hatter.epitaphFrost
/*
import ParallelConstruction
import lunalib.lunaDebug.LunaDebug
import lunalib.lunaExtensions.getSystemsWithTag
import lunalib.lunaSettings.LunaSettings
import assortment_of_things.campaign.procgen.LootModifier
import assortment_of_things.campaign.skills.util.SkillManager
import assortment_of_things.campaign.ui.MinimapUI
import assortment_of_things.misc.RATSettings
import assortment_of_things.modular_weapons.scripts.WeaponComponentsListener
import assortment_of_things.modular_weapons.util.ModularWeaponLoader
import assortment_of_things.snippets.ProcgenDebugSnippet
import assortment_of_things.snippets.ResetAllModularSnippet
import assortment_of_things.strings.RATTags
 */
import com.fs.starfarer.api.BaseModPlugin
import com.fs.starfarer.api.Global

import org.hatter.epitaphFrost.campaign.EFCampaignPlugin


class EFModPlugin : BaseModPlugin() {

    companion object {
        var added = false
    }
    override fun onGameLoad(newGame: Boolean) {
        Global.getSector().registerPlugin(EFCampaignPlugin())
    }
    override fun onNewGameAfterTimePass() {
        var vestige = Global.getSector().getFaction("ef_vestige")
        vestige?.adjustRelationship("player", -1f)
        var verglas = Global.getSector().getFaction("ef_verglas")
        verglas?.adjustRelationship("player", -1f)
    }
}