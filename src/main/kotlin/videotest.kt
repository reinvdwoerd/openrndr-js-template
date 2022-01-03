import kotlinx.coroutines.await
import org.openrndr.applicationAsync
import org.openrndr.color.ColorRGBa

suspend fun videotest() = applicationAsync {
    configure {
        title = "Video player test"
    }

    program {
        val video = loadVideo("./phonk 1.mp3").await()



        extend {
            video.update()
            drawer.clear(ColorRGBa.BLUE)

            drawer.image(video.colorBuffer)
        }
    }
}

