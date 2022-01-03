import kotlinx.coroutines.await
import org.openrndr.applicationAsync
import org.openrndr.color.ColorRGBa

suspend fun audiotest() = applicationAsync {
    configure {
        title = "Audio player test"
    }

    program {
        val audio = loadAudio("./phonk 1.mp3").await()
        audio.play()

        keyboard.keyDown.listen {
            audio.currentTime = 0.0
            audio.play()
        }

        extend {
            drawer.clear(ColorRGBa.BLUE)

        }
    }
}

