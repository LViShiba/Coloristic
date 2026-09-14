package lv.almer.coloristic.client.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class IconButton extends Button {

    private static final Identifier BUTTON_SPRITE = Identifier.withDefaultNamespace("widget/button");
    private static final Identifier BUTTON_HIGHLIGHTED_SPRITE = Identifier.withDefaultNamespace("widget/button_highlighted");
    private static final Identifier BUTTON_DISABLED_SPRITE = Identifier.withDefaultNamespace("widget/button_disabled");

    private final Identifier icon;

    protected IconButton(int x, int y, int width, int height, Identifier icon, OnPress onPress) {
        super(x, y, width, height, Component.empty(), onPress, DEFAULT_NARRATION);
        this.icon = icon;
    }

    public static IconButton create(int x, int y, int width, int height, Identifier icon, OnPress onPress) {
        return new IconButton(x, y, width, height, icon, onPress);
    }

    public static IconButton create(int x, int y, int width, int height, Identifier icon, Component tooltipText, OnPress onPress) {
        IconButton button = new IconButton(x, y, width, height, icon, onPress);
        button.setTooltip(Tooltip.create(tooltipText));
        return button;
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        Identifier backgroundSprite;
        if (!this.active) {
            backgroundSprite = BUTTON_DISABLED_SPRITE;
        } else if (this.isHoveredOrFocused()) {
            backgroundSprite = BUTTON_HIGHLIGHTED_SPRITE;
        } else {
            backgroundSprite = BUTTON_SPRITE;
        }

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, backgroundSprite,
                this.getX(), this.getY(), this.getWidth(), this.getHeight());

        int iconSize = Math.min(this.getWidth(), this.getHeight()) - 2;
        int iconX = this.getX() + (this.getWidth() - iconSize) / 2;
        int iconY = this.getY() + (this.getHeight() - iconSize) / 2;

        graphics.blit(RenderPipelines.GUI_TEXTURED, icon, iconX, iconY, 0, 0, iconSize, iconSize, iconSize, iconSize);
    }
}
