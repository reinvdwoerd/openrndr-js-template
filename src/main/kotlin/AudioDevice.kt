import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.await
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get
import org.w3c.dom.Audio
import org.w3c.dom.HTMLAudioElement
import org.w3c.dom.HTMLVideoElement
import org.w3c.dom.mediacapture.MediaStream
import org.w3c.dom.mediacapture.MediaStreamConstraints
import kotlin.js.Promise


class AudioDevice(
    val stream: MediaStream,
    val audioContext: dynamic
) {
    var fftLevels = mutableListOf<Int>()
    val microphoneLevel: Int
        get() = fftLevels.average().toInt()

    fun startAnalysis(fftSize: Int = 128) {
        val analyser = audioContext.createAnalyser()
        val microphone = audioContext.createMediaStreamSource(stream)
        val javascriptNode = audioContext.createScriptProcessor(2048, 1, 1)
        analyser.smoothingTimeConstant = 0.8
        analyser.fftSize = fftSize
        microphone.connect(analyser)
        analyser.connect(javascriptNode)
        javascriptNode.connect(audioContext.destination)
        javascriptNode.onaudioprocess = {
            val array = Uint8Array(analyser.frequencyBinCount as Int)
            analyser.getByteFrequencyData(array)
            fftLevels = mutableListOf<Int>()
            val length = array.length
            for (i in 0 until length) {
                fftLevels.add(array.get(i).toInt())
            }
        }
    }
}


fun loadAudioDevice(): Promise<AudioDevice> {
    return Promise() { resolve, reject ->
        window.navigator.mediaDevices.getUserMedia(MediaStreamConstraints(false,  true)).then { stream ->
            val audioContext: dynamic = js("new AudioContext()") // explicit type definition is not required
            resolve(AudioDevice(stream, audioContext))
        }
    }
}


fun loadAudio(src: String): Promise<HTMLAudioElement> {
    return Promise() { resolve, reject ->
        val audio = document.createElement("audio") as HTMLAudioElement
        audio.src = src
        console.log(audio)

        audio.addEventListener("abort", { event ->
            println(event)
        })

        audio.addEventListener("error", { event ->
            println(event)
        })

        audio.addEventListener("canplay", { event ->
            println(event)
            resolve(audio)
        })
    }
}