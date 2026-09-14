package lv.almer.coloristic.client.screen;

import lv.almer.coloristic.Coloristic;
import lv.almer.coloristic.client.ColoristicConfig;
import lv.almer.coloristic.client.ColoristicFx;
import lv.almer.coloristic.client.GameOptionsContainer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.io.File;
import java.nio.file.Path;

@Environment(EnvType.CLIENT)
public class ColorSettingsScreen extends Screen {
    private static final Component TITLE_TEXT = Component.translatable("options.colorTitle");

    private static final Component MENU_TITLE_TEXT = Component.translatable("option.color_menu_title");

    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);

    private static final Identifier BACK_ICON = Identifier.fromNamespaceAndPath(Coloristic.MOD_ID, "textures/gui/sprites/icon/icon_arrow_left.png");

    private final Screen backTarget;
    public ColorSettingsScreen() {
        this(null);
    }
    public ColorSettingsScreen(Screen backTarget) {
        super(TITLE_TEXT);
        this.backTarget = backTarget;
    }

    public static Screen resolveScreen(Minecraft minecraft, Screen backTarget) {
        return minecraft.level != null ? new ColorSettingsScreen(backTarget) : new NoWorldScreen(backTarget);
    }
    public static Screen resolveScreen(Minecraft minecraft) {
        return resolveScreen(minecraft, null);
    }

    public static void openOrPromptForWorld(Minecraft minecraft, Screen backTarget) {
        minecraft.setScreenAndShow(resolveScreen(minecraft, backTarget));
    }

    public static void openOrPromptForWorld(Minecraft minecraft) {
        openOrPromptForWorld(minecraft, null);
    }
    @Override
    protected void init() {

        this.layout.removeChildren();
        GameOptionsContainer options = (GameOptionsContainer) this.minecraft.options;
        GridLayout gridWidget = new GridLayout();
        gridWidget.defaultCellSetting().paddingLeft(6).paddingTop(6).paddingRight(6);
        GridLayout.RowHelper adder = gridWidget.createRowHelper(3);
        adder.addChild(options.getRedMatrix1().createButton(this.minecraft.options, 0, 0, 90), 1, gridWidget.newCellSettings().alignHorizontallyLeft());
        adder.addChild(options.getGreenMatrix1().createButton(this.minecraft.options, 0, 0, 90), 1, gridWidget.newCellSettings().alignHorizontallyLeft());
        adder.addChild(options.getBlueMatrix1().createButton(this.minecraft.options, 0, 0, 90), 1, gridWidget.newCellSettings().alignHorizontallyLeft());
        adder.addChild(options.getRedMatrix2().createButton(this.minecraft.options, 0, 0, 90), 1, gridWidget.newCellSettings().alignHorizontallyLeft());
        adder.addChild(options.getGreenMatrix2().createButton(this.minecraft.options, 0, 0, 90), 1, gridWidget.newCellSettings().alignHorizontallyLeft());
        adder.addChild(options.getBlueMatrix2().createButton(this.minecraft.options, 0, 0, 90), 1, gridWidget.newCellSettings().alignHorizontallyLeft());
        adder.addChild(options.getRedMatrix3().createButton(this.minecraft.options, 0, 0, 90), 1, gridWidget.newCellSettings().alignHorizontallyLeft());
        adder.addChild(options.getGreenMatrix3().createButton(this.minecraft.options, 0, 0, 90), 1, gridWidget.newCellSettings().alignHorizontallyLeft());
        adder.addChild(options.getBlueMatrix3().createButton(this.minecraft.options, 0, 0, 90), 1, gridWidget.newCellSettings().alignHorizontallyLeft());

        adder.addChild(options.getColorScale1().createButton(this.minecraft.options, 0, 0, 90), 1, gridWidget.newCellSettings().paddingTop(60));
        adder.addChild(options.getSaturation().createButton(this.minecraft.options, 0, 0, 90), 1, gridWidget.newCellSettings().paddingTop(60));
        adder.addChild(options.getResolution().createButton(this.minecraft.options, 0, 0, 90), 1, gridWidget.newCellSettings().paddingTop(60));
        adder.addChild(options.getColorScale2().createButton(this.minecraft.options, 0, 0, 90), 1, gridWidget.newCellSettings());
        adder.addChild(options.getMosaicSize().createButton(this.minecraft.options, 0, 0, 90), 1, gridWidget.newCellSettings());
        adder.addChild(options.getInverseAmount().createButton(this.minecraft.options, 0, 0, 90), 1, gridWidget.newCellSettings());
        adder.addChild(options.getColorScale3().createButton(this.minecraft.options, 0, 0, 90), 1, gridWidget.newCellSettings());
        adder.addChild(options.getRadius().createButton(this.minecraft.options, 0, 0, 90), 1, gridWidget.newCellSettings());
        adder.addChild(Button.builder(Component.translatable("option.setDefault"), (button) -> {

            ColoristicFx.INSTANCE.setToDefault();
            this.rebuildWidgets();
        }).width(90).build(), 1, gridWidget.newCellSettings());

        GridLayout gridWidget1 = new GridLayout();
        gridWidget1.defaultCellSetting().paddingLeft(6).paddingTop(6).paddingRight(6).paddingBottom(6);
        GridLayout.RowHelper adder1 = gridWidget1.createRowHelper(1);

        AbstractWidget buttonVisibilityWidget = options.getButtonVisibility().createButton(this.minecraft.options, 0, 0, 100);
        buttonVisibilityWidget.setTooltip(Tooltip.create(Component.translatable("option.button_visibility.tooltip")));
        adder1.addChild(buttonVisibilityWidget, 1, gridWidget1.newCellSettings().alignVerticallyTop().alignHorizontallyRight().paddingBottom(6));
        adder1.addChild(Button.builder(Component.translatable("option.preset_title"), (button) -> {
            Path path = Path.of("./coloristic_presets/").toAbsolutePath().normalize();
            new File("coloristic_presets").mkdir();

            this.minecraft.setScreenAndShow(new PresetScreen(path));
        }).width(100).build(), 1, gridWidget1.newCellSettings().alignVerticallyTop().alignHorizontallyRight().paddingBottom(6));
        adder1.addChild(Button.builder(CommonComponents.GUI_DONE, (button) -> {

            this.onClose();
        }).width(100).build(), 1, gridWidget1.newCellSettings().alignVerticallyBottom().alignHorizontallyRight());
        gridWidget.arrangeElements();

        FrameLayout.alignInRectangle(gridWidget, 0, 0, this.width, this.height, 1.0F, 0.0F);

        gridWidget.visitWidgets((element) -> { AbstractWidget var10000 = this.addRenderableWidget(element); });
        gridWidget1.arrangeElements();
        FrameLayout.alignInRectangle(gridWidget1, 0, 0, this.width, this.height, 1.0F, 1.0F);
        gridWidget1.visitWidgets((element) -> { AbstractWidget var10000 = this.addRenderableWidget(element); });

        LinearLayout menuHeaderLayout = this.layout.addToHeader(LinearLayout.vertical().spacing(5));
        menuHeaderLayout.defaultCellSetting().alignHorizontallyCenter();
        menuHeaderLayout.addChild(new StringWidget(MENU_TITLE_TEXT, this.font));
        this.layout.arrangeElements();
        this.layout.visitWidgets((element) -> { AbstractWidget var10000 = this.addRenderableWidget(element); });

        this.addRenderableWidget(IconButton.create(10, 8, 20, 20, BACK_ICON,
                Component.translatable("option.back_button.tooltip"), (button) -> this.onClose()));
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
        if (this.getFocused() instanceof AbstractSliderButton || this.getFocused() instanceof AbstractButton) {
            this.setFocused(null);
        }
        return handled;
    }

    @Override
    public void onClose() {

        ColoristicConfig.saveFrom(ColoristicFx.INSTANCE);

        this.minecraft.setScreenAndShow(this.backTarget);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        if (this.minecraft.level == null) {
            this.extractPanorama(context, delta);
        }
    }
}
