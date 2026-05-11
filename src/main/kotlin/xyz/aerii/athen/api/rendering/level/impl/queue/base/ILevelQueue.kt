package xyz.aerii.athen.api.rendering.level.impl.queue.base

import xyz.aerii.athen.api.rendering.level.impl.data.impl.ExtractedBeam
import xyz.aerii.athen.api.rendering.level.impl.data.impl.ExtractedBox
import xyz.aerii.athen.api.rendering.level.impl.data.impl.ExtractedCircle
import xyz.aerii.athen.api.rendering.level.impl.data.impl.ExtractedLine
import xyz.aerii.athen.api.rendering.level.impl.data.impl.ExtractedText
import xyz.aerii.athen.api.rendering.level.impl.queue.data.ExtractedQueue

interface ILevelQueue {
    val beams: MutableList<ExtractedBeam>
    val texts: MutableList<ExtractedText>
    val lines: ExtractedQueue<ExtractedLine>
    val boxes0: ExtractedQueue<ExtractedBox>
    val boxes1: ExtractedQueue<ExtractedBox>
    val circles0: ExtractedQueue<ExtractedCircle>
    val circles1: ExtractedQueue<ExtractedCircle>

    fun clear()
}