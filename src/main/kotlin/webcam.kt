import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.await
import org.openrndr.applicationAsync
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.ColorBuffer
import org.openrndr.draw.colorBuffer
import org.w3c.dom.HTMLVideoElement
import org.w3c.dom.mediacapture.MediaStreamConstraints
import kotlin.js.Promise


fun loadVideoDevice(): Promise<ColorBuffer> {
    return Promise() { resolve: (ColorBuffer) -> Unit, reject ->
        val video = document.createElement("video") as HTMLVideoElement
        var videoBuffer: ColorBuffer? = null

        window.navigator.getUserMedia(MediaStreamConstraints(true,  false), { stream ->
            video.srcObject = stream
            video.play()
            video.addEventListener("playing",{
                console.log("start playing")
                videoBuffer = colorBuffer(video.videoWidth, video.videoHeight)
                resolve(videoBuffer!!)

                if (video.videoWidth == 0) {
                    console.error("videoWidth is 0. Camera not connected?")
                }
            })

            fun update() {
                if (videoBuffer != null) {
//                    println("update!")
                    videoBuffer!!.write(video)
                }
                window.requestAnimationFrame { update() }
            }

            window.requestAnimationFrame {
                update()
            }

        }, {
            println(it)
        })
    }
}


suspend fun main() = applicationAsync {
    val nav = window.navigator
    val w = window

    configure {
        title = "Webcam test"
        hideCursor = true
    }

    program {
        val camera = loadVideoDevice().await()

        extend {
            drawer.clear(ColorRGBa.PINK)
            drawer.image(camera)
        }
    }
}