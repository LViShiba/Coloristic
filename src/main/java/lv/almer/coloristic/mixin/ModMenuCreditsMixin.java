package lv.almer.coloristic.mixin;

import com.terraformersmc.modmenu.util.mod.Mod;
import com.terraformersmc.modmenu.util.mod.fabric.FabricMod;
import lv.almer.coloristic.Coloristic;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.ModMetadata;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mixin(FabricMod.class)
@Environment(EnvType.CLIENT)
public class ModMenuCreditsMixin {

    @Inject(method = "getContributors", at = @At("RETURN"))
    private void coloristic$addCustomCredits(CallbackInfoReturnable<Map<String, Collection<String>>> cir) {

        Mod self = (Mod) (Object) this;
        if (!Coloristic.MOD_ID.equals(self.getId())) {

            return;
        }

        Optional<ModContainer> ownContainer = FabricLoader.getInstance().getModContainer(Coloristic.MOD_ID);
        if (ownContainer.isEmpty()) {
            return;
        }
        ModMetadata ownMetadata = ownContainer.get().getMetadata();
        if (!ownMetadata.containsCustomValue("credits")) {
            return;
        }
        CustomValue creditsValue = ownMetadata.getCustomValue("credits");
        if (creditsValue.getType() != CustomValue.CvType.OBJECT) {
            return;
        }

        Map<String, Collection<String>> contributors = cir.getReturnValue();
        for (Map.Entry<String, CustomValue> roleEntry : creditsValue.getAsObject()) {
            String role = roleEntry.getKey();
            CustomValue namesValue = roleEntry.getValue();
            List<String> names = new ArrayList<>();
            if (namesValue.getType() == CustomValue.CvType.STRING) {
                names.add(namesValue.getAsString());
            } else if (namesValue.getType() == CustomValue.CvType.ARRAY) {
                for (CustomValue nameValue : namesValue.getAsArray()) {
                    if (nameValue.getType() == CustomValue.CvType.STRING) {
                        names.add(nameValue.getAsString());
                    }
                }
            }
            for (String name : names) {
                contributors.put(name, List.of(role));
            }
        }
    }
}
