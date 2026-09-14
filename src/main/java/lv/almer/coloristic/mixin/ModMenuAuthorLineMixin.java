package lv.almer.coloristic.mixin;

import com.terraformersmc.modmenu.gui.ModsScreen;
import com.terraformersmc.modmenu.util.mod.Mod;
import lv.almer.coloristic.Coloristic;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;

@Mixin(ModsScreen.class)
@Environment(EnvType.CLIENT)
public class ModMenuAuthorLineMixin {

    @Redirect(
            method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/terraformersmc/modmenu/util/mod/Mod;getAuthors()Ljava/util/List;"
            )
    )
    private List<String> coloristic$appendDisplayOnlyAuthor(Mod mod) {
        List<String> authors = mod.getAuthors();
        if (!Coloristic.MOD_ID.equals(mod.getId())) {

            return authors;
        }

        List<String> displayAuthors = new ArrayList<>(authors);
        displayAuthors.add("LViShiba");
        return displayAuthors;
    }
}
