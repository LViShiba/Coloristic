package lv.almer.coloristic.client.presets;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public class Preset {
    public final String name;

    public final String description;
    public final List<Double> options;
    public static final Codec<Preset> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(Preset::getName),
            Codec.STRING.optionalFieldOf("description", "").forGetter(Preset::getDescription),
            Codec.DOUBLE.listOf().fieldOf("options").forGetter(Preset::getOptions)
    ).apply(instance, Preset::new));
    public Preset(String name, String description, List<Double> options){
        this.name = name;
        this.description = description == null ? "" : description;
        this.options = options;
    }
    public List<Double> getOptions(){
        return this.options;
    }
    public String getName(){
        return this.name;
    }
    public String getDescription(){
        return this.description;
    }
}
