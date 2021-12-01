import org.openrndr.applicationAsync
import org.openrndr.color.ColorRGBa
import org.openrndr.extra.parameters.title


suspend fun main() = applicationAsync {
    configure {
        title = "HOI"
    }

    program {
        mouse.buttonDown.listen {
            kotlin.js.console.log("down")
            clipboard.contents = seconds.toString()
        }

        mouse.buttonUp.listen {
            kotlin.js.console.log("up")
            println(clipboard.contents)
        }

        keyboard.keyDown.listen {
            println(it.name)
            println(keyboard.pressedKeys)
        }

        keyboard.keyUp.listen {
            println(it.name)
            println(keyboard.pressedKeys)
        }

        extend {
            val a = ColorRGBa.BLUE
            drawer.clear(a)
            drawer.fill = ColorRGBa.WHITE
        }
    }
}