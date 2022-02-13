package filters.blur

import org.openrndr.applicationAsync
import org.openrndr.draw.Filter
import org.openrndr.draw.colorBuffer
import org.openrndr.draw.loadImageSuspend
import org.openrndr.extra.fx.mppFilterShader
import org.openrndr.extra.parameters.Description
import org.openrndr.extra.parameters.DoubleParameter
import org.openrndr.extra.parameters.IntParameter
import org.openrndr.extras.imageFit.imageFit
import org.openrndr.math.map


const val fx_gaussian_blur = """

#ifdef OR_IN_OUT
in vec2 v_texCoord0;
#else
varying vec2 v_texCoord0;
#endif

uniform sampler2D tex0;
uniform vec2 textureSize0;

uniform int window;
uniform float sigma;
uniform float spread;
uniform float gain;

#ifndef OR_GL_FRAGCOLOR
out vec4 o_color;
#endif

void main() {

    vec2 s = textureSize0.xy;
    s = vec2(1.0/s.x, 1.0/s.y);

    float w = float(window);

    vec4 sum = vec4(0.0,0.0,0.0,0.0);
    float weight = 0.0;
    for (float y = -25.0; y<= 25.0; ++y) {
        for (float x = -25.0; x<= 25.0; ++x) {
            if (x < -w || x >= w || y < -w || y >= w) break;
        
            float lw = float(exp(-(x*x+y*y) / (2.0 * sigma * sigma))); 
           
            #ifdef OR_GL_TEXTURE2D
            sum += texture2D(tex0, v_texCoord0 + vec2(x,y) * s * spread) * lw;
            #else
            sum += texture(tex0, v_texCoord0 + vec2(x,y) * s * spread) * lw;
            #endif
            
            weight+=lw;
        }
    }
    
    vec4 result = (sum / weight) * gain;

    #ifdef OR_GL_FRAGCOLOR
    gl_FragColor = result; 
    #else
    o_color = result;
    #endif
    
}"""


/**
 * Exact Gaussian blur, implemented as a single pass filter
 */
@Description("Gaussian blur")
class GaussianBlur : Filter(mppFilterShader(fx_gaussian_blur,"gaussian-blur")) {

    /**
     * The sample window, default value is 5
     */
    @IntParameter("window size", 1, 25)
    var window: Int by parameters

    /**
     * Spread multiplier, default value is 1.0
     */
    @DoubleParameter("kernel spread", 1.0, 4.0)
    var spread: Double by parameters

    /**
     * Blur kernel sigma, default value is 1.0
     */
    @DoubleParameter("kernel sigma", 0.0, 25.0)
    var sigma: Double by parameters

    /**
     * Post-blur gain, default value is 1.0
     */
    @DoubleParameter("gain", 0.0, 4.0)
    var gain: Double by parameters

    init {
        window = 5
        spread = 1.0
        sigma = 1.0
        gain = 1.0
    }
}




suspend fun gaussianblurtest() = applicationAsync {
    program {
        val img = loadImageSuspend("pm5544.png")
        val cb = colorBuffer(width, height)
        val blur = GaussianBlur()

        extend {
            blur.spread = mouse.position.x.map(0.0,drawer.width.toDouble(), 1.0, 4.0)
            blur.apply(img, cb)
            drawer.imageFit(cb, drawer.bounds)
        }
    }
}