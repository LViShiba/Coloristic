package lv.almer.coloristic.client.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class NoWorldScreen extends Screen {
    private static final Component TITLE_TEXT = Component.translatable("options.colorTitle");
    private static final Component MESSAGE_TEXT = Component.translatable("screen.noWorld.message");
    private final Screen backTarget;

    private boolean cursorCentered;

    public NoWorldScreen() {
        this(null);
    }

    public NoWorldScreen(Screen backTarget) {
        super(TITLE_TEXT);
        this.backTarget = backTarget;
    }

    @Override
    protected void init() {
        if (!this.cursorCentered) {
            this.cursorCentered = true;
            double centerX = this.minecraft.getWindow().getScreenWidth() / 2.0;
            double centerY = this.minecraft.getWindow().getScreenHeight() / 2.0;
            GLFW.glfwSetCursorPos(this.minecraft.getWindow().handle(), centerX, centerY);
        }

        LinearLayout content = LinearLayout.vertical().spacing(12);
        content.defaultCellSetting().alignHorizontallyCenter();
        content.addChild(new StringWidget(MESSAGE_TEXT, this.font));
        content.addChild(Button.builder(CommonComponents.GUI_DONE, (button) -> this.onClose()).width(150).build());
        content.arrangeElements();
        FrameLayout.centerInRectangle(content, 0, 0, this.width, this.height);

        content.visitWidgets((element) -> {
            AbstractWidget var10000 = this.addRenderableWidget(element);
        });
    }

    @Override
    public void onClose() {
        this.minecraft.setScreenAndShow(this.backTarget != null ? this.backTarget : new TitleScreen());
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        this.extractPanorama(context, delta);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        boolean handled = super.mouseClicked(event, doubleClick);
        if (!handled) {
            this.setFocused(null);
        }
        return handled;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        boolean handled = super.mouseReleased(event);
        if (this.getFocused() instanceof AbstractButton) {
            this.setFocused(null);
        }
        return handled;
    }
}
