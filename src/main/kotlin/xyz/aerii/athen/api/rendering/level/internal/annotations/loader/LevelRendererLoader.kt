package xyz.aerii.athen.api.rendering.level.internal.annotations.loader

import io.github.classgraph.ClassGraph
import xyz.aerii.athen.annotations.Priority
import xyz.aerii.athen.api.rendering.level.impl.queue.impl.LevelQueueImpl
import xyz.aerii.athen.api.rendering.level.impl.renderers.base.ILevelRenderer
import xyz.aerii.athen.api.rendering.level.internal.annotations.impl.LevelRenderer
import xyz.aerii.library.utils.safely

@Priority
object LevelRendererLoader {
    init {
        ClassGraph()
            .enableClassInfo()
            .enableAnnotationInfo()
            .acceptPackages("xyz.aerii.athen.api.rendering.level.impl.renderers.impl")
            .scan()
            .use { s ->
                val a = s.getClassesWithAnnotation(LevelRenderer::class.java).loadClasses()

                loop@ for (k in a) {
                    safely {
                        Class.forName(k.name)
                        LevelQueueImpl.renderers.add(k.kotlin.objectInstance as? ILevelRenderer ?: continue@loop)
                    }
                }
            }
    }
}