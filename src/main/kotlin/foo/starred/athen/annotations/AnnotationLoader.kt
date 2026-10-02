package foo.starred.athen.annotations

import foo.starred.athen.events.GameEvent
import foo.starred.athen.events.core.on
import foo.starred.athen.modules.Module
import foo.starred.klassgraph.KlassGraph
import foo.starred.snowbird.utils.safely

object AnnotationLoader {
    fun load(load: String = "foo.starred.athen") {
        KlassGraph.scan(AnnotationLoader::class.java, load).use { result ->
            val list0 = result.annotated<Priority>().sortedBy { it.getAnnotation(Priority::class.java)?.value ?: 0 }
            val list1 = result.annotated<Load>()

            for (klass in list0) {
                safely {
                    Class.forName(klass.name)
                }
            }

            for (klass in list1) {
                safely {
                    Class.forName(klass.name)
                }
            }

            val modules = result.subtypes<Module>()
            on<GameEvent.Start> {
                for (module in modules) {
                    safely {
                        module.kotlin.objectInstance?.observable
                    }
                }
            }.once()
        }
    }
}
