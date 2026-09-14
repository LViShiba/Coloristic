package lv.almer.coloristic.mixin;

import com.mojang.serialization.Codec;
import lv.almer.coloristic.client.ColoristicConfig;
import lv.almer.coloristic.client.ColoristicFx;
import lv.almer.coloristic.client.GameOptionsContainer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.OptionInstance;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.util.Locale;

@Mixin(Options.class)
@Environment(EnvType.CLIENT)
public class GameOptionsMixin implements GameOptionsContainer {
    @Final
    private OptionInstance<Double> redMatrix1;
    @Final
    private OptionInstance<Double> redMatrix2;
    @Final
    private OptionInstance<Double> redMatrix3;
    @Final
    private OptionInstance<Double> greenMatrix1;
    @Final
    private OptionInstance<Double> greenMatrix2;
    @Final
    private OptionInstance<Double> greenMatrix3;
    @Final
    private OptionInstance<Double> blueMatrix1;
    @Final
    private OptionInstance<Double> blueMatrix2;
    @Final
    private OptionInstance<Double> blueMatrix3;
    @Final
    private OptionInstance<Double> colorScale1;
    @Final
    private OptionInstance<Double> colorScale2;
    @Final
    private OptionInstance<Double> colorScale3;
    @Final
    private OptionInstance<Double> saturation;
    @Final
    private OptionInstance<Integer> resolution;
    @Final
    private OptionInstance<Integer> mosaicSize;
    @Final
    private OptionInstance<Double> inverseAmount;
    @Final
    private OptionInstance<Double> radius;
    @Final
    private OptionInstance<Boolean> buttonVisibility;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(Minecraft client, File optionsFile, CallbackInfo ci){

        ColoristicConfig.loadInto(ColoristicFx.INSTANCE);
        this.redMatrix1 = new OptionInstance<>("options.redMatrix1", OptionInstance.noTooltip(), (optionText, value) -> {
            return Options.genericValueLabel(optionText, Component.literal(String.format(Locale.ROOT, "%.2f", (Double)value)));
        }, OptionInstance.UnitDouble.INSTANCE, (double) ColoristicFx.INSTANCE.redMatrix1, (value) -> {
            ColoristicFx.INSTANCE.redMatrix1 = ((Double)value).floatValue();
            ColoristicFx.INSTANCE.release();
        });
        this.redMatrix2 = new OptionInstance<>("options.redMatrix2", OptionInstance.noTooltip(), (optionText, value) -> {
            return Options.genericValueLabel(optionText, Component.literal(String.format(Locale.ROOT, "%.2f", (Double)value)));
        }, OptionInstance.UnitDouble.INSTANCE, (double) ColoristicFx.INSTANCE.redMatrix2, (value) -> {
            ColoristicFx.INSTANCE.redMatrix2 = ((Double)value).floatValue();
            ColoristicFx.INSTANCE.release();
        });
        this.redMatrix3 = new OptionInstance<>("options.redMatrix3", OptionInstance.noTooltip(), (optionText, value) -> {
            return Options.genericValueLabel(optionText, Component.literal(String.format(Locale.ROOT, "%.2f", (Double)value)));
        }, OptionInstance.UnitDouble.INSTANCE, (double) ColoristicFx.INSTANCE.redMatrix3, (value) -> {
            ColoristicFx.INSTANCE.redMatrix3 = ((Double)value).floatValue();
            ColoristicFx.INSTANCE.release();
        });
        this.greenMatrix1 = new OptionInstance<>("options.greenMatrix1", OptionInstance.noTooltip(), (optionText, value) -> {
            return Options.genericValueLabel(optionText, Component.literal(String.format(Locale.ROOT, "%.2f", (Double)value)));
        }, OptionInstance.UnitDouble.INSTANCE, (double) ColoristicFx.INSTANCE.greenMatrix1, (value) -> {
            ColoristicFx.INSTANCE.greenMatrix1 = ((Double)value).floatValue();
            ColoristicFx.INSTANCE.release();
        });
        this.greenMatrix2 = new OptionInstance<>("options.greenMatrix2", OptionInstance.noTooltip(), (optionText, value) -> {
            return Options.genericValueLabel(optionText, Component.literal(String.format(Locale.ROOT, "%.2f", (Double)value)));
        }, OptionInstance.UnitDouble.INSTANCE, (double) ColoristicFx.INSTANCE.greenMatrix2, (value) -> {
            ColoristicFx.INSTANCE.greenMatrix2 = ((Double)value).floatValue();
            ColoristicFx.INSTANCE.release();
        });
        this.greenMatrix3 = new OptionInstance<>("options.greenMatrix3", OptionInstance.noTooltip(), (optionText, value) -> {
            return Options.genericValueLabel(optionText, Component.literal(String.format(Locale.ROOT, "%.2f", (Double)value)));
        }, OptionInstance.UnitDouble.INSTANCE, (double) ColoristicFx.INSTANCE.greenMatrix3, (value) -> {
            ColoristicFx.INSTANCE.greenMatrix3 = ((Double)value).floatValue();
            ColoristicFx.INSTANCE.release();
        });
        this.blueMatrix1 = new OptionInstance<>("options.blueMatrix1", OptionInstance.noTooltip(), (optionText, value) -> {
            return Options.genericValueLabel(optionText, Component.literal(String.format(Locale.ROOT, "%.2f", (Double)value)));
        }, OptionInstance.UnitDouble.INSTANCE, (double) ColoristicFx.INSTANCE.blueMatrix1, (value) -> {
            ColoristicFx.INSTANCE.blueMatrix1 = ((Double)value).floatValue();
            ColoristicFx.INSTANCE.release();
        });
        this.blueMatrix2 = new OptionInstance<>("options.blueMatrix2", OptionInstance.noTooltip(), (optionText, value) -> {
            return Options.genericValueLabel(optionText, Component.literal(String.format(Locale.ROOT, "%.2f", (Double)value)));
        }, OptionInstance.UnitDouble.INSTANCE, (double) ColoristicFx.INSTANCE.blueMatrix2, (value) -> {
            ColoristicFx.INSTANCE.blueMatrix2 = ((Double)value).floatValue();
            ColoristicFx.INSTANCE.release();
        });
        this.blueMatrix3 = new OptionInstance<>("options.blueMatrix3", OptionInstance.noTooltip(), (optionText, value) -> {
            return Options.genericValueLabel(optionText, Component.literal(String.format(Locale.ROOT, "%.2f", (Double)value)));
        }, OptionInstance.UnitDouble.INSTANCE, (double) ColoristicFx.INSTANCE.blueMatrix3, (value) -> {
            ColoristicFx.INSTANCE.blueMatrix3 = ((Double)value).floatValue();
            ColoristicFx.INSTANCE.release();
        });
        this.colorScale1 = new OptionInstance<>("options.colorScale1", OptionInstance.noTooltip(), (optionText, value) -> {
            return Options.genericValueLabel(optionText, Component.literal(String.format(Locale.ROOT, "%.2f", (Double)value)));
        }, (new OptionInstance.IntRange(0, 50)).xmap((sliderProgressValue) -> {
            return (double)sliderProgressValue / 10.0;
        }, (value) -> {
            return (int)(value * 10.0);
        }, true), Codec.doubleRange(0.0, 5.0), (double) ColoristicFx.INSTANCE.colorScale1, (value) -> {
            ColoristicFx.INSTANCE.colorScale1 = ((Double)value).floatValue();
            ColoristicFx.INSTANCE.release();
        });
        this.colorScale2 = new OptionInstance<>("options.colorScale2", OptionInstance.noTooltip(), (optionText, value) -> {
            return Options.genericValueLabel(optionText, Component.literal(String.format(Locale.ROOT, "%.2f", (Double)value)));
        }, (new OptionInstance.IntRange(0, 50)).xmap((sliderProgressValue) -> {
            return (double)sliderProgressValue / 10.0;
        }, (value) -> {
            return (int)(value * 10.0);
        }, true), Codec.doubleRange(0.0, 5.0), (double) ColoristicFx.INSTANCE.colorScale2, (value) -> {
            ColoristicFx.INSTANCE.colorScale2 = ((Double)value).floatValue();
            ColoristicFx.INSTANCE.release();
        });
        this.colorScale3 = new OptionInstance<>("options.colorScale3", OptionInstance.noTooltip(), (optionText, value) -> {
            return Options.genericValueLabel(optionText, Component.literal(String.format(Locale.ROOT, "%.2f", (Double)value)));
        }, (new OptionInstance.IntRange(0, 50)).xmap((sliderProgressValue) -> {
            return (double)sliderProgressValue / 10.0;
        }, (value) -> {
            return (int)(value * 10.0);
        }, true), Codec.doubleRange(0.0, 5.0), (double) ColoristicFx.INSTANCE.colorScale3, (value) -> {
            ColoristicFx.INSTANCE.colorScale3 = ((Double)value).floatValue();
            ColoristicFx.INSTANCE.release();
        });
        this.saturation = new OptionInstance<>("options.saturation", OptionInstance.noTooltip(), (optionText, value) -> {
            return Options.genericValueLabel(optionText, Component.literal(String.format(Locale.ROOT, "%.2f", (Double)value)));
        }, (new OptionInstance.IntRange(0, 30)).xmap((sliderProgressValue) -> {
            return (double)sliderProgressValue / 10.0;
        }, (value) -> {
            return (int)(value * 10.0);
        }, true), Codec.doubleRange(0.0, 3.0), (double) ColoristicFx.INSTANCE.saturation, (value) -> {
            ColoristicFx.INSTANCE.saturation = ((Double)value).floatValue();
            ColoristicFx.INSTANCE.release();
        });
        this.resolution = new OptionInstance<>("options.resolution", OptionInstance.noTooltip(), (optionText, value) -> {
            return Options.genericValueLabel(optionText, Component.literal(((Integer)value).toString()));
        }, new OptionInstance.IntRange(1, 100), Codec.intRange(1, 100), (int) ColoristicFx.INSTANCE.resolution, (value) -> {
            ColoristicFx.INSTANCE.resolution = ((Integer)value).floatValue();
            ColoristicFx.INSTANCE.release();
        });

        this.mosaicSize = new OptionInstance<>("options.mosaicSize", OptionInstance.noTooltip(), (optionText, value) -> {
            return Options.genericValueLabel(optionText, Component.literal(((Integer)value).toString()));
        }, new OptionInstance.IntRange(1, 10), Codec.intRange(1, 10), (int) ColoristicFx.INSTANCE.mosaicSize, (value) -> {
            ColoristicFx.INSTANCE.mosaicSize = ((Integer)value).floatValue();
            ColoristicFx.INSTANCE.release();
        });
        this.inverseAmount = new OptionInstance<>("options.inverseAmount", OptionInstance.noTooltip(), (optionText, value) -> {
            return Options.genericValueLabel(optionText, Component.literal(String.format(Locale.ROOT, "%.0f", ((Double)value * 100)) + "%"));
        }, OptionInstance.UnitDouble.INSTANCE, (double) ColoristicFx.INSTANCE.inverseAmount, (value) -> {
            ColoristicFx.INSTANCE.inverseAmount = ((Double)value).floatValue();
            ColoristicFx.INSTANCE.release();
        });
        this.radius = new OptionInstance<>("options.radius", OptionInstance.noTooltip(), (optionText, value) -> {
            return Options.genericValueLabel(optionText, Component.literal(String.format(Locale.ROOT, "%.2f", (Double)value)));
        }, (new OptionInstance.IntRange(0, 100)).xmap((sliderProgressValue) -> {
            return (double)sliderProgressValue / 10.0;
        }, (value) -> {
            return (int)(value * 10.0);
        }, true), Codec.doubleRange(0.0, 10.0), (double) ColoristicFx.INSTANCE.radius, (value) -> {
            ColoristicFx.INSTANCE.radius = ((Double)value).floatValue();
            ColoristicFx.INSTANCE.release();
        });

        this.buttonVisibility = OptionInstance.createBoolean("option.button_visibility", ColoristicFx.INSTANCE.showQuickButton, (value) -> {
            ColoristicFx.INSTANCE.showQuickButton = (Boolean) value;
        });
    }
    @Override
    public OptionInstance<Double> getRedMatrix1() {
        return this.redMatrix1;
    }
    @Override
    public OptionInstance<Double> getRedMatrix2() {
        return this.redMatrix2;
    }
    @Override
    public OptionInstance<Double> getRedMatrix3() {
        return this.redMatrix3;
    }
    @Override
    public OptionInstance<Double> getGreenMatrix1() {
        return this.greenMatrix1;
    }
    @Override
    public OptionInstance<Double> getGreenMatrix2() {
        return this.greenMatrix2;
    }
    @Override
    public OptionInstance<Double> getGreenMatrix3() {return this.greenMatrix3;}
    @Override
    public OptionInstance<Double> getBlueMatrix1() {
        return this.blueMatrix1;
    }
    @Override
    public OptionInstance<Double> getBlueMatrix2() {
        return this.blueMatrix2;
    }
    @Override
    public OptionInstance<Double> getBlueMatrix3() {
        return this.blueMatrix3;
    }
    @Override
    public OptionInstance<Double> getColorScale1() {
        return this.colorScale1;
    }
    @Override
    public OptionInstance<Double> getColorScale2() {
        return this.colorScale2;
    }
    @Override
    public OptionInstance<Double> getColorScale3() {
        return this.colorScale3;
    }
    @Override
    public OptionInstance<Double> getSaturation() {
        return this.saturation;
    }
    @Override
    public OptionInstance<Integer> getResolution() {
        return this.resolution;
    }
    @Override
    public OptionInstance<Integer> getMosaicSize() {
        return this.mosaicSize;
    }
    @Override
    public OptionInstance<Double> getInverseAmount() {
        return this.inverseAmount;
    }
    @Override
    public OptionInstance<Double> getRadius() {
        return this.radius;
    }
    @Override
    public OptionInstance<Boolean> getButtonVisibility() {
        return this.buttonVisibility;
    }
}
