import kotlinx.browser.window
import kotlinx.coroutines.await
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get
import org.openrndr.applicationAsync
import org.openrndr.color.ColorRGBa
import org.openrndr.math.map
import org.openrndr.shape.Rectangle
import org.w3c.dom.mediacapture.MediaStreamConstraints




suspend fun main3() = applicationAsync {
    val nav = window.navigator

    configure {
        title = "Microphone test"
        hideCursor = true
    }

    program {
        var microphoneLevels = mutableListOf<Int>()

        val stream = nav.mediaDevices.getUserMedia(MediaStreamConstraints(false,  true)).await()
        val audioContext: dynamic = js("new AudioContext()") // explicit type definition is not required
        val analyser = audioContext.createAnalyser()
        val microphone = audioContext.createMediaStreamSource(stream)
        val javascriptNode = audioContext.createScriptProcessor(2048, 1, 1)
        analyser.smoothingTimeConstant = 0.8
        analyser.fftSize = 128
        microphone.connect(analyser)
        analyser.connect(javascriptNode)
        javascriptNode.connect(audioContext.destination)

        javascriptNode.onaudioprocess = {
            val array = Uint8Array(analyser.frequencyBinCount as Int)
            analyser.getByteFrequencyData(array)
            microphoneLevels = mutableListOf<Int>()
            val length = array.length
            for (i in 0 until length) {
                microphoneLevels.add(array.get(i).toInt())
            }
        }

        extend {
            drawer.clear(ColorRGBa.PINK)
            val margin = 20.0
            val rects = microphoneLevels.mapIndexed { index, i ->
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