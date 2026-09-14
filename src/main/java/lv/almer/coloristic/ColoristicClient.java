package lv.almer.coloristic;

import lv.almer.coloristic.client.ColoristicEventHandler;
import lv.almer.coloristic.client.ColoristicKeybinds;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ColoristicClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ColoristicEventHandler.registerClientEvents();

        ColoristicKeybinds.register();
    }
}
