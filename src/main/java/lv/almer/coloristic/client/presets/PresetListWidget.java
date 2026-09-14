package lv.almer.coloristic.client.presets;

import com.google.common.hash.Hashing;
import lv.almer.coloristic.Coloristic;
import lv.almer.coloristic.client.GameOptionsContainer;
import lv.almer.coloristic.client.screen.PresetScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import com.mojang.blaze3d.platform.NativeImage;

@Environment(EnvType.CLIENT)
public class PresetListWidget extends ObjectSelectionList<PresetListWidget.PresetEntry> {
    private final Component title;
    final PresetScreen screen;

    public static final int HEADER_HEIGHT = 13;

    private static final int ITEM_HEIGHT = 36;
    public PresetListWidget(Minecraft client, PresetScreen screen, int width, int height, Component title) {
        super(client, width, height, 0, ITEM_HEIGHT);
        this.screen = screen;
        this.title = title;
        this.centerListVertically = false;
        Objects.requireNonNull(client.font);

    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float partialTick) {
        super.extractWidgetRenderState(context, mouseX, mouseY, partialTick);
        this.drawHeaderLabel(context);
    }
    private void drawHeaderLabel(GuiGraphicsExtractor context) {
        Component text = Component.empty().append(this.title).withStyle(ChatFormatting.UNDERLINE, ChatFormatting.BOLD);
        int textX = this.getX() + this.width / 2 - this.minecraft.font.width(text) / 2;
        int textY = this.getY() - HEADER_HEIGHT + 3;
        context.text(this.minecraft.font, text, textX, textY, -1, false);
    }
    public int getRowWidth() {
        return this.width;
    }
    protected int getScrollbarPosition() {
        return this.getRight() - 6;
    }

    @Override
    protected void extractSelection(GuiGraphicsExtractor context, PresetEntry entry, int outlineColor) {
        if (this.scrollable()) {

            int j = this.getRowLeft() - 2;
            int k = this.getRight() - 6 - 1;
            int l = entry.getY() - 2;
            int m = entry.getY() + entry.getHeight() + 2;
            context.fill(j, l, k, m, outlineColor);
        } else {
            super.extractSelection(context, entry, outlineColor);
        }
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (this.getSelected() != null) {
            switch (event.key()) {
                case 32:
                case 257:
                    (this.getSelected()).toggle();
                    return true;
                default:
                    if (event.hasShiftDown()) {
                        switch (event.key()) {
                            case 264:
                                (this.getSelected()).moveTowardEnd();
                                return true;
                            case 265:
                                (this.getSelected()).moveTowardStart();
                                return true;
                        }
                    }
            }
        }
        return super.keyPressed(event);
    }
    @Environment(EnvType.CLIENT)
    public static class PresetEntry extends ObjectSelectionList.Entry<PresetEntry> {
        private final PresetListWidget widget;
        protected final Minecraft client;
        private final FormattedCharSequence displayName;

        @Nullable
        private final FormattedCharSequence displayDescription;
        private final Preset preset;
        public PresetEntry(Minecraft client, PresetListWidget widget, Preset preset) {
            this.client = client;
            this.preset = preset;
            this.widget = widget;
            this.displayName = trimTextToWidth(client, Component.nullToEmpty(preset.getName()));
            String description = preset.getDescription();
            this.displayDescription = (description == null || description.isBlank()) ? null
                    : trimTextToWidth(client, Component.nullToEmpty(description));

            this.setWidth(widget.getRowWidth());
            this.setHeight(ITEM_HEIGHT);
        }
        private static FormattedCharSequence trimTextToWidth(Minecraft client, Component text) {
            int i = client.font.width(text);
            if (i > 157) {
                FormattedText formattedText = FormattedText.composite(client.font.substrByWidth(text, 157 - client.font.width("...")), FormattedText.of("..."));
                return Language.getInstance().getVisualOrder(formattedText);
            } else {
                return text.getVisualOrderText();
            }
        }
        public Component getNarration() {
            return Component.translatable("narrator.select", this.displayName);
        }

        @Override
        public void extractContent(GuiGraphicsExtractor context, int mouseX, int mouseY, boolean hovered, float partialTick) {

            int x = this.getContentX();
            int y = this.getContentY();
            String[] nameArr = this.preset.getName().toLowerCase().split(" ");
            String name = "";
            for(int i = 0; i < nameArr.length; i++){
                name += nameArr[i];
                if(i < nameArr.length - 1){
                    name += "_";
                }
            }

            try(InputStream inputStream = Files.newInputStream(Path.of("./coloristic_presets/", name, name + ".png").toAbsolutePath().normalize())){
                NativeImage image = NativeImage.read(inputStream);

                final String iconLabel = name;
                DynamicTexture texture = new DynamicTexture(() -> "coloristic_preset_icon_" + iconLabel, image);
                String safeName = Util.sanitizeName(name, Identifier::validPathChar);
                Identifier identifier = Identifier.withDefaultNamespace("pack/" + safeName + "/" + Hashing.sha1().hashUnencodedChars(name) + "/icon");
                this.client.getTextureManager().register(identifier, texture);

                context.blit(RenderPipelines.GUI_TEXTURED, identifier, x, y, 0.0F, 0.0F, 32, 32, 32, 32);
            }catch(IOException var){
                context.blit(RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath(Coloristic.MOD_ID, "textures/gui/sprites/icon/default_icon.png"), x, y, 0.0F, 0.0F, 32, 32, 32, 32);
            }
            FormattedCharSequence orderedText = this.displayName;

            if (this.isSelectable() && (hovered || this.widget.getSelected() == this && this.widget.isFocused())) {
                context.fill(x, y, x + 32, y + 32, -1601138544);
            }

            context.text(this.client.font, orderedText, x + 32 + 2, y + 1, -1);

            if (this.displayDescription != null) {
                context.text(this.client.font, this.displayDescription, x + 32 + 2, y + 1 + this.client.font.lineHeight, 0xFFAAAAAA);
            }
        }
        public String getName() {
            return this.preset.getName();
        }

        public Preset getPreset() {
            return this.preset;
        }
        private boolean isSelectable() {
            return true;
        }
        public void toggle() {
            if (this.enable()) {
                this.widget.screen.switchFocusedList(this.widget);
            }
        }
        void moveTowardStart() {
        }
        void moveTowardEnd() {
        }
        private boolean enable() {
            GameOptionsContainer options = (GameOptionsContainer) this.client.options;
            options.getRedMatrix1().set(this.preset.getOptions().get(0));
            options.getRedMatrix2().set(this.preset.getOptions().get(1));
            options.getRedMatrix3().set(this.preset.getOptions().get(2));
            options.getGreenMatrix1().set(this.preset.getOptions().get(3));
            options.getGreenMatrix2().set(this.preset.getOptions().get(4));
            options.getGreenMatrix3().set(this.preset.getOptions().get(5));
            options.getBlueMatrix1().set(this.preset.getOptions().get(6));
            options.getBlueMatrix2().set(this.preset.getOptions().get(7));
            options.getBlueMatrix3().set(this.preset.getOptions().get(8));
            options.getColorScale1().set(this.preset.getOptions().get(9));
            options.getColorScale2().set(this.preset.getOptions().get(10));
            options.getColorScale3().set(this.preset.getOptions().get(11));
            options.getSaturation().set(this.preset.getOptions().get(12));
            options.getResolution().set(this.preset.getOptions().get(13).intValue());
            options.getMosaicSize().set(this.preset.getOptions().get(14).intValue());
            options.getInverseAmount().set(this.preset.getOptions().get(15));
            options.getRadius().set(this.preset.getOptions().get(16));
            return true;
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            double d = event.x() - (double)this.widget.getRowLeft();
            double e = event.y() - (double)this.widget.getRowTop(this.widget.children().indexOf(this));
            if (this.isSelectable() && d <= 32.0) {
            }
            return super.mouseClicked(event, doubleClick);
        }
    }
}
