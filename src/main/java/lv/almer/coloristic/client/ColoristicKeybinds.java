package lv.almer.coloristic.client;

import com.mojang.blaze3d.platform.InputConstants;
import lv.almer.coloristic.Coloristic;
import lv.almer.coloristic.client.screen.ColorSettingsScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class ColoristicKeybinds {
    private static final KeyMapping.Category CATEGORY =
            KeyMapping.Category.register(Identifier.fromNamespaceAndPath(Coloristic.MOD_ID, "main"));

    private static final KeyMapping OPEN_MENU = new KeyMapping(
            "key.coloristic.open_menu",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_CONTROL,
            CATEGORY
    );

    public static void register() {
        KeyMappingHelper.registerKeyMapping(OPEN_MENU);
        ClientTickEvents.END_CLIENT_TICK.register(ColoristicKeybinds::onClientTick);
    }

    private static void onClientTick(Minecraft client) {
        while (OPEN_MENU.consumeClick()) {
            if (client.gui.screen() == null) {
                ColorSettingsScreen.openOrPromptForWorld(client);
            }
        }
    }
}
