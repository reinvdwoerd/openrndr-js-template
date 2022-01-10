import org.openrndr.applicationAsync
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.loadImageSuspend
import org.openrndr.math.Vector2
import org.openrndr.shape.contour
//import org.openrndr.extensions.Screenshots


suspend fun primitivestest() = applicationAsync {
    configure {
        title = "HOI"
        hideCursor = true
    }

    program {
        val img = loadImageSuspend("./pm5544.png")

        mouse.buttonDown.listen {
            console.log("mouse down")
//            clipboard.contents = seconds.toString()
        }

        mouse.buttonUp.listen {
            console.log("mouse up")
//            println(clipboard.contents)
        }

        keyboard.keyDown.listen {
            console.log("down", it.name)
            println(keyboard.pressedKeys)
        }

        keyboard.keyUp.listen {
            console.log("up", it.name)
            println(keyboard.pressedKeys)
        }

//        extend(Screenshots()) {
//            key = "ArrowUp"
//        }

        extend {
            drawer.clear(ColorRGBa.PINK)
            drawer.image(img, 20.0, 20.0, 384.0, 288.0)
            drawer.rectangle(20.0, 328.0, 200.0, 200.0)
            drawer.circle(340.0, (328.0 + 100.0), 100.0)
            drawer.lineSegment(20.0, 548.0, 440.0, 548.0)

            val c = contour {
                moveTo(Vector2(440.0, 20.0))
                // -- here `cursor` points to the end point of the previous command
                lineTo(cursor + Vector2(200.0, 0.0))
                lineTo(cursor + Vector2(0.0, 200.0))
                lineTo(anchor)
                close()
            }

            drawer.contour(c)
        }
    }
}