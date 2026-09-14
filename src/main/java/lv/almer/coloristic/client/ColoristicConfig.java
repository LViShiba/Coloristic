package lv.almer.coloristic.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ColoristicConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("coloristic.json");

    public double redMatrix1 = 1.0;
    public double redMatrix2 = 0.0;
    public double redMatrix3 = 0.0;
    public double greenMatrix1 = 0.0;
    public double greenMatrix2 = 1.0;
    public double greenMatrix3 = 0.0;
    public double blueMatrix1 = 0.0;
    public double blueMatrix2 = 0.0;
    public double blueMatrix3 = 1.0;
    public double colorScale1 = 1.0;
    public double colorScale2 = 1.0;
    public double colorScale3 = 1.0;
    public double saturation = 1.0;
    public int resolution = 100;
    public int mosaicSize = 1;
    public double inverseAmount = 0.0;
    public double radius = 0.0;
    public boolean showQuickButton = true;

    public static void loadInto(ColoristicFx fx) {
        ColoristicConfig config = read();
        if (config == null) {
            return;
        }
        fx.redMatrix1 = (float) config.redMatrix1;
        fx.redMatrix2 = (float) config.redMatrix2;
        fx.redMatrix3 = (float) config.redMatrix3;
        fx.greenMatrix1 = (float) config.greenMatrix1;
        fx.greenMatrix2 = (float) config.greenMatrix2;
        fx.greenMatrix3 = (float) config.greenMatrix3;
        fx.blueMatrix1 = (float) config.blueMatrix1;
        fx.blueMatrix2 = (float) config.blueMatrix2;
        fx.blueMatrix3 = (float) config.blueMatrix3;
        fx.colorScale1 = (float) config.colorScale1;
        fx.colorScale2 = (float) config.colorScale2;
        fx.colorScale3 = (float) config.colorScale3;
        fx.saturation = (float) config.saturation;
        fx.resolution = (float) config.resolution;
        fx.mosaicSize = (float) config.mosaicSize;
        fx.inverseAmount = (float) config.inverseAmount;
        fx.radius = (float) config.radius;
        fx.showQuickButton = config.showQuickButton;
    }

    public static void saveFrom(ColoristicFx fx) {
        ColoristicConfig config = new ColoristicConfig();
        config.redMatrix1 = fx.redMatrix1;
        config.redMatrix2 = fx.redMatrix2;
        config.redMatrix3 = fx.redMatrix3;
        config.greenMatrix1 = fx.greenMatrix1;
        config.greenMatrix2 = fx.greenMatrix2;
        config.greenMatrix3 = fx.greenMatrix3;
        config.blueMatrix1 = fx.blueMatrix1;
        config.blueMatrix2 = fx.blueMatrix2;
        config.blueMatrix3 = fx.blueMatrix3;
        config.colorScale1 = fx.colorScale1;
        config.colorScale2 = fx.colorScale2;
        config.colorScale3 = fx.colorScale3;
        config.saturation = fx.saturation;
        config.resolution = (int) fx.resolution;
        config.mosaicSize = (int) fx.mosaicSize;
        config.inverseAmount = fx.inverseAmount;
        config.radius = fx.radius;
        config.showQuickButton = fx.showQuickButton;
        write(config);
    }

    private static ColoristicConfig read() {
        try {
            if (!Files.exists(CONFIG_PATH)) {
                return null;
            }
            String json = Files.readString(CONFIG_PATH, StandardCharsets.UTF_8);
            return GSON.fromJson(json, ColoristicConfig.class);
        } catch (IOException | RuntimeException e) {

            return null;
        }
    }

    private static void write(ColoristicConfig config) {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, GSON.toJson(config), StandardCharsets.UTF_8);
        } catch (IOException e) {

        }
    }
}
