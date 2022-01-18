package filters.blur

import org.openrndr.applicationAsync
import org.openrndr.draw.colorBuffer
import org.openrndr.draw.loadImageSuspend
import org.openrndr.extra.fx.blur.Bloom
import org.openrndr.extra.fx.blur.BoxBlur
import org.openrndr.extra.fx.blur.FrameBlur
import org.openrndr.extras.imageFit.imageFit
import org.openrndr.math.map


suspend fun frameblurtest() = applicationAsync {
    program {
        val img = loadImageSuspend("pm5544.png")
        val cb = colorBuffer(width, height)
        val blur = FrameBlur()

        extend {
            blur.blend = mouse.position.x.map(0.0,drawer.width.toDouble(), 0.0, 1.0)
            blur.apply(img, cb)
            drawer.imageFit(cb, drawer.bounds)
        }
    }
}