package filters.blur

import org.openrndr.applicationAsync
import org.openrndr.draw.colorBuffer
import org.openrndr.draw.loadImageSuspend
import org.openrndr.extra.fx.blur.*
import org.openrndr.extras.imageFit.imageFit
import org.openrndr.math.map


suspend fun lineblurtest() = applicationAsync {
    program {
        val img = loadImageSuspend("pm5544.png")
        val cb = colorBuffer(width, height)
        val blur = LineBlur()

        extend {
            blur.spread = mouse.position.x.map(0.0,drawer.width.toDouble(), 1.0, 4.0)
            blur.apply(img, cb)
            drawer.imageFit(cb, drawer.bounds)
        }
    }
}