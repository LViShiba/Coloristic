package lv.almer.coloristic.client;

import lv.almer.coloristic.client.ColoristicConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

@Environment(EnvType.CLIENT)
public class ColoristicEventHandler {
    @Environment(EnvType.CLIENT)
    public static void registerClientEvents() {
        ClientTickEvents.END_CLIENT_TICK.register(ColoristicFx.INSTANCE);
        ClientEntityEvents.ENTITY_LOAD.register(ColoristicEventHandler::onClientLoad);

        ClientLifecycleEvents.CLIENT_STOPPING.register((minecraft) -> ColoristicConfig.saveFrom(ColoristicFx.INSTANCE));
    }

    @Environment(EnvType.CLIENT)
    private static void onClientLoad(Entity entity, Level world) {
        ColoristicFx.INSTANCE.release();
    }
}
