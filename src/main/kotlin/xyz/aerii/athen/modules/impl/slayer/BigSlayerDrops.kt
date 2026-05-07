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
    private val set0a = setOf("UNDEAD_CATALYST", "SEVERED_HAND", "BEHEADED_HORROR", "REVENANT_CATALYST", "SHARD_OF_THE_SHREDDED", "WARDEN_HEART", "DYE_MATCHA")
    private val set0b = setOf("ewogICJ0aW1lc3RhbXAiIDogMTcxOTUwNDEyOTIyMiwKICAicHJvZmlsZUlkIiA6ICIxNzM1MGE5OWQ3MzQ0NDBjYTY0YzJjMDU3YTNjMWM4ZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJHaWxkZWRoZXJvNTY5MSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9hOGM0ODExMzk1ZmJmN2Y2MjBmMDVjYzMxNzVjZWYxNTE1YWFmNzc1YmEwNGEwMTA0NTAyN2YwNjkzYTkwMTQ3IiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=", "ewogICJ0aW1lc3RhbXAiIDogMTcwOTMwNjAwOTQ3NywKICAicHJvZmlsZUlkIiA6ICI1ZjQ5N2JmZDQwODU0NjRhOTNiMTRjN2Y3OTc5ZGYyNCIsCiAgInByb2ZpbGVOYW1lIiA6ICJUVEs5ODciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmM0YTY1YzY4OWIyZDM2NDA5MTAwYTYwYzJhYjhkM2QwYTY3Y2U5NGVlYTNjMWY3YWM5NzRmZDg5MzU2OGI1ZCIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9")
    private val set1a = setOf("SPIDER_CATALYST", "TARANTULA_CATALYST", "VIAL_OF_VENOM", "TARANTULA_TALISMAN", "SHRIVELED_WASP", "ENSNARED_SNAIL", "PRIMORDIAL_EYE", "DYE_BRICK_RED")
    private val set1b = setOf("ewogICJ0aW1lc3RhbXAiIDogMTcxOTUwNDQ3MDQ3NywKICAicHJvZmlsZUlkIiA6ICIzOTVkZTJlYjVjNjU0ZmRkOWQ2NDAwY2JhNmNmNjFhNyIsCiAgInByb2ZpbGVOYW1lIiA6ICJzcGFyZXN0ZXZlIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzQzYTFhZDRmY2M0MmZiNjNjNjgxMzI4ZTQyZDYzYzgzY2ExOTNiMzMzYWYyYTQyNjcyOGEyNWE4Y2M2MDA2OTIiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==", "ewogICJ0aW1lc3RhbXAiIDogMTcxOTUwMzQyNTYyNywKICAicHJvZmlsZUlkIiA6ICI2ZjhlYWI1MTVmNTc0MmRhOWYxZDYzMzY1ODAxMDU4YyIsCiAgInByb2ZpbGVOYW1lIiA6ICJDaW5kZXJGb3hfMjAwNiIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9iOGQwNWZkNGZjNmZkMWNiMzJjY2JhYmU4NzA0MGEyOTZiNTE0MjdiNGZhNWNlNTdiZTViNDExZDg2ZTIzNGM4IiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=")
    private val set2a = setOf("FURBALL", "DYE_CELESTE")
    private val set2b = setOf("ewogICJ0aW1lc3RhbXAiIDogMTcxOTUwNDE1NTc5MSwKICAicHJvZmlsZUlkIiA6ICI4YjA2ZmU5ZGNjNjg0NDNmYWNmM2QzODA0NWNkNTMyNiIsCiAgInByb2ZpbGVOYW1lIiA6ICJDYXN0aWVsY3ZyIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2M3MzhiOGFmOGQ3Y2UxYTI2ZGM2ZDQwMTgwYjM1ODk0MDNlMTFlZjM2YTY2ZDdjNDU5MDAzNzczMjgyOTU0MmUiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==", "ewogICJ0aW1lc3RhbXAiIDogMTcxOTUwMzU0NzI1NywKICAicHJvZmlsZUlkIiA6ICJhNTdmZDE5MGZmM2U0YjBkYTEzMmY2OGUzOTU3ZjViMSIsCiAgInByb2ZpbGVOYW1lIiA6ICJ4SGFubmFoNyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS83MzRmYjMyMDMyMzNlZmJhZTgyNjI4YmQ0ZmNhNzM0OGNkMDcxZTViN2I1MjQwN2YxZDFkMjc5NGUzMTc5OWZmIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=")
    private val set3a = setOf("SUMMONING_EYE", "HAZMAT_ENDERMAN", "POCKET_ESPRESSO_MACHINE", "HANDY_BLOOD_CHALICE", "SINFUL_DICE", "EXCEEDINGLY_RARE_ENDER_ARTIFACT_UPGRADE", "ETHERWARP_MERGER", "JUDGEMENT_CORE", "ENDSTONE_IDOL", "DYE_BYZANTIUM", "TRANSMISSION_TUNER")
    private val set3b = setOf("ewogICJ0aW1lc3RhbXAiIDogMTcxOTUwMzYzMjkyMiwKICAicHJvZmlsZUlkIiA6ICI1ZjU5NmViY2JlOTQ0NmQxYmI0M2JlNGYzZjRiOGJlNSIsCiAgInByb2ZpbGVOYW1lIiA6ICJUZWlsMHNzIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2MzYTlhY2JiN2QzZDQ5YjFkNTRkMjYxMTExMDRkMGRhNTdkOGI0YWIzNzg4NWI0YmJkMjQwYWM3MTA3NGNhZDIiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==", "ewogICJ0aW1lc3RhbXAiIDogMTYyODYzNTU3NDI0OSwKICAicHJvZmlsZUlkIiA6ICI0ZTMwZjUwZTdiYWU0M2YzYWZkMmE3NDUyY2ViZTI5YyIsCiAgInByb2ZpbGVOYW1lIiA6ICJfdG9tYXRvel8iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2IxMWZiOTBkYjdmNTdiZWI0MzU5NTQwMTNiMWM3ZWY3NzZjNmJkOTZjYmYzMzA4YWE4ZWJhYzI5NTkxZWJiZCIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9", "eyJ0aW1lc3RhbXAiOjE1ODY5MTE5MTcyODAsInByb2ZpbGVJZCI6IjNmYzdmZGY5Mzk2MzRjNDE5MTE5OWJhM2Y3Y2MzZmVkIiwicHJvZmlsZU5hbWUiOiJZZWxlaGEiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzhmZmY0MWUxYWZjNTk3YjE0Zjc3YjhlNDRlMmExMzRkYWJlMTYxYTE1MjZhZGU4MGU2MjkwZjJkZjMzMWRjMTEifX19")
    private val set4a = setOf("ARROW_BUNDLE_MAGMA", "MANA_DISINTEGRATOR", "SCORCHED_BOOKS", "KELVIN_INVERTER", "GLOWSTONE_DUST_DISTILLATE", "SCORCHED_POWER_CRYSTAL", "ARCHFIEND_DICE", "FLAWED_OPAL_GEM", "HIGH_CLASS_ARCHFIEND_DICE", "SUBZERO_INVERTER", "FLAME_DYE")
    private val set4b = setOf("ewogICJ0aW1lc3RhbXAiIDogMTcxOTUwNDcyMTAxMiwKICAicHJvZmlsZUlkIiA6ICJmNmYxY2IxMmYzNDU0MDRlYjZlNjU2NGE2ZDlmMjU2NyIsCiAgInByb2ZpbGVOYW1lIiA6ICJBdXJlbGl1c0dlbWluaSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS84YzhjY2Q1Zjg2M2Q4MmJiMDk3YjkyNmJjNWY0Y2NhOTdiMTlmNDZlMTFiM2EzYTU5ZDAwMWFkYjg5ODg2NzczIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=", "ewogICJ0aW1lc3RhbXAiIDogMTY1Nzk5NDkxODg4NiwKICAicHJvZmlsZUlkIiA6ICJhNzdkNmQ2YmFjOWE0NzY3YTFhNzU1NjYxOTllYmY5MiIsCiAgInByb2ZpbGVOYW1lIiA6ICIwOEJFRDUiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGQ2MjBlNGUzZDNhYmZlZDZhZDgxYTU4YTU2YmNkMDg1ZDllOWVmYzgwM2NhYmIyMWZhNmM5ZTM5NjllMmQyZSIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9")
    private val set5a = setOf("ENCHANTED_BOOK_BUNDLE_QUANTUM", "GUARDIAN_LUCKY_BLOCK", "ENCHANTMENT_ULTIMATE_THE_ONE", "MCGRUBBER_BURGER", "UNFANGED_VAMPIRE_PART", "DYE_SANGRIA", "BUBBA_BLISTER")
    private val set5b = setOf("ewogICJ0aW1lc3RhbXAiIDogMTY4MTUxOTM1Mjc5NCwKICAicHJvZmlsZUlkIiA6ICI4N2YzOGM1MWE4Yzc0MmNmYTY2YTgxNWExZTI2NzMzYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJCZWR3YXJzQ3V0aWUiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjVmZmRmYmQ0OTBmYzczMTBkNjFhMWM0YzM1YTRlMGNkMmY5ZmNjYzEyMzljNmE0YmNkN2RlYzA1ZTI1ZWE2NyIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9")

    private val set = mutableSetOf<Vec3>()

    val scale by config.slider("Scale", 3f, 1f, 10f)
    private val range by config.slider("Range multiplier", 1.0, 0.5, 5.0, "blocks", true)
    private val unscale by config.slider("Unscale after", 15, 5, 60, "seconds")
    private val selected by config.multiCheckbox("Enable for", listOf("Revenant", "Tarantula", "Sven", "Voidgloom", "Riftstalker", "Blaze"), listOf(0, 1, 2, 3, 4, 5))

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

    private fun fn0(id: String, it: Item, tx: String?): Boolean {
        if (0 !in selected) return false

        return when (it) {
            Items.ROTTEN_FLESH -> id == "REVENANT_FLESH"
            Items.CHARCOAL -> id == "FOUL_FLESH"
            Items.DIAMOND -> id == "SCYTHE_BLADE"
            Items.COOKED_PORKCHOP -> id == "REVENANT_VISCERA"
            Items.PLAYER_HEAD -> id in set0a || tx in set0b
            else -> false
        }
    }

    private fun fn1(id: String, it: Item, tx: String?): Boolean {
        if (1 !in selected) return false

        return when (it) {
            Items.STRING -> id == "TARANTULA_WEB"
            Items.LIME_DYE -> id == "TOXIC_ARROW_POISON"
            Items.COBWEB -> id == "TARANTULA_SILK"
            Items.GOLDEN_SHOVEL -> id == "FLY_SWATTER"
            Items.ROTTEN_FLESH -> id == "DIGESTED_MOSQUITO"
            Items.PLAYER_HEAD -> id in set1a || tx in set1b
            else -> false
        }
    }

    private fun fn2(id: String, it: Item, tx: String?): Boolean {
        if (2 !in selected) return false

        return when (it) {
            Items.GHAST_TEAR -> id == "WOLF_TOOTH"
            Items.OAK_TRAPDOOR -> id == "HAMSTER_WHEEL"
            Items.MOOSHROOM_SPAWN_EGG -> id == "RED_CLAW_EGG"
            Items.SALMON -> id == "GRIZZLY_BAIT"
            Items.QUARTZ -> id == "OVERFLUX_CAPACITOR"
            Items.PLAYER_HEAD -> id in set2a || tx in set2b
            else -> false
        }
    }

    private fun fn3(id: String, it: Item, tx: String?): Boolean {
        if (3 !in selected) return false

        return when (it) {
            Items.FIREWORK_STAR -> id == "NULL_SPHERE"
            Items.PURPLE_DYE -> id == "TWILIGHT_ARROW_POISON"
            Items.OAK_BUTTON -> id == "NULL_ATOM"
            Items.PLAYER_HEAD -> id in set3a || tx in set3b
            else -> false
        }
    }

    private fun fn4(id: String, it: Item, tx: String?): Boolean {
        if (4 !in selected) return false

        return when (it) {
            Items.GUNPOWDER -> id == "DERELICT_ASHE"
            Items.BLAZE_POWDER -> id == "ENCHANTED_BLAZE_POWDER"
            Items.PAPER -> id == "WILSON_ENGINEERING_PLANS"
            Items.PLAYER_HEAD -> id in set4a || tx in set4b
            else -> false
        }
    }

    private fun fn5(id: String, it: Item, tx: String?): Boolean {
        if (5 !in selected) return false

        return when (it) {
            Items.NETHER_WART -> id == "COVEN_SEAL"
            Items.COOKIE -> id == "CHOCOLATE_CHIP"
            Items.PLAYER_HEAD -> id in set5a || tx in set5b
            else -> false
        }
    }
}