import org.openrndr.applicationAsync
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.colorBuffer
import org.openrndr.draw.loadImage

suspend fun draganddroptest() = applicationAsync {
    configure {
        title = "Drag and drop test"
    }

    program {
        var firstImage = colorBuffer(width, height)

        program.window.drop.listen {
            println(it)
            println(it.files.size)
            firstImage = loadImage(it.files[0])
        }

        extend {
            drawer.clear(ColorRGBa.BLUE)

            drawer.image(firstImage)
        }
    }
}

