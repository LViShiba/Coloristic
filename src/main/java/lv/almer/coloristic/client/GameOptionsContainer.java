package lv.almer.coloristic.client;

import net.minecraft.client.OptionInstance;

public interface GameOptionsContainer {
    OptionInstance<Double> getRedMatrix1();
    OptionInstance<Double> getRedMatrix2();
    OptionInstance<Double> getRedMatrix3();
    OptionInstance<Double> getGreenMatrix1();
    OptionInstance<Double> getGreenMatrix2();
    OptionInstance<Double> getGreenMatrix3();
    OptionInstance<Double> getBlueMatrix1();
    OptionInstance<Double> getBlueMatrix2();
    OptionInstance<Double> getBlueMatrix3();
    OptionInstance<Double> getColorScale1();
    OptionInstance<Double> getColorScale2();
    OptionInstance<Double> getColorScale3();
    OptionInstance<Double> getSaturation();
    OptionInstance<Integer> getResolution();
    OptionInstance<Integer> getMosaicSize();
    OptionInstance<Double> getInverseAmount();
    OptionInstance<Double> getRadius();
    OptionInstance<Boolean> getButtonVisibility();
}
