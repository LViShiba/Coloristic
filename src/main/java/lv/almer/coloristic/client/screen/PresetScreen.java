package lv.almer.coloristic.client.screen;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.JsonOps;
import lv.almer.coloristic.Coloristic;
import lv.almer.coloristic.client.ColoristicFx;
import lv.almer.coloristic.client.presets.Preset;
import lv.almer.coloristic.client.presets.PresetListWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

@Environment(EnvType.CLIENT)
public class PresetScreen extends Screen {
    private static final Component TITLE_TEXT = Component.translatable("option.preset_title");
    private static final Component AVAILABLE_TITLE = Component.translatable("pack.available.title");
    private static final Component OPEN_FOLDER = Component.translatable("pack.openFolder");
    private static final Component FOLDER_INFO;

    private static final Component CLICK_CREATE_PROMPT =
            Component.translatable("option.click_create_prompt", Component.translatable("option.create_preset"));

    private static final Component NAME_PLACEHOLDER = Component.translatable("text.namePlaceholder");
    private static final Component DESCRIPTION_PLACEHOLDER = Component.translatable("text.descriptionPlaceholder");

    private static final int PLACEHOLDER_COLOR = 0xFF2C3337;

    private static final Identifier DEFAULT_ICON =
            Identifier.fromNamespaceAndPath(Coloristic.MOD_ID, "textures/gui/sprites/icon/default_icon.png");

    private static final Identifier BACK_ICON =
            Identifier.fromNamespaceAndPath(Coloristic.MOD_ID, "textures/gui/sprites/icon/icon_arrow_left.png");

    private long notAllowedCursor;

    private boolean notAllowedCursorActive;

    private int hideForScreenshotFrames;
    private Preset pendingScreenshotPreset;

    private String editingOriginalName;

    private String iconResetPresetName;

    private final Path file;
    private Button doneButton;
    private EditBox enterNameField;
    private EditBox enterDescriptionField;
    private Button confirmButton;
    private Button createButton;
    private Button undoButton;
    private PresetListWidget availablePackList;
    public Button useButton;

    private Button editButton;
    private Button deleteButton;

    private Button deleteIconButton;

    private Button openPresetFolderButton;
    @Nullable
    private DirectoryWatcher directoryWatcher;
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    private long refreshTimeout;
    private final Map<String, Identifier> iconTextures = Maps.newHashMap();

    private Set<String> lastPresetSignatures;
    protected PresetScreen(Path file) {
        super(TITLE_TEXT);
        this.file = file;
        this.directoryWatcher = DirectoryWatcher.create(file);
    }
    private void closeDirectoryWatcher() {
        if (this.directoryWatcher != null) {
            try {
                this.directoryWatcher.close();
                this.directoryWatcher = null;
            } catch (Exception var2) {
            }
        }
    }
    @Override
    protected void init() {

        this.layout.removeChildren();

        this.lastPresetSignatures = null;
        LinearLayout directionalLayoutWidget = this.layout.addToHeader(LinearLayout.vertical().spacing(5));
        directionalLayoutWidget.defaultCellSetting().alignHorizontallyCenter();
        this.availablePackList = this.addRenderableWidget(new PresetListWidget(this.minecraft, this, 200, this.height - 66, AVAILABLE_TITLE));
        directionalLayoutWidget.addChild(new StringWidget(this.getTitle(), this.font));

        this.addRenderableWidget(IconButton.create(10, 8, 20, 20, BACK_ICON,
                Component.translatable("option.back_button.tooltip"), (button) -> this.onClose()));
        LinearLayout directionalLayoutWidget2 = this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
        LinearLayout directionalLayoutWidget1 = this.layout.addToContents(LinearLayout.horizontal().spacing(8));
        directionalLayoutWidget1.defaultCellSetting().padding(200, -57, 0, 0);
        this.useButton = directionalLayoutWidget1.addChild(Button.builder(Component.translatable("option.use_preset"), (button) -> {
            this.availablePackList.getSelected().toggle();
        }).width(200).build());
        directionalLayoutWidget2.addChild(Button.builder(OPEN_FOLDER, (button) -> {
            Util.getPlatform().openPath(this.file);
        }).tooltip(Tooltip.create(FOLDER_INFO)).build());

        this.doneButton = directionalLayoutWidget2.addChild(Button.builder(CommonComponents.GUI_DONE, (button) -> {
            this.minecraft.setScreenAndShow(new ColorSettingsScreen());
        }).build());
        LinearLayout directionalLayoutWidget3 = this.layout.addToContents(LinearLayout.vertical().spacing(6));
        directionalLayoutWidget3.defaultCellSetting().padding(200, 0, 0, 0);
        this.enterNameField = directionalLayoutWidget3.addChild(new PlaceholderEditBox(this.minecraft.font, 200, 20, Component.translatable("text.defaultName").append("1")));

        this.enterDescriptionField = this.addRenderableWidget(new PlaceholderEditBox(this.minecraft.font, 200, 20, Component.translatable("text.defaultDescription")));
        this.enterDescriptionField.setMaxLength(128);
        this.confirmButton = directionalLayoutWidget3.addChild(Button.builder(Component.translatable("option.confirm"), (button) -> {

            List<Double> values = List.of((double)ColoristicFx.INSTANCE.redMatrix1, (double)ColoristicFx.INSTANCE.redMatrix2, (double)ColoristicFx.INSTANCE.redMatrix3,
                    (double)ColoristicFx.INSTANCE.greenMatrix1, (double)ColoristicFx.INSTANCE.greenMatrix2, (double)ColoristicFx.INSTANCE.greenMatrix3,
                    (double)ColoristicFx.INSTANCE.blueMatrix1, (double)ColoristicFx.INSTANCE.blueMatrix2, (double)ColoristicFx.INSTANCE.blueMatrix3,
                    (double)ColoristicFx.INSTANCE.colorScale1, (double)ColoristicFx.INSTANCE.colorScale2, (double)ColoristicFx.INSTANCE.colorScale3,
                    (double)ColoristicFx.INSTANCE.saturation, (double)ColoristicFx.INSTANCE.resolution, (double)ColoristicFx.INSTANCE.mosaicSize,
                    (double)ColoristicFx.INSTANCE.inverseAmount, (double)ColoristicFx.INSTANCE.radius);
            Preset preset = new Preset(this.enterNameField.getValue(), this.enterDescriptionField.getValue(), values);
            this.enterNameField.active = false;
            this.enterDescriptionField.active = false;

            this.enterNameField.setValue("");
            this.enterDescriptionField.setValue("");
            this.confirmButton.active = false;
            this.undoButton.active = false;
            this.createButton.active = true;

            if (this.editingOriginalName != null
                    && !sanitizePresetFileName(this.editingOriginalName).equals(sanitizePresetFileName(preset.getName()))) {
                deletePresetFiles(this.editingOriginalName);
            }
            this.editingOriginalName = null;

            if (this.iconResetPresetName != null
                    && this.iconResetPresetName.equals(sanitizePresetFileName(preset.getName()))) {
                this.iconResetPresetName = null;
                writePreset(preset, false);
                this.updatePackLists();
                return;
            }

            this.beginCleanScreenshotThenWritePreset(preset);
        }).width(174).build(), directionalLayoutWidget3.newCellSettings().padding(226, 0, 0, 0));
        this.undoButton = directionalLayoutWidget3.addChild(Button.builder(Component.literal("X"), (button) ->{
            this.cancelEditing();
        }).width(20).build(), directionalLayoutWidget3.newCellSettings().padding(200, -26, 0, 0));
        this.enterNameField.active = false;
        this.enterDescriptionField.active = false;
        this.confirmButton.active = false;
        this.undoButton.active = false;
        this.createButton = directionalLayoutWidget2.addChild(Button.builder(Component.translatable("option.create_preset"), (button) ->{
            this.enterNameField.active = true;
            this.enterDescriptionField.active = true;
            this.confirmButton.active = true;
            this.undoButton.active = true;
            this.createButton.active = false;

            this.editingOriginalName = null;

            this.availablePackList.setSelected(null);
        }).width(100).build());

        this.editButton = this.addRenderableWidget(Button.builder(Component.translatable("option.edit_preset"), (button) -> {
            PresetListWidget.PresetEntry selected = this.availablePackList.getSelected();
            if (selected == null) return;
            Preset preset = selected.getPreset();

            this.editingOriginalName = preset.getName();
            this.enterNameField.setValue(preset.getName());
            this.enterDescriptionField.setValue(preset.getDescription());
            this.enterNameField.active = true;
            this.enterDescriptionField.active = true;
            this.confirmButton.active = true;
            this.undoButton.active = true;
            this.createButton.active = false;
        }).width(200).build());
        this.deleteButton = this.addRenderableWidget(Button.builder(Component.translatable("option.delete_preset"), (button) -> {
            this.deleteSelectedPreset();
        }).width(200).build());
        this.deleteIconButton = this.addRenderableWidget(Button.builder(Component.translatable("option.delete_icon"), (button) -> {
            this.deleteSelectedPresetIcon();
        }).width(200).build());
        this.openPresetFolderButton = this.addRenderableWidget(Button.builder(Component.translatable("option.open_preset_folder"), (button) -> {
            this.openSelectedPresetFolder();
        }).width(200).build());
        this.editButton.active = false;
        this.deleteButton.active = false;
        this.deleteIconButton.active = false;
        this.openPresetFolderButton.active = false;
        this.refresh();
        this.layout.visitWidgets((element) -> {
            AbstractWidget var10000 = this.addRenderableWidget(element);
        });
        this.initTabNavigation();
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

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        if (this.hideForScreenshotFrames > 0) {

            this.hideForScreenshotFrames--;
            if (this.hideForScreenshotFrames == 0) {
                Preset preset = this.pendingScreenshotPreset;
                this.pendingScreenshotPreset = null;
                writePreset(preset, true);
                this.updatePackLists();
            }
            return;
        }
        super.extractRenderState(graphics, mouseX, mouseY, delta);

        this.drawPlaceholderIfEmpty(graphics, this.enterNameField,
                this.enterNameField.active ? NAME_PLACEHOLDER.getString() : CLICK_CREATE_PROMPT.getString());
        this.drawPlaceholderIfEmpty(graphics, this.enterDescriptionField,
                this.enterDescriptionField.active ? DESCRIPTION_PLACEHOLDER.getString() : CLICK_CREATE_PROMPT.getString());
        this.updateCreateFieldsCursor(mouseX, mouseY);
    }

    private void beginCleanScreenshotThenWritePreset(Preset preset) {
        this.pendingScreenshotPreset = preset;
        this.hideForScreenshotFrames = 2;
    }

    private void updateCreateFieldsCursor(int mouseX, int mouseY) {
        boolean overDisabledField =
                (!this.enterNameField.active && rawBounds(this.enterNameField, mouseX, mouseY)) ||
                (!this.enterDescriptionField.active && rawBounds(this.enterDescriptionField, mouseX, mouseY));
        long window = this.minecraft.getWindow().handle();
        if (overDisabledField) {
            if (this.notAllowedCursor == 0L) {
                this.notAllowedCursor = GLFW.glfwCreateStandardCursor(GLFW.GLFW_NOT_ALLOWED_CURSOR);
            }
            GLFW.glfwSetCursor(window, this.notAllowedCursor);
            this.notAllowedCursorActive = true;
        } else if (this.notAllowedCursorActive) {

            GLFW.glfwSetCursor(window, 0L);
            this.notAllowedCursorActive = false;
        }
    }

    @Override
    public void onClose() {
        this.minecraft.setScreenAndShow(new ColorSettingsScreen());
    }

    @Override
    public void removed() {
        super.removed();
        if (this.notAllowedCursorActive) {
            GLFW.glfwSetCursor(this.minecraft.getWindow().handle(), 0L);
            this.notAllowedCursorActive = false;
        }
        if (this.notAllowedCursor != 0L) {
            GLFW.glfwDestroyCursor(this.notAllowedCursor);
            this.notAllowedCursor = 0L;
        }
    }

    private void drawPlaceholderIfEmpty(GuiGraphicsExtractor graphics, EditBox field, String placeholder) {
        if (field == null || !field.getValue().isEmpty() || field.isFocused()) return;
        int padding = 4;
        int textY = field.getY() + (field.getHeight() - this.font.lineHeight) / 2 + 1;
        graphics.text(this.font, placeholder, field.getX() + padding, textY, PLACEHOLDER_COLOR, false);
    }

    private static boolean rawBounds(AbstractWidget widget, int mouseX, int mouseY) {
        return mouseX >= widget.getX() && mouseX < widget.getX() + widget.getWidth()
                && mouseY >= widget.getY() && mouseY < widget.getY() + widget.getHeight();
    }

    private static class PlaceholderEditBox extends EditBox {
        PlaceholderEditBox(Font font, int width, int height, Component message) {
            super(font, width, height, message);
        }

        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            return this.active && super.isMouseOver(mouseX, mouseY);
        }
    }

    protected void initTabNavigation() {
        this.layout.arrangeElements();

        int listX = this.width / 2 - 15 - 200;
        int listY = this.layout.getHeaderHeight() + PresetListWidget.HEADER_HEIGHT;
        int listHeight = (this.height - 66) - PresetListWidget.HEADER_HEIGHT;
        this.availablePackList.updateSizeAndPosition(200, listHeight, listX, listY);

        int useButtonNaturalY = this.useButton.getY();
        int nameFieldGapFromButton = this.enterNameField.getY() - (useButtonNaturalY + this.useButton.getHeight());
        this.useButton.setY(listY);
        this.enterNameField.setY(listY + this.useButton.getHeight() + nameFieldGapFromButton);

        this.enterDescriptionField.setPosition(this.enterNameField.getX(), this.enterNameField.getY() + this.enterNameField.getHeight() + nameFieldGapFromButton);

        int buttonRowY = this.enterDescriptionField.getY() + this.enterDescriptionField.getHeight() + 6;
        this.confirmButton.setY(buttonRowY);
        this.undoButton.setY(buttonRowY);

        this.editButton.setPosition(this.useButton.getX(), buttonRowY + this.confirmButton.getHeight() + nameFieldGapFromButton);
        this.deleteButton.setPosition(this.useButton.getX(), this.editButton.getY() + this.editButton.getHeight() + nameFieldGapFromButton);

        this.deleteIconButton.setPosition(this.useButton.getX(), this.deleteButton.getY() + this.deleteButton.getHeight() + nameFieldGapFromButton);

        this.openPresetFolderButton.setPosition(this.useButton.getX(), this.deleteIconButton.getY() + this.deleteIconButton.getHeight() + nameFieldGapFromButton);
    }

    private static void deletePresetFiles(String rawName) {
        String name = sanitizePresetFileName(rawName);
        Path dir = presetDirPath(rawName);
        Path jsonPath = dir.resolve(name + ".json");
        Path pngPath = dir.resolve(name + ".png");
        try {
            Files.deleteIfExists(jsonPath);
            Files.deleteIfExists(pngPath);

            Files.deleteIfExists(dir);
        } catch (IOException e) {
            Coloristic.LOGGER.warn("Couldn't delete preset {}", name, e);
        }
    }

    private void deleteSelectedPreset() {
        PresetListWidget.PresetEntry selected = this.availablePackList.getSelected();
        if (selected == null) return;
        deletePresetFiles(selected.getName());
        this.cancelEditing();
        this.updatePackLists();
    }

    private void cancelEditing() {
        this.enterNameField.active = false;
        this.enterDescriptionField.active = false;

        this.enterNameField.setValue("");
        this.enterDescriptionField.setValue("");
        this.confirmButton.active = false;
        this.undoButton.active = false;
        this.createButton.active = true;
        this.editingOriginalName = null;
    }

    private static void resetPresetIcon(String rawName) {
        String name = sanitizePresetFileName(rawName);
        Path dir = presetDirPath(rawName);
        Path pngPath = dir.resolve(name + ".png");
        try (InputStream in = Minecraft.getInstance().getResourceManager().getResourceOrThrow(DEFAULT_ICON).open()) {
            dir.toFile().mkdirs();
            Files.copy(in, pngPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            Coloristic.LOGGER.warn("Couldn't reset preset icon {}", name, e);
        }
    }

    private void deleteSelectedPresetIcon() {
        PresetListWidget.PresetEntry selected = this.availablePackList.getSelected();
        if (selected == null) return;
        resetPresetIcon(selected.getName());

        this.iconResetPresetName = sanitizePresetFileName(selected.getName());
    }

    private void openSelectedPresetFolder() {
        PresetListWidget.PresetEntry selected = this.availablePackList.getSelected();
        if (selected == null) return;
        Util.getPlatform().openPath(presetDirPath(selected.getName()));
    }
    private void updatePackLists() {
        String path = Path.of("./coloristic_presets/").toAbsolutePath().normalize().toString();
        new File("coloristic_presets").mkdir();

        try(Stream<Path> paths = Files.walk(Path.of(path), 2)){
            List<Preset> presets = new ArrayList<>();
            paths.forEach((file) ->{
                if(file.toString().endsWith(".json")) {
                    try (FileReader reader = new FileReader(file.toFile())) {
                        Preset preset = new Gson().fromJson(reader, Preset.class);
                        presets.addLast(preset);
                    } catch (FileNotFoundException var) {
                        Coloristic.LOGGER.warn("Can not read directory");
                    } catch (IOException var1) {
                        Coloristic.LOGGER.warn("Can not read directory");
                    }
                }
            });

            Set<String> signatures = new HashSet<>();
            for (Preset preset : presets) {
                signatures.add(preset.getName() + " " + preset.getDescription() + " " + preset.getOptions());
            }
            if (signatures.equals(this.lastPresetSignatures)) {
                return;
            }
            this.lastPresetSignatures = signatures;
            this.updatePackList(this.availablePackList, presets.stream());
        } catch(IOException var){
            Coloristic.LOGGER.warn("Can not read directory");
        }
    }

    private void updatePackList(PresetListWidget widget, Stream<Preset> presets) {
        PresetListWidget.PresetEntry presetsEntry = widget.getSelected();
        String string = presetsEntry == null ? "" : presetsEntry.getName();
        widget.setSelected(null);
        List<PresetListWidget.PresetEntry> entries = new ArrayList<>();
        presets.forEach((preset) -> {
            PresetListWidget.PresetEntry presetEntry = new PresetListWidget.PresetEntry(this.minecraft, widget, preset);
            entries.add(presetEntry);
            if (preset.getName().equals(string)) {
                widget.setSelected(presetEntry);
            }
        });
        widget.replaceEntries(entries);
    }
    public void switchFocusedList(PresetListWidget listWidget) {
        PresetListWidget packListWidget = this.availablePackList;

        PresetListWidget.PresetEntry selectedEntry = packListWidget.getSelected();
        List<PresetListWidget.PresetEntry> packEntries = packListWidget.children();
        PresetListWidget.PresetEntry targetEntry = selectedEntry != null ? selectedEntry
                : (packEntries.isEmpty() ? null : packEntries.get(0));
        this.changeFocus(ComponentPath.path(targetEntry, packListWidget, this));
    }
    public void tick() {
        if (this.directoryWatcher != null) {
            try {
                if (this.directoryWatcher.pollForChange()) {
                    this.refreshTimeout = 20L;
                }
            } catch (IOException var2) {
                Coloristic.LOGGER.warn("Failed to poll for directory {} changes, stopping", this.file);
                this.closeDirectoryWatcher();
            }
        }
        if (this.refreshTimeout > 0L && --this.refreshTimeout == 0L) {
            this.refresh();
        }
        if(this.availablePackList.getSelected() != null){
            this.useButton.active = true;
            this.editButton.active = true;
            this.deleteButton.active = true;
            this.deleteIconButton.active = true;
            this.openPresetFolderButton.active = true;
        }
        else{
            this.useButton.active = false;
            this.editButton.active = false;
            this.deleteButton.active = false;
            this.deleteIconButton.active = false;
            this.openPresetFolderButton.active = false;
        }
    }

    private static String sanitizePresetFileName(String rawName) {
        String[] nameArr = rawName.toLowerCase().split(" ");
        String name = "";
        for(int i = 0; i < nameArr.length; i++){
            name += nameArr[i];
            if(i < nameArr.length - 1){
                name += "_";
            }
        }
        return name;
    }

    private static Path presetDirPath(String rawName) {
        return Path.of("./coloristic_presets/", sanitizePresetFileName(rawName)).toAbsolutePath().normalize();
    }

    public static void writePreset(Preset preset, boolean writeIcon){
        String name = sanitizePresetFileName(preset.getName());
        Path dir = presetDirPath(preset.getName());
        String path = dir.resolve(name + ".json").toString();
        String path1 = dir.resolve(name + ".png").toString();
        dir.toFile().mkdirs();
        try (FileWriter writer = new FileWriter(path)){
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject obj = Preset.CODEC.encodeStart(JsonOps.INSTANCE, preset).getOrThrow().getAsJsonObject();
            gson.toJson(obj, gson.newJsonWriter(writer));

            if (!writeIcon) {
                return;
            }
            Minecraft client1 = Minecraft.getInstance();

            Screenshot.takeScreenshot(client1.gameRenderer.mainRenderTarget(), nativeImage -> {
                Util.ioPool().execute(() -> {
                    int i = nativeImage.getWidth();
                    int j = nativeImage.getHeight();
                    int k = 0;
                    int l = 0;
                    if (i > j) {
                        k = (i - j) / 2;
                        i = j;
                    } else {
                        l = (j - i) / 2;
                        j = i;
                    }
                    try {

                        NativeImage nativeImage2 = new NativeImage(32, 32, false);
                        try {
                            nativeImage.resizeSubRectTo(k, l, i, j, nativeImage2);
                            nativeImage2.writeToFile(Path.of(path1));
                        } catch (Throwable var15) {
                            try {
                                nativeImage2.close();
                            } catch (Throwable var14) {
                                var15.addSuppressed(var14);
                            }
                            throw var15;
                        }
                        nativeImage2.close();
                    } catch (IOException var16) {
                        Coloristic.LOGGER.warn("Couldn't save auto screenshot", var16);
                    } finally {
                        nativeImage.close();
                    }
                });
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void refresh() {
        this.updatePackLists();
        this.refreshTimeout = 0L;
        this.iconTextures.clear();
    }
    static {
        FOLDER_INFO = Component.translatable("pack.folderInfo");
    }
    @Environment(EnvType.CLIENT)
    private static class DirectoryWatcher implements AutoCloseable {
        private final WatchService watchService;
        private final Path path;

        public DirectoryWatcher(Path path) throws IOException {
            this.path = path;
            this.watchService = path.getFileSystem().newWatchService();

            try {
                this.watchDirectory(path);
                DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path);

                try {
                    Iterator var3 = directoryStream.iterator();

                    while(var3.hasNext()) {
                        Path path2 = (Path)var3.next();
                        if (Files.isDirectory(path2, new LinkOption[]{LinkOption.NOFOLLOW_LINKS})) {
                            this.watchDirectory(path2);
                        }
                    }
                } catch (Throwable var6) {
                    if (directoryStream != null) {
                        try {
                            directoryStream.close();
                        } catch (Throwable var5) {
                            var6.addSuppressed(var5);
                        }
                    }

                    throw var6;
                }

                if (directoryStream != null) {
                    directoryStream.close();
                }

            } catch (Exception var7) {
                this.watchService.close();
                throw var7;
            }
        }

        @Nullable
        public static PresetScreen.DirectoryWatcher create(Path path) {
            try {
                return new PresetScreen.DirectoryWatcher(path);
            } catch (IOException var2) {
                Coloristic.LOGGER.warn("Failed to initialize preset directory {} monitoring", path, var2);
                return null;
            }
        }

        private void watchDirectory(Path path) throws IOException {
            path.register(this.watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
        }

        public boolean pollForChange() throws IOException {
            boolean bl = false;

            WatchKey watchKey;
            while((watchKey = this.watchService.poll()) != null) {
                List<WatchEvent<?>> list = watchKey.pollEvents();
                Iterator var4 = list.iterator();

                while(var4.hasNext()) {
                    WatchEvent<?> watchEvent = (WatchEvent)var4.next();
                    bl = true;
                    if (watchKey.watchable() == this.path && watchEvent.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                        Path path = this.path.resolve((Path)watchEvent.context());
                        if (Files.isDirectory(path, new LinkOption[]{LinkOption.NOFOLLOW_LINKS})) {
                            this.watchDirectory(path);
                        }
                    }
                }

                watchKey.reset();
            }

            return bl;
        }

        public void close() throws IOException {
            this.watchService.close();
        }
    }
}
