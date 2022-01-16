import kotlinx.coroutines.await
import org.openrndr.applicationAsync
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.grayscale
import org.openrndr.draw.tint
import org.openrndr.extras.web.loadVideoDevice


suspend fun webcamtest() = applicationAsync {
    configure {
        title = "Webcam test"
    }

    program {
        val camera = loadVideoDevice().await()


        extend {
            camera.update()
            drawer.clear(ColorRGBa.PINK)
//            drawer.drawStyle.colorMatrix = tint(ColorRGBa.WHITE.shade(.5)) //grayscale()
            drawer.image(camera.colorBuffer)
        }
    }
}



