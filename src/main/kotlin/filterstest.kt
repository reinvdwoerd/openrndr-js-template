import org.openrndr.applicationAsync
import org.openrndr.draw.Filter
import org.openrndr.draw.colorBuffer
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.fx.mppFilterShader
import org.openrndr.extra.parameters.BooleanParameter
import org.openrndr.extra.parameters.DoubleParameter
import org.openrndr.math.Vector4
import org.openrndr.resourceUrl


const val hashnoise_shader = """
    in vec2 v_texCoord0;
    out vec4 o_color;
    
    uniform sampler2D tex0;
    uniform sampler2D tex1;
    uniform float blendFactor;
    uniform float brightness;
    
    void main() {
        vec3 original = texture(tex0, v_texCoord0).rgb;
        vec3 bloom = texture(tex1, v_texCoord0).rgb;
    
        vec3 hdrColor = mix(original, bloom, blendFactor);
    
        vec3 result = vec3(1.0) - exp(-hdrColor * brightness);
    
        o_color = vec4(result, 1.0);
    }
"""

/**
 * Hash noise filter that produces white-noise-like noise.
 */
class HashNoise : Filter(mppFilterShader(hashnoise_shader, "hash-noise")) {
    /**
     * noise gain per channel, default is Vector4(1.0, 1.0, 1.0, 0.0)
     */
    var gain: Vector4 by parameters

    /**
     * noise bias per channel, default is Vector4(0.0, 0.0, 0.0, 1.0)
     */
    var bias: Vector4 by parameters

    /**
     * is the noise monochrome, default is true
     */
    var monochrome: Boolean by parameters

    /**
     * noise seed, feed it with time to animate
     */
    var seed: Double by parameters

    init {
        monochrome = true
        gain = Vector4(1.0, 1.0, 1.0, 0.0)
        bias = Vector4(0.0, 0.0, 0.0, 1.0)
        seed = 0.0
    }
}



suspend fun filterstest() = applicationAsync {
    program {
        extend(Screenshots())

        extend {
            val cb = colorBuffer(width, height)
            val hn = HashNoise()
            extend {
                hn.seed = seconds
                hn.apply(emptyArray(), cb)
                drawer.image(cb)
            }
        }
    }
}