#version 330

// PORTING NOTE: copy of vanilla's assets/minecraft/shaders/post/bits.fsh (read directly from a
// real 26.2 asset clone), with exactly ONE change: `const float Saturation = 1.5;` -> `= 1.0;`.
//
// Same root cause as color_convolve_neutral.fsh right next to this file: vanilla's bits.fsh
// (the "pixelation/posterize" pass, driven by Resolution/MosaicSize) ALSO carries its own,
// separate, always-on 1.5x saturation boost baked into the shader source, on top of
// color_convolve's 1.8x. Combined, that's why default settings looked so oversaturated -
// two unrelated hardcoded boosts stacking (~2.7x total), neither controllable from JSON
// uniforms, regardless of Resolution/MosaicSize being at their neutral values (100 / 1).
//
// Unlike color_convolve's Saturation, this one isn't wired to a real uniform/slider here -
// there's no existing option in this mod that maps naturally to "posterize pass saturation"
// (it reads as an internal contrast-compensation constant for the posterize effect itself,
// not a user-facing control vanilla ever exposed either). Just neutralizing it to 1.0 is
// enough to make this pass a true no-op at default Resolution/MosaicSize, matching vanilla.
//
// SECOND CHANGE (added when you asked for a real "MosaicSize = 0 turns mosaic off"): the
// original formula `mosaicInSize = InSize / MosaicSize` divides by MosaicSize directly, so
// MosaicSize = 0 would divide by zero (NaN/undefined - would have broken the whole pass, not
// disabled it). While tracing through the math to add a safe "off" branch, found that even
// the OLD default, MosaicSize = 1, was never quite a true no-op either: with 1-pixel blocks,
// texCoord*mosaicInSize lands exactly on integer pixel boundaries, so `texCoord - fractPix`
// samples exactly AT a texel edge instead of its center - with nearest-neighbor filtering
// (this pass doesn't request bilinear) that edge case can round to the wrong neighboring
// pixel, silently shifting the whole image by up to 1 texel. So the guard below treats
// anything under 1.5 (covers both the new 0 = "off" and the old default 1) as "skip the
// mosaic step entirely, sample texCoord directly" - this both adds the off switch you asked
// for AND fixes that latent 1-texel-shift edge case at the previous default, at no cost.
//
// THIRD CHANGE (auditing every slider for an exact vanilla-match position): the posterize
// step, `baseTexel.rgb - fract(baseTexel.rgb * Resolution) / Resolution`, quantizes each
// color channel to steps of 1/Resolution. Resolution's slider only goes up to 100 (its
// existing max, already the default), so even at max this was an ~0.01-sized step - a real,
// if extremely subtle, deviation from a true identity, not a mathematically exact one. Since
// this is already our own custom shader, added the same kind of bypass as the MosaicSize
// case above: at Resolution's max (100), skip the quantization math entirely instead of
// merely approximating identity with a very fine step. Below 100 the slider still quantizes
// exactly as before - only the top end of the range is now a true no-op.
uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

layout(std140) uniform BitsConfig {
    float Resolution;
    float MosaicSize;
};

out vec4 fragColor;

const float Saturation = 1.0;

void main() {
    vec2 oneTexel = 1.0 / InSize;

    vec4 baseTexel;
    if (MosaicSize < 1.5) {
        // "Off" - MosaicSize 0 (new) or 1 (old default): sample straight, no block-snapping.
        baseTexel = texture(InSampler, texCoord);
    } else {
        vec2 mosaicInSize = InSize / MosaicSize;
        vec2 fractPix = fract(texCoord * mosaicInSize) / mosaicInSize;
        baseTexel = texture(InSampler, texCoord - fractPix);
    }

    vec3 fractTexel;
    if (Resolution >= 100.0) {
        // "Off" - Resolution at its slider max: no quantization, use the color as-is.
        fractTexel = baseTexel.rgb;
    } else {
        fractTexel = baseTexel.rgb - fract(baseTexel.rgb * Resolution) / Resolution;
    }
    float luma = dot(fractTexel, vec3(0.3, 0.59, 0.11));
    vec3 chroma = (fractTexel - luma) * Saturation;
    baseTexel.rgb = luma + chroma;
    baseTexel.a = 1.0;

    fragColor = baseTexel;
}
