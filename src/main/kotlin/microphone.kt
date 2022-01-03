import kotlinx.browser.window
import kotlinx.coroutines.await
import org.openrndr.applicationAsync
import org.openrndr.color.ColorRGBa
import org.openrndr.math.map
import org.openrndr.shape.Rectangle


suspend fun main3() = applicationAsync {
    val nav = window.navigator

    configure {
        title = "Microphone test"
        hideCursor = true
    }

    program {
        val microphone = loadAudioDevice().await()
        microphone.startAnalysis()

        extend {
            drawer.clear(ColorRGBa.PINK)
            val margin = 20.0
            val rects = microphone.fftLevels.mapIndexed { index, i ->
                Rectangle(
                    margin,
                    index*10.0 + 20.0,
                    i.toDouble().map(0.0,255.0, 0.0, width.toDouble() - 40.0),
                    5.0)
            }

            drawer.rectangles(rects)
        }
    }
}