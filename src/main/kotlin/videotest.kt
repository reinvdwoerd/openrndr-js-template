import kotlinx.coroutines.await
import org.openrndr.applicationAsync
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.tint
import org.openrndr.extras.imageFit.imageFit

suspend fun videotest() = applicationAsync {
    configure {
        title = "Video player test"
    }

    program {
        val video = loadVideo("./amazone is verkrekt.mp4").await()

        video.videoElement.apply {
            muted = true
            loop = true
            play()
        }

        extend {
            video.update()

            drawer.clear(ColorRGBa.BLUE)
            drawer.imageFit(video.colorBuffer, drawer.bounds)
            drawer.drawStyle.colorMatrix = tint(ColorRGBa.BLUE)
        }
    }
}

