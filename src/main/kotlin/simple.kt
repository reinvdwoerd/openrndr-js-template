import org.openrndr.applicationAsync
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.loadImageSuspend
import org.openrndr.webgl.Screenshots


suspend fun main() = applicationAsync {
    configure {
        title = "HOI"
        hideCursor = true
    }

    program {
        val img = loadImageSuspend("./Bucephala-albeola-010.jpg")

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

        extend(Screenshots()) {
            key = "ArrowUp"
        }

        extend {
            val a = ColorRGBa.BLUE
            drawer.clear(a)
            drawer.fill = ColorRGBa.WHITE

            drawer.image(img, 200.0, 0, 200.0, 200.0)

            drawer.rectangle(10.0,10.0, 200.0,200.0)
            drawer.circle(10.0 + 100.0, 300.0, 100.0)

        }
    }
}