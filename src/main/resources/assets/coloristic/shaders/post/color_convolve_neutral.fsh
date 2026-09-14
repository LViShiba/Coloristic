#version 330

// PORTING NOTE: this is a copy of vanilla's assets/minecraft/shaders/post/color_convolve.fsh
// (read directly from a real 26.2 asset clone), with exactly ONE change: the hardcoded
// `const float Saturation = 1.8;` is replaced with a real uniform, `SaturationConfig.Saturation`.
//
// WHY: reusing vanilla's color_convolve.fsh as-is meant the image was ALWAYS pushed through
// a fixed 1.8x saturation boost, even with an identity RedMatrix/GreenMatrix/BlueMatrix (the
// "default" color settings) - that constant isn't something JSON uniforms can override, it's
// baked into the vanilla shader's source. That's what caused the "acid"/oversaturated look at
// default settings you reported. Vanilla itself doesn't care because it never uses this shader
// with an identity matrix - wherever it's used, some saturation boost is presumably wanted.
//
// Fix: put Saturation in its own tiny uniform block (SaturationConfig) so we can drive it from
// the mod's existing `saturation` slider (options.saturation - already wired in GameOptionsMixin
// and ColoristicFx, previously a dead value with nothing real to bind to). Its default is 1.0
// (neutral - no saturation change), which combined with the identity color matrix now makes
// this whole pass a true no-op at default settings, matching vanilla's look. Kept as a SEPARATE
// uniform block from ColorConfig (rather than appending Saturation as a 4th field to ColorConfig)
// specifically to avoid any risk around std140 packing order interacting with the existing,
// already-confirmed-working RedMatrix/GreenMatrix/BlueMatrix layout - a single-float block has
// no packing ambiguity at all.
//
// SECOND ADDITION (you reported the 3 "Color Scale" sliders doing nothing): same root cause
// as Saturation originally was - `colorScale1/2/3` had no real uniform anywhere to bind to,
// since vanilla's color_convolve.fsh has no such concept at all. Since this is already our
// own custom shader, added a genuinely new uniform block, `ColorScaleConfig` (a per-channel
// R/G/B output multiplier, applied AFTER the color matrix and saturation steps - matches the
// sliders' existing range, 0.0-5.0 with 1.0 = neutral/no change, already defined in
// GameOptionsMixin). Same "own separate block, no packing ambiguity" approach as
// SaturationConfig above.
uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

layout(std140) uniform ColorConfig {
    vec3 RedMatrix;
    vec3 GreenMatrix;
    vec3 BlueMatrix;
};

layout(std140) uniform SaturationConfig {
    float Saturation;
};

layout(std140) uniform ColorScaleConfig {
    vec3 ColorScale;
};

const vec3 Gray = vec3(0.3, 0.59, 0.11);

out vec4 fragColor;

void main() {
    vec2 oneTexel = 1.0 / InSize;
    vec4 InTexel = texture(InSampler, texCoord);

    // Color Matrix
    float RedValue = dot(InTexel.rgb, RedMatrix);
    float GreenValue = dot(InTexel.rgb, GreenMatrix);
    float BlueValue = dot(InTexel.rgb, BlueMatrix);
    vec3 OutColor = vec3(RedValue, GreenValue, BlueValue);

    // Saturation
    float Luma = dot(OutColor, Gray);
    vec3 Chroma = OutColor - Luma;
    OutColor = (Chroma * Saturation) + Luma;

    // Color Scale (per-channel output multiplier)
    OutColor *= ColorScale;

    fragColor = vec4(OutColor, 1.0);
}
