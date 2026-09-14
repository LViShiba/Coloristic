package lv.almer.coloristic.mixin;

import lv.almer.coloristic.Coloristic;
import lv.almer.coloristic.client.ColoristicFx;
import lv.almer.coloristic.client.screen.ColorSettingsScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Mixin(PauseScreen.class)
@Environment(EnvType.CLIENT)
public class GameMenuScreenMixin extends Screen {
    protected GameMenuScreenMixin(Component title) {
        super(title);
    }

    private static final int BUTTON_SIZE = 20;
    private static final int SPRITE_SIZE = 15;

    @Inject(method = "init()V", at = @At("TAIL"))
    private void coloristic$addColorSettingsButton(CallbackInfo ci) {

        if (!ColoristicFx.INSTANCE.showQuickButton) {
            return;
        }
        List<AbstractWidget> icons = new ArrayList<>();
        for (Object child : this.children()) {
            if (!(child instanceof AbstractWidget widget)) {
                continue;
            }

            if (widget.getWidth() != widget.getHeight() || widget.getWidth() <= 0 || widget.getWidth() > 20) {
                continue;
            }
            icons.add(widget);
        }
        icons.sort(Comparator.comparingInt(AbstractWidget::getX));

        int x;
        int y;
        if (!icons.isEmpty()) {
            AbstractWidget first = icons.get(0);
            AbstractWidget last = icons.get(icons.size() - 1);
            int groupCenterX = (first.getX() + last.getX() + last.getWidth()) / 2;
            y = first.getY();

            int gap = 4;
            if (icons.size() >= 2) {
                AbstractWidget a = icons.get(0);
                AbstractWidget b = icons.get(1);
                gap = b.getX() - (a.getX() + a.getWidth());
            }

            int totalWidth = BUTTON_SIZE;
            for (AbstractWidget icon : icons) {
                totalWidth += icon.getWidth() + gap;
            }
            int cursor = groupCenterX - totalWidth / 2;
            for (AbstractWidget icon : icons) {
                icon.setX(cursor);
                cursor += icon.getWidth() + gap;
            }

            x = cursor;
        } else {

            x = this.width / 2 - BUTTON_SIZE / 2;
            y = this.height / 4 + 108;
        }

        SpriteIconButton colorButton = SpriteIconButton.builder(Component.translatable("options.colorTitle"), (button) -> {

            ColorSettingsScreen.openOrPromptForWorld(this.minecraft, (Screen) (Object) this);
        }, true)
                .size(BUTTON_SIZE, BUTTON_SIZE)
                .sprite(Identifier.fromNamespaceAndPath(Coloristic.MOD_ID, "icon/color"), SPRITE_SIZE, SPRITE_SIZE)
                .build();
        colorButton.setX(x);
        colorButton.setY(y);

        colorButton.setTooltip(Tooltip.create(Component.literal("Coloristic")));
        this.addRenderableWidget(colorButton);
    }
}
