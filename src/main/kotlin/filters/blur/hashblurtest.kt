package filters.blur

import org.openrndr.applicationAsync
import org.openrndr.draw.Filter
import org.openrndr.draw.colorBuffer
import org.openrndr.draw.loadImageSuspend
import org.openrndr.extra.fx.mppFilterShader
import org.openrndr.extras.imageFit.imageFit
import org.openrndr.math.map


const val fx_hash_blur = """// based on Hashed blur by David Hoskins.
// License Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.

#ifdef OR_IN_OUT
in vec2 v_texCoord0;
#else
varying vec2 v_texCoord0;
#endif

uniform sampler2D tex0;
uniform vec2 textureSize0;
uniform float radius;
uniform float time;
uniform int samples;
uniform float gain;

#ifndef OR_GL_FRAGCOLOR
out vec4 o_color;
#endif

#define TAU 6.28318530718

//-------------------------------------------------------------------------------------------
#define HASHSCALE 443.8975
vec2 hash22(vec2 p) {
	vec3 p3 = fract(vec3(p.xyx) * HASHSCALE);
    p3 += dot(p3, p3.yzx+19.19);
    return fract(vec2((p3.x + p3.y)*p3.z, (p3.x+p3.z)*p3.y));
}

vec2 sampleTexture(inout vec2 r) {
	r = fract(r * vec2(33.3983, 43.4427));
	//return r-.5;
	return sqrt(r.x+.001) * vec2(sin(r.y * TAU), cos(r.y * TAU))*.5; // <<=== circular sampling.
}


//-------------------------------------------------------------------------------------------
vec4 blur(vec2 uv, float radius) {
    vec2 circle = vec2(radius) * (vec2(1.0) / textureSize0);
	vec2 random = hash22(uv + vec2(time));

	vec4 acc = vec4(0.0);
    
	for (int i = 0; i < 100; i++) {
        if (i > samples) break;
        #ifndef OR_GL_TEXTURE2D
		acc += texture(tex0, uv + circle * sampleTexture(random));
        #else
        acc += texture2D(tex0, uv + circle * sampleTexture(random));
        #endif
    }
	return acc / float(samples);
}

//-------------------------------------------------------------------------------------------
void main() {
	vec2 uv = v_texCoord0;
    float radiusSqr = pow(radius, 2.0);
    
    vec4 result = blur(uv, radiusSqr);
    result.rgb *= gain;
    
    #ifdef OR_GL_FRAGCOLOR
    gl_FragColor = result;
    #else
    o_color = result;
    #endif
}"""



class HashBlur : Filter(mppFilterShader(fx_hash_blur, "hash-blur")) {
    /**
     * Blur radius in pixels, default is 5.0
     */
    var radius: Double by parameters

    /**
     * Time/seed, this should be fed with seconds, default is 0.0
     */
    var time: Double by parameters

    /**
     * Number of samples, default is 30
     */
    var samples: Int by parameters

    /**
     * Post-blur gain, default is 1.0
     */
    var gain: Double by parameters

    init {
        radius = 5.0
        time = 0.0
        samples = 30
        gain = 1.0
    }
}

suspend fun hashblurtest() = applicationAsync {
    program {
        val img = loadImageSuspend("pm5544.png")
        val cb = colorBuffer(width, height)
        val blur = HashBlur()

        extend {
            blur.samples = mouse.position.y.map(0.0, drawer.width.toDouble(), 10.0, 100.0).toInt()
            print(blur.samples)

            blur.radius = mouse.position.x.map(0.0,drawer.width.toDouble(), 1.0, 4.0)
            blur.apply(img, cb)
            drawer.imageFit(cb, drawer.bounds)
        }
    }
}