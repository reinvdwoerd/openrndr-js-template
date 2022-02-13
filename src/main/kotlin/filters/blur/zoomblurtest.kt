package filters.blur

import org.openrndr.applicationAsync
import org.openrndr.draw.ColorBuffer
import org.openrndr.draw.Filter
import org.openrndr.draw.colorBuffer
import org.openrndr.draw.loadImageSuspend
import org.openrndr.extra.fx.mppFilterShader
import org.openrndr.extra.parameters.Description
import org.openrndr.extra.parameters.DoubleParameter
import org.openrndr.extras.imageFit.imageFit
import org.openrndr.math.Vector2
import org.openrndr.math.map


const val fx_zoom_blur = """

uniform sampler2D tex0; // input
uniform vec2 center;
uniform float strength;
uniform vec2 dimensions;

#ifdef OR_IN_OUT
in vec2 v_texCoord0;
#else
varying vec2 v_texCoord0;
#endif

#ifndef OR_GL_FRAGCOLOR
out vec4 o_output;
#endif


float random(vec3 scale, float seed) {
    /* use the fragment position for a different seed per-pixel */
    return fract(sin(dot(gl_FragCoord.xyz + seed, scale)) * 43758.5453 + seed);
}

// Implementation by Evan Wallace (glfx.js)
void main() {
    vec4 color = vec4(0.0);
    float total = 0.0;
    vec2 toCenter = center - v_texCoord0;

    /* randomize the lookup values to hide the fixed number of samples */
    float offset = random(vec3(12.9898, 78.233, 151.7182), 0.0);

    for (float t = 0.0; t <= 40.0; t++) {
        float percent = (t + offset) / 40.0;
        float weight = 4.0 * (percent - percent * percent);
        
        #ifndef OR_GL_TEXTURE2D
        vec4 tex = texture(tex0, v_texCoord0 + toCenter * percent * strength);
        #else
        vec4 tex = texture2D(tex0, v_texCoord0 + toCenter * percent * strength);
        #endif

        /* switch to pre-multiplied alpha to correctly blur transparent images */
        tex.rgb *= tex.a;

        color += tex * weight;
        total += weight;
    }

    
    #ifndef OR_GL_FRAGCOLOR
    o_color = color / total;
    /* switch back from pre-multiplied alpha */
    o_color.rgb /= o_color.a + 0.00001;
    #else
    gl_FragColor = color / total;
    /* switch back from pre-multiplied alpha */
    gl_FragColor.rgb /= gl_FragColor.a + 0.00001;
    #endif
    
}"""


@Description("Zoom Blur")
class ZoomBlur : Filter(mppFilterShader(fx_zoom_blur, "zoom-blur")) {
    var center: Vector2 by parameters

    @DoubleParameter("strength", 0.0, 1.0)
    var strength: Double by parameters

    init {
        center = Vector2.ONE / 2.0
        strength = 0.2
    }

    private var intermediate: ColorBuffer? = null

    override fun apply(source: Array<ColorBuffer>, target: Array<ColorBuffer>) {
        intermediate?.let {
            if (it.width != target[0].width || it.height != target[0].height) {
                intermediate = null
            }
        }

        if (intermediate == null) {
            intermediate =
                colorBuffer(target[0].width, target[0].height, target[0].contentScale, target[0].format, target[0].type)
        }

        intermediate?.let {
            parameters["dimensions"] = Vector2(it.effectiveWidth.toDouble(), it.effectiveHeight.toDouble())

            super.apply(source, arrayOf(it))

            it.copyTo(target[0])
        }
    }
}




suspend fun zoomblurtest() = applicationAsync {
    program {
        val img = loadImageSuspend("pm5544.png")
        val cb = colorBuffer(width, height)
        val blur = ZoomBlur()

        extend {
            blur.strength = mouse.position.x.map(0.0,drawer.width.toDouble(), 1.0, 4.0)
            blur.apply(img, cb)
            drawer.imageFit(cb, drawer.bounds)
        }
    }
}