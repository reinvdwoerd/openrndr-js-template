package filters.grain

import FilmGrain
import org.openrndr.applicationAsync
import org.openrndr.draw.colorBuffer
import org.openrndr.draw.loadImageSuspend
import org.openrndr.extras.imageFit.imageFit


suspend fun filmgraintest() = applicationAsync {
    program {
        val img = loadImageSuspend("pm5544.png")
        val cb = colorBuffer(width, height)
        val grain = FilmGrain()

        extend {
            grain.time = seconds
            grain.apply(img, cb)
            drawer.imageFit(cb, drawer.bounds)
        }
    }
}