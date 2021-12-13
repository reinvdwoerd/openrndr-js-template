import kotlinx.browser.document
import kotlinx.browser.window
import org.openrndr.draw.ColorBuffer
import org.openrndr.draw.colorBuffer
import org.w3c.dom.HTMLVideoElement
import org.w3c.dom.mediacapture.MediaStreamConstraints
import kotlin.js.Promise


class VideoDevice(
    private val videoElement: HTMLVideoElement,
    val colorBuffer: ColorBuffer
    ) {

    fun update() {
        colorBuffer.write(videoElement)
    }
}


fun loadVideoDevice(): Promise<VideoDevice> {
    return Promise() { resolve: (VideoDevice) -> Unit, reject ->
        window.navigator.getUserMedia(MediaStreamConstraints(true,  false), { stream ->
            val video = document.createElement("video") as HTMLVideoElement
            video.srcObject = stream
            video.play()

            video.addEventListener("playing",{
                console.log("start playing")
                val videoBuffer = colorBuffer(video.videoWidth, video.videoHeight)
                resolve(VideoDevice(video, videoBuffer))

                if (video.videoWidth == 0) {
                    console.error("videoWidth is 0. Camera not connected?")
                }
            })
        }, {
            reject(it)
        })
    }
}