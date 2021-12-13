import kotlinx.coroutines.await
import org.openrndr.applicationAsync
import org.openrndr.color.ColorRGBa


suspend fun main() = applicationAsync {
    configure {
        title = "Webcam test"
    }

    program {
        val camera = loadVideoDevice().await()

        keyboard.keyDown.listen {
            camera.update()
        }

        extend {
            drawer.clear(ColorRGBa.PINK)
            drawer.image(camera.colorBuffer)
        }
    }
}



