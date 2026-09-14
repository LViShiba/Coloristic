package lv.almer.coloristic.client;

import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.serialization.JsonOps;
import lv.almer.coloristic.Coloristic;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostChainConfig;
import net.minecraft.client.renderer.Projection;
import net.minecraft.client.renderer.ProjectionMatrixBuffer;
import net.minecraft.client.renderer.UniformValue;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import org.joml.Vector3f;

import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ColoristicFx implements ClientTickEvents.EndTick {

    private static final Identifier EFFECT_LOCATION =
            Identifier.fromNamespaceAndPath(Coloristic.MOD_ID, "post_effect/coloristic.json");

    public static final ColoristicFx INSTANCE = new ColoristicFx();

    public float redMatrix1 = 1.0f;
    public float redMatrix2 = 0.0f;
    public float redMatrix3 = 0.0f;
    public float greenMatrix1 = 0.0f;
    public float greenMatrix2 = 1.0f;
    public float greenMatrix3 = 0.0f;
    public float blueMatrix1 = 0.0f;
    public float blueMatrix2 = 0.0f;
    public float blueMatrix3 = 1.0f;
    public float colorScale1 = 1.0f;
    public float colorScale2 = 1.0f;
    public float colorScale3 = 1.0f;
    public float saturation = 1.0f;
    public float resolution = 100.0f;
    public float mosaicSize = 1.0f;
    public float inverseAmount = 0.0f;
    public float radius = 0.0f;

    public boolean showQuickButton = true;

    private int ticks;
    private final Minecraft client = Minecraft.getInstance();

    private PostChain colorShader;

    private boolean loadFailed;

    private PostChain getOrCreateShader() {
        if (this.colorShader == null && !this.loadFailed) {
            try {

                PostChainConfig config;
                try (Reader reader = this.client.getResourceManager()
                        .getResourceOrThrow(EFFECT_LOCATION).openAsReader()) {
                    config = PostChainConfig.CODEC.parse(JsonOps.INSTANCE, GsonHelper.parse(reader)).getOrThrow();
                }

                config = buildLiveConfig(config);

                this.colorShader = PostChain.load(
                        config,
                        this.client.getTextureManager(),
                        Set.of(PostChain.MAIN_TARGET_ID),
                        EFFECT_LOCATION,
                        new Projection(),
                        new ProjectionMatrixBuffer("coloristic_post_chain")
                );

            } catch (Exception e) {

                Coloristic.LOGGER.warn("Coloristic: failed to load post_effect/coloristic.json", e);
                this.loadFailed = true;
            }
        }
        return this.colorShader;
    }

    private PostChainConfig buildLiveConfig(PostChainConfig baseConfig) {
        List<PostChainConfig.Pass> newPasses = new ArrayList<>();
        for (PostChainConfig.Pass pass : baseConfig.passes()) {
            Map<String, List<UniformValue>> uniforms = new HashMap<>(pass.uniforms());
            if (uniforms.containsKey("ColorConfig")) {

                uniforms.put("ColorConfig", List.of(
                        new UniformValue.Vec3Uniform(new Vector3f(this.redMatrix1, this.redMatrix2, this.redMatrix3)),
                        new UniformValue.Vec3Uniform(new Vector3f(this.greenMatrix1, this.greenMatrix2, this.greenMatrix3)),
                        new UniformValue.Vec3Uniform(new Vector3f(this.blueMatrix1, this.blueMatrix2, this.blueMatrix3))
                ));
            }

            if (uniforms.containsKey("SaturationConfig")) {
                uniforms.put("SaturationConfig", List.of(new UniformValue.FloatUniform(this.saturation)));
            }

            if (uniforms.containsKey("ColorScaleConfig")) {
                uniforms.put("ColorScaleConfig", List.of(
                        new UniformValue.Vec3Uniform(new Vector3f(this.colorScale1, this.colorScale2, this.colorScale3))
                ));
            }
            if (uniforms.containsKey("BitsConfig")) {

                uniforms.put("BitsConfig", List.of(
                        new UniformValue.FloatUniform(this.resolution),
                        new UniformValue.FloatUniform(this.mosaicSize)
                ));
            } else if (uniforms.containsKey("InvertConfig")) {
                uniforms.put("InvertConfig", List.of(new UniformValue.FloatUniform(this.inverseAmount)));
            } else if (uniforms.containsKey("BlurConfig")) {

                List<UniformValue> existing = uniforms.get("BlurConfig");
                UniformValue blurDir = existing.get(0);
                uniforms.put("BlurConfig", List.of(blurDir, new UniformValue.FloatUniform(this.radius + 0.5f)));
            }
            newPasses.add(new PostChainConfig.Pass(
                    pass.vertexShaderId(), pass.fragmentShaderId(), pass.inputs(), pass.outputTarget(), uniforms));
        }
        return new PostChainConfig(baseConfig.internalTargets(), newPasses);
    }

    public void release() {

        if (this.colorShader != null) {
            this.colorShader.close();
        }
        this.colorShader = null;

        this.loadFailed = false;
    }

    public void onResize(int width, int height) {

        this.release();
    }

    @Override
    public void onEndTick(Minecraft client) {
        if (!client.isPaused()) {
            ticks++;
        }
    }

    public void renderShaderEffects(float tickDelta) {
        PostChain shader = getOrCreateShader();
        if (shader == null) {
            return;
        }

        shader.process(this.client.gameRenderer.mainRenderTarget(), GraphicsResourceAllocator.UNPOOLED);
    }

    public void onWorldRendered(Camera camera, float tickDelta) {
        renderShaderEffects(tickDelta);
    }

    public void setToDefault(){
        GameOptionsContainer options = (GameOptionsContainer) this.client.options;
        options.getRedMatrix1().set(1.0);
        options.getRedMatrix2().set(0.0);
        options.getRedMatrix3().set(0.0);
        options.getGreenMatrix1().set(0.0);
        options.getGreenMatrix2().set(1.0);
        options.getGreenMatrix3().set(0.0);
        options.getBlueMatrix1().set(0.0);
        options.getBlueMatrix2().set(0.0);
        options.getBlueMatrix3().set(1.0);
        options.getColorScale1().set(1.0);
        options.getColorScale2().set(1.0);
        options.getColorScale3().set(1.0);
        options.getSaturation().set(1.0);
        options.getResolution().set(100);
        options.getMosaicSize().set(1);
        options.getInverseAmount().set(0.0);
        options.getRadius().set(0.0);
    }
}
