package xyz.aerii.athen.modules.impl.slayer

import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.phys.Vec3
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.utils.extentions.getTexture
import xyz.aerii.athen.annotations.Load
import xyz.aerii.athen.annotations.OnlyIn
import xyz.aerii.athen.config.Category
import xyz.aerii.athen.events.LocationEvent
import xyz.aerii.athen.events.SlayerEvent
import xyz.aerii.athen.handlers.Chronos
import xyz.aerii.athen.modules.Module
import kotlin.math.abs
import kotlin.time.Duration.Companion.seconds

@Load
@OnlyIn(skyblock = true)
object BigSlayerDrops : Module(
    "Big slayer drops",
    "Renders the items dropped by a slayer boss to be bigger!",
    Category.SLAYER
) {
    private val set0 = setOf("ewogICJ0aW1lc3RhbXAiIDogMTcxOTUwNDEyOTIyMiwKICAicHJvZmlsZUlkIiA6ICIxNzM1MGE5OWQ3MzQ0NDBjYTY0YzJjMDU3YTNjMWM4ZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJHaWxkZWRoZXJvNTY5MSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9hOGM0ODExMzk1ZmJmN2Y2MjBmMDVjYzMxNzVjZWYxNTE1YWFmNzc1YmEwNGEwMTA0NTAyN2YwNjkzYTkwMTQ3IiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=", "ewogICJ0aW1lc3RhbXAiIDogMTcwOTMwNjAwOTQ3NywKICAicHJvZmlsZUlkIiA6ICI1ZjQ5N2JmZDQwODU0NjRhOTNiMTRjN2Y3OTc5ZGYyNCIsCiAgInByb2ZpbGVOYW1lIiA6ICJUVEs5ODciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmM0YTY1YzY4OWIyZDM2NDA5MTAwYTYwYzJhYjhkM2QwYTY3Y2U5NGVlYTNjMWY3YWM5NzRmZDg5MzU2OGI1ZCIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9")
    private val set1 = setOf("ewogICJ0aW1lc3RhbXAiIDogMTcxOTUwNDQ3MDQ3NywKICAicHJvZmlsZUlkIiA6ICIzOTVkZTJlYjVjNjU0ZmRkOWQ2NDAwY2JhNmNmNjFhNyIsCiAgInByb2ZpbGVOYW1lIiA6ICJzcGFyZXN0ZXZlIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzQzYTFhZDRmY2M0MmZiNjNjNjgxMzI4ZTQyZDYzYzgzY2ExOTNiMzMzYWYyYTQyNjcyOGEyNWE4Y2M2MDA2OTIiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==", "ewogICJ0aW1lc3RhbXAiIDogMTcxOTUwMzQyNTYyNywKICAicHJvZmlsZUlkIiA6ICI2ZjhlYWI1MTVmNTc0MmRhOWYxZDYzMzY1ODAxMDU4YyIsCiAgInByb2ZpbGVOYW1lIiA6ICJDaW5kZXJGb3hfMjAwNiIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9iOGQwNWZkNGZjNmZkMWNiMzJjY2JhYmU4NzA0MGEyOTZiNTE0MjdiNGZhNWNlNTdiZTViNDExZDg2ZTIzNGM4IiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=")
    private val set2 = setOf("ewogICJ0aW1lc3RhbXAiIDogMTcxOTUwNDE1NTc5MSwKICAicHJvZmlsZUlkIiA6ICI4YjA2ZmU5ZGNjNjg0NDNmYWNmM2QzODA0NWNkNTMyNiIsCiAgInByb2ZpbGVOYW1lIiA6ICJDYXN0aWVsY3ZyIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2M3MzhiOGFmOGQ3Y2UxYTI2ZGM2ZDQwMTgwYjM1ODk0MDNlMTFlZjM2YTY2ZDdjNDU5MDAzNzczMjgyOTU0MmUiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==", "ewogICJ0aW1lc3RhbXAiIDogMTcxOTUwMzU0NzI1NywKICAicHJvZmlsZUlkIiA6ICJhNTdmZDE5MGZmM2U0YjBkYTEzMmY2OGUzOTU3ZjViMSIsCiAgInByb2ZpbGVOYW1lIiA6ICJ4SGFubmFoNyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS83MzRmYjMyMDMyMzNlZmJhZTgyNjI4YmQ0ZmNhNzM0OGNkMDcxZTViN2I1MjQwN2YxZDFkMjc5NGUzMTc5OWZmIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=")
    private val set3 = setOf("ewogICJ0aW1lc3RhbXAiIDogMTcxOTUwMzYzMjkyMiwKICAicHJvZmlsZUlkIiA6ICI1ZjU5NmViY2JlOTQ0NmQxYmI0M2JlNGYzZjRiOGJlNSIsCiAgInByb2ZpbGVOYW1lIiA6ICJUZWlsMHNzIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2MzYTlhY2JiN2QzZDQ5YjFkNTRkMjYxMTExMDRkMGRhNTdkOGI0YWIzNzg4NWI0YmJkMjQwYWM3MTA3NGNhZDIiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==", "ewogICJ0aW1lc3RhbXAiIDogMTYyODYzNTU3NDI0OSwKICAicHJvZmlsZUlkIiA6ICI0ZTMwZjUwZTdiYWU0M2YzYWZkMmE3NDUyY2ViZTI5YyIsCiAgInByb2ZpbGVOYW1lIiA6ICJfdG9tYXRvel8iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2IxMWZiOTBkYjdmNTdiZWI0MzU5NTQwMTNiMWM3ZWY3NzZjNmJkOTZjYmYzMzA4YWE4ZWJhYzI5NTkxZWJiZCIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9", "eyJ0aW1lc3RhbXAiOjE1ODY5MTE5MTcyODAsInByb2ZpbGVJZCI6IjNmYzdmZGY5Mzk2MzRjNDE5MTE5OWJhM2Y3Y2MzZmVkIiwicHJvZmlsZU5hbWUiOiJZZWxlaGEiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzhmZmY0MWUxYWZjNTk3YjE0Zjc3YjhlNDRlMmExMzRkYWJlMTYxYTE1MjZhZGU4MGU2MjkwZjJkZjMzMWRjMTEifX19")
    private val set4 = setOf("ewogICJ0aW1lc3RhbXAiIDogMTcxOTUwNDcyMTAxMiwKICAicHJvZmlsZUlkIiA6ICJmNmYxY2IxMmYzNDU0MDRlYjZlNjU2NGE2ZDlmMjU2NyIsCiAgInByb2ZpbGVOYW1lIiA6ICJBdXJlbGl1c0dlbWluaSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS84YzhjY2Q1Zjg2M2Q4MmJiMDk3YjkyNmJjNWY0Y2NhOTdiMTlmNDZlMTFiM2EzYTU5ZDAwMWFkYjg5ODg2NzczIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=", "ewogICJ0aW1lc3RhbXAiIDogMTY1Nzk5NDkxODg4NiwKICAicHJvZmlsZUlkIiA6ICJhNzdkNmQ2YmFjOWE0NzY3YTFhNzU1NjYxOTllYmY5MiIsCiAgInByb2ZpbGVOYW1lIiA6ICIwOEJFRDUiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGQ2MjBlNGUzZDNhYmZlZDZhZDgxYTU4YTU2YmNkMDg1ZDllOWVmYzgwM2NhYmIyMWZhNmM5ZTM5NjllMmQyZSIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9")
    private val set5 = setOf("ewogICJ0aW1lc3RhbXAiIDogMTY4MTUxOTM1Mjc5NCwKICAicHJvZmlsZUlkIiA6ICI4N2YzOGM1MWE4Yzc0MmNmYTY2YTgxNWExZTI2NzMzYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJCZWR3YXJzQ3V0aWUiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjVmZmRmYmQ0OTBmYzczMTBkNjFhMWM0YzM1YTRlMGNkMmY5ZmNjYzEyMzljNmE0YmNkN2RlYzA1ZTI1ZWE2NyIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9")

    private val set = mutableSetOf<Vec3>()

    val scale by config.slider("Scale", 3f, 1f, 10f)
    private val range by config.slider("Range multiplier", 1.0, 0.5, 5.0, "blocks", true)
    private val unscale by config.slider("Unscale after", 15, 5, 60, "seconds")
    private val selected by config.multiCheckbox("Enable for", listOf("Revenant", "Tarantula", "Sven", "Voidgloom", "Riftstalker", "Blaze"), listOf(0, 1, 2, 3, 4, 5))

    private val _filter by config.expandable("Scale filter")
    private val runes by config.switch("Runes").childOf { _filter }
    private val rev by config.multiCheckbox("Revenant", Rev.entries.map { it.str }, Rev.entries.map { it.ordinal }).childOf { _filter }
    private val tara by config.multiCheckbox("Tarantula", Tara.entries.map { it.str }, Tara.entries.map { it.ordinal }).childOf { _filter }
    private val sven by config.multiCheckbox("Sven", Sven.entries.map { it.str }, Sven.entries.map { it.ordinal }).childOf { _filter }
    private val void by config.multiCheckbox("Voidgloom", Void.entries.map { it.str }, Void.entries.map { it.ordinal }).childOf { _filter }
    private val blaze by config.multiCheckbox("Blaze", Blaze.entries.map { it.str }, Blaze.entries.map { it.ordinal }).childOf { _filter }
    private val vamp by config.multiCheckbox("Vampire", Vamp.entries.map { it.str }, Vamp.entries.map { it.ordinal }).childOf { _filter }

    init {
        on<SlayerEvent.Boss.Death> {
            val p = entity.position()
            set.add(p)

            Chronos.schedule(unscale.seconds) {
                set.remove(p)
            }
        }

        on<LocationEvent.Server.Connect> {
            set.clear()
        }
    }

    @JvmStatic
    fun ItemEntity.fn(): Boolean {
        val a = position().x()
        val b = position().y()
        val c = position().z()

        val d = item
        val e = d.getData(DataTypes.ID) ?: return false
        val f = d.item
        val g = d.getTexture()

        val h = fn0(e, f, g) || fn1(e, f, g) || fn2(e, f, g) || fn3(e, f, g) || fn4(e, f, g) || fn5(e, f, g)
        if (!h) return false

        val i = range
        for (s in set) {
            if (abs(s.x - a) > 5 * i) continue
            if (abs(s.y - b) > 3 * i) continue
            if (abs(s.z - c) > 5 * i) continue

            return true
        }

        return false
    }

    private fun fn0(id: String, item: Item, tx: String?): Boolean {
        if (0 !in selected) return false
        if (item == Items.PLAYER_HEAD && tx in set0) return runes

        return Rev.find(id, item)?.ordinal in rev
    }

    private fun fn1(id: String, item: Item, tx: String?): Boolean {
        if (1 !in selected) return false
        if (item == Items.PLAYER_HEAD && tx in set1) return runes

        return Tara.find(id, item)?.ordinal in tara
    }

    private fun fn2(id: String, item: Item, tx: String?): Boolean {
        if (2 !in selected) return false
        if (item == Items.PLAYER_HEAD && tx in set2) return runes

        return Sven.find(id, item)?.ordinal in sven
    }

    private fun fn3(id: String, item: Item, tx: String?): Boolean {
        if (3 !in selected) return false
        if (item == Items.PLAYER_HEAD && tx in set3) return runes

        return Void.find(id, item)?.ordinal in void
    }

    private fun fn4(id: String, item: Item, tx: String?): Boolean {
        if (4 !in selected) return false
        if (item == Items.PLAYER_HEAD && tx in set4) return runes

        return Blaze.find(id, item)?.ordinal in blaze
    }

    private fun fn5(id: String, item: Item, tx: String?): Boolean {
        if (5 !in selected) return false
        if (item == Items.PLAYER_HEAD && tx in set5) return runes

        return Vamp.find(id, item)?.ordinal in vamp
    }

    private enum class Rev(val item: Item, val str: String) {
        REVENANT_FLESH(Items.ROTTEN_FLESH, "Revenant flesh"),
        FOUL_FLESH(Items.CHARCOAL, "Foul flesh"),
        UNDEAD_CATALYST(Items.PLAYER_HEAD, "Undead catalyst"),
        SEVERED_HAND(Items.PLAYER_HEAD, "Severed hand"),
        BEHEADED_HORROR(Items.PLAYER_HEAD, "Beheaded horror"),
        REVENANT_CATALYST(Items.PLAYER_HEAD, "Revenant catalyst"),
        SCYTHE_BLADE(Items.DIAMOND, "Scythe blade"),
        REVENANT_VISCERA(Items.COOKED_PORKCHOP, "Revenant viscera"),
        SHARD_OF_THE_SHREDDED(Items.PLAYER_HEAD, "Shard of the shredded"),
        WARDEN_HEART(Items.PLAYER_HEAD, "Warden heart"),
        DYE_MATCHA(Items.PLAYER_HEAD, "Matcha dye");

        companion object {
            fun find(id: String, item: Item): Rev? {
                return entries.find { it.item == item && it.name == id }
            }
        }
    }

    private enum class Tara(val item: Item, val str: String) {
        TARANTULA_WEB(Items.STRING, "Tarantula web"),
        TOXIC_ARROW_POISON(Items.LIME_DYE, "Toxic arrow poison"),
        TARANTULA_SILK(Items.COBWEB, "Tarantula silk"),
        FLY_SWATTER(Items.GOLDEN_SHOVEL, "Fly swatter"),
        DIGESTED_MOSQUITO(Items.ROTTEN_FLESH, "Digested mosquito"),
        SPIDER_CATALYST(Items.PLAYER_HEAD, "Spider catalyst"),
        TARANTULA_CATALYST(Items.PLAYER_HEAD, "Tarantula catalyst"),
        VIAL_OF_VENOM(Items.PLAYER_HEAD, "Vial of venom"),
        TARANTULA_TALISMAN(Items.PLAYER_HEAD, "Tarantula talisman"),
        SHRIVELED_WASP(Items.PLAYER_HEAD, "Shriveled wasp"),
        ENSNARED_SNAIL(Items.PLAYER_HEAD, "Ensnared snail"),
        PRIMORDIAL_EYE(Items.PLAYER_HEAD, "Primordial eye"),
        DYE_BRICK_RED(Items.PLAYER_HEAD, "Brick red dye");

        companion object {
            fun find(id: String, item: Item): Tara? {
                return entries.find { it.item == item && it.name == id }
            }
        }
    }

    private enum class Sven(val item: Item, val str: String) {
        WOLF_TOOTH(Items.GHAST_TEAR, "Wolf tooth"),
        HAMSTER_WHEEL(Items.OAK_TRAPDOOR, "Hamster wheel"),
        RED_CLAW_EGG(Items.MOOSHROOM_SPAWN_EGG, "Red claw egg"),
        GRIZZLY_BAIT(Items.SALMON, "Grizzly bait"),
        FURBALL(Items.PLAYER_HEAD, "Furball"),
        OVERFLUX_CAPACITOR(Items.QUARTZ, "Overflux capacitor"),
        DYE_CELESTE(Items.PLAYER_HEAD, "Celeste dye");

        companion object {
            fun find(id: String, item: Item): Sven? {
                return entries.find { it.item == item && it.name == id }
            }
        }
    }

    private enum class Void(val item: Item, val str: String) {
        NULL_SPHERE(Items.FIREWORK_STAR, "Null sphere"),
        TWILIGHT_ARROW_POISON(Items.PURPLE_DYE, "Twilight arrow poison"),
        NULL_ATOM(Items.OAK_BUTTON, "Null atom"),
        TRANSMISSION_TUNER(Items.PLAYER_HEAD, "Transmission tuner"),
        HAZMAT_ENDERMAN(Items.PLAYER_HEAD, "Hazmat enderman"),
        POCKET_ESPRESSO_MACHINE(Items.PLAYER_HEAD, "Pocket espresso machine"),
        HANDY_BLOOD_CHALICE(Items.PLAYER_HEAD, "Handy blood chalice"),
        SINFUL_DICE(Items.PLAYER_HEAD, "Sinful dice"),
        SUMMONING_EYE(Items.PLAYER_HEAD, "Summoning eye"),
        EXCEEDINGLY_RARE_ENDER_ARTIFACT_UPGRADE(Items.PLAYER_HEAD, "Ender artifact upgrade"),
        ETHERWARP_MERGER(Items.PLAYER_HEAD, "Etherwarp merger"),
        JUDGEMENT_CORE(Items.PLAYER_HEAD, "Judgement core"),
        ENDSTONE_IDOL(Items.PLAYER_HEAD, "Endstone idol"),
        DYE_BYZANTIUM(Items.PLAYER_HEAD, "Byzantium dye");

        companion object {
            fun find(id: String, item: Item): Void? {
                return entries.find { it.item == item && it.name == id }
            }
        }
    }

    private enum class Blaze(val item: Item, val str: String) {
        DERELICT_ASHE(Items.GUNPOWDER, "Derelict ashe"),
        ENCHANTED_BLAZE_POWDER(Items.BLAZE_POWDER, "Enchanted blaze powder"),
        ARROW_BUNDLE_MAGMA(Items.PLAYER_HEAD, "Bundle of magma arrows"),
        MANA_DISINTEGRATOR(Items.PLAYER_HEAD, "Mana disintegrator"),
        SCORCHED_BOOKS(Items.PLAYER_HEAD, "Scorched books"),
        KELVIN_INVERTER(Items.PLAYER_HEAD, "Kelvin inverter"),
        GLOWSTONE_DUST_DISTILLATE(Items.PLAYER_HEAD, "Glowstone dust distillate"),
        SCORCHED_POWER_CRYSTAL(Items.PLAYER_HEAD, "Scorched power crystal"),
        WILSON_ENGINEERING_PLANS(Items.PAPER, "Wilson's engineering plans"),
        ARCHFIEND_DICE(Items.PLAYER_HEAD, "Archfiend dice"),
        FLAWED_OPAL_GEM(Items.PLAYER_HEAD, "Flawed opal gem"),
        HIGH_CLASS_ARCHFIEND_DICE(Items.PLAYER_HEAD, "High class dice"),
        SUBZERO_INVERTER(Items.PLAYER_HEAD, "Subzero inverter"),
        FLAME_DYE(Items.PLAYER_HEAD, "Flame dye");

        companion object {
            fun find(id: String, item: Item): Blaze? {
                return entries.find { it.item == item && it.name == id }
            }
        }
    }

    private enum class Vamp(val item: Item, val str: String) {
        COVEN_SEAL(Items.NETHER_WART, "Coven seal"),
        CHOCOLATE_CHIP(Items.COOKIE, "Chocolate chip"),
        ENCHANTED_BOOK_BUNDLE_QUANTUM(Items.PLAYER_HEAD, "Bundle of quantum book"),
        GUARDIAN_LUCKY_BLOCK(Items.PLAYER_HEAD, "Guardian lucky block"),
        ENCHANTMENT_ULTIMATE_THE_ONE(Items.PLAYER_HEAD, "Bundle of The One book"),
        MCGRUBBER_BURGER(Items.PLAYER_HEAD, "McGrubber burger"),
        UNFANGED_VAMPIRE_PART(Items.PLAYER_HEAD, "Unfanged vampire part"),
        DYE_SANGRIA(Items.PLAYER_HEAD, "Sangria dye"),
        BUBBA_BLISTER(Items.PLAYER_HEAD, "Bubba blister");

        companion object {
            fun find(id: String, item: Item): Vamp? {
                return entries.find { it.item == item && it.name == id }
            }
        }
    }
}