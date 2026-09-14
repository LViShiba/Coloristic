package lv.almer.coloristic.mixin;

import lv.almer.coloristic.client.ColoristicFx;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.joml.Matrix4fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
@Environment(EnvType.CLIENT)
public class GameRendererMixin {
    @Inject(method = "renderItemInHand", at = @At("TAIL"))
    private void coloristic$afterRenderItemInHand(CameraRenderState cameraRenderState, float deltaPartialTick, Matrix4fc modelViewMatrix, CallbackInfo ci) {
        ColoristicFx.INSTANCE.renderShaderEffects(deltaPartialTick);
    }
}
