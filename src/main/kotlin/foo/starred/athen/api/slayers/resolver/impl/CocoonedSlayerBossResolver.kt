package foo.starred.athen.api.slayers.resolver.impl

import foo.starred.athen.annotations.Load
import foo.starred.athen.api.scheduling.Scheduler
import foo.starred.athen.api.slayers.SlayerAPI
import foo.starred.athen.api.slayers.data.SlayerInfo
import foo.starred.athen.api.slayers.enums.tier.SlayerTier
import foo.starred.athen.api.slayers.enums.type.base.ISlayerType
import foo.starred.athen.api.slayers.resolver.base.GenericSlayerBossResolver
import foo.starred.athen.ducks.entity.EntityDuck.Companion.parent
import foo.starred.athen.events.EntityEvent
import foo.starred.athen.events.LocationEvent
import foo.starred.athen.events.MessageEvent
import foo.starred.athen.events.SlayerEvent
import foo.starred.athen.events.core.on
import foo.starred.snowbird.api.client
import net.minecraft.world.phys.Vec3
import kotlin.math.abs

@Load
object CocoonedSlayerBossResolver {
    private var vec3: Vec3? = null
    private var type: ISlayerType? = null
    private var tier: SlayerTier? = null
    private var cocoon: Int = 0

    init {
        on<SlayerEvent.Boss.Death> {
            if (!slayerInfo.owned) return@on

            vec3 = entity.position()
            type = slayerInfo.type
            tier = slayerInfo.tier
        }

        on<MessageEvent.Chat.Receive> {
            if (stripped.trim() != "YOU COCOONED YOUR SLAYER BOSS") return@on

            cocoon = Scheduler.ticks.server + 120
        }

        on<EntityEvent.Update.Attach>(-100) {
            if (cocoon == 0) return@on
            if (abs(Scheduler.ticks.server - cocoon) > 5) return@on

            val vec0 = vec3 ?: return@on
            val type0 = type ?: return@on
            val tier0 = tier ?: return@on

            if (type0.names.none { stripped.contains(it) }) return@on
            if (SlayerTier.find(stripped) != tier0) return@on

            val parent = entity.parent ?: return@on
            val vec = parent.position()
            val x2 = vec.x - vec0.x
            val z2 = vec.z - vec0.z

            if (x2 * x2 + z2 * z2 > 9) return@on
            if (abs(vec.y - vec0.y) > 5) return@on

            reset()
            GenericSlayerBossResolver.spawn(SlayerAPI.bosses.computeIfAbsent(parent, ::SlayerInfo).apply {
                type = type0
                tier = tier0
                phase = 1
                owner = client.user.name
            })
        }

        on<LocationEvent.Server.Connect> {
            reset()
        }
    }

    private fun reset() {
        vec3 = null
        type = null
        tier = null
        cocoon = 0
    }
}
