import kotlinx.browser.document
import kotlinx.browser.window
import org.openrndr.draw.ColorBuffer
import org.openrndr.draw.colorBuffer
import org.w3c.dom.HTMLAudioElement
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
    return Promise() { resolve, reject ->
        window.navigator.mediaDevices.getUserMedia(MediaStreamConstraints(true,  false)).then { stream ->
            val video = document.createElement("video") as HTMLVideoElement
            video.srcObject = stream
            video.play()

            video.addEventListener("playing",{
                console.log("start playing")
                if (video.videoWidth == 0) {
                    reject(Error("videoWidth is 0. Camera not connected?"))
                }

                val videoBuffer = colorBuffer(video.videoWidth, video.videoHeight)
                resolve(VideoDevice(video, videoBuffer))
            })
        }
    }
}


fun loadVideo(src: String): Promise<VideoDevice> {
    return Promise() { resolve, reject ->
        val video = document.createElement("video") as HTMLVideoElement
        video.src = src
        video.play()
        
        video.onload = { event ->
            val videoBuffer = colorBuffer(video.videoWidth, video.videoHeight)
            resolve(VideoDevice(video, videoBuffer))
        }
    }
}