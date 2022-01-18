package filters.blur

import org.openrndr.applicationAsync
import org.openrndr.draw.colorBuffer
import org.openrndr.draw.loadImageSuspend
import org.openrndr.extra.fx.blur.Bloom
import org.openrndr.extra.fx.blur.BoxBlur
import org.openrndr.extra.fx.blur.FrameBlur
import org.openrndr.extra.fx.blur.GaussianBlur
import org.openrndr.extras.imageFit.imageFit
import org.openrndr.math.map


suspend fun guassianblurtest() = applicationAsync {
    program {
        val img = loadImageSuspend("pm5544.png")
        val cb = colorBuffer(width, height)
        val blur = GaussianBlur()

        extend {
            blur.spread = mouse.position.x.map(0.0,drawer.width.toDouble(), 1.0, 4.0)
            blur.apply(img, cb)
            drawer.imageFit(cb, drawer.bounds)
        }
    }
}