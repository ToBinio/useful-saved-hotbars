package tobinio.usefulsavedhotbars.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.HotbarStorage;
import net.minecraft.client.option.HotbarStorageEntry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import tobinio.usefulsavedhotbars.client.hotbarWidget.HotbarWidget;
import tobinio.usefulsavedhotbars.client.hotbarWidget.LoadHotbarWidget;
import tobinio.usefulsavedhotbars.client.hotbarWidget.SaveHotbarWidget;

import java.util.ArrayList;
import java.util.List;

import static tobinio.usefulsavedhotbars.UsefulSavedHotbars.id;

@Environment (EnvType.CLIENT)
public class SavedHotbarScreen extends Screen {

    public static final Identifier BG_TEXTURE = id("textures/gui/container/background.png");

    public static final int BG_WIDTH = 162;
    public static final int BG_HEIGHT = 199;

    private final MinecraftClient client;
    private final Type type;
    private final List<HotbarWidget> hotbarWidgets;

    private int selected = 0;

    private int lastMouseX;
    private int lastMouseY;
    private boolean mouseUsedForSelection;

    public SavedHotbarScreen(MinecraftClient client, Type type) {
        super(NarratorManager.EMPTY);

        this.client = client;
        this.type = type;

        this.hotbarWidgets = new ArrayList<>();
    }

    @Override
    protected void init() {
        super.init();

        HotbarStorage hotbars = client.getCreativeHotbarStorage();

        int x = (this.width / 2) - HotbarWidget.WIDTH / 2;
        int y = (this.height / 2) - BG_HEIGHT / 2 + 24;

        for (int i = 0; i < HotbarStorage.STORAGE_ENTRY_COUNT; i++) {
            HotbarStorageEntry hotbar = hotbars.getSavedHotbar(i);

            switch (this.type) {
                case LOAD_F3, LOAD -> this.hotbarWidgets.add(new LoadHotbarWidget(x,
                        y + i * (HotbarWidget.HEIGHT - 1),
                        i,
                        hotbar,
                        client));
                case SAVE_F3, SAVE -> this.hotbarWidgets.add(new SaveHotbarWidget(x,
                        y + i * (HotbarWidget.HEIGHT - 1),
                        i,
                        hotbar,
                        client));
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {

        if (this.type == Type.LOAD_F3 || this.type == Type.SAVE_F3)
            this.checkForCloseF3();

        if (!this.mouseUsedForSelection) {
            this.lastMouseX = mouseX;
            this.lastMouseY = mouseY;
            this.mouseUsedForSelection = true;
        }

        boolean mouseHasNotMoved = this.lastMouseX == mouseX && this.lastMouseY == mouseY;

        super.render(context, mouseX, mouseY, delta);

        context.drawTexture(RenderLayer::getGuiTexturedOverlay,
                BG_TEXTURE,
                this.width / 2 - BG_WIDTH / 2,
                this.height / 2 - BG_HEIGHT / 2,
                0,
                0,
                BG_WIDTH,
                BG_HEIGHT,
                BG_WIDTH,
                BG_HEIGHT);

        String text = switch (this.type) {
            case LOAD_F3, LOAD -> "Load Hotbar " + (this.selected + 1);
            case SAVE_F3, SAVE -> "Save Hotbar " + (this.selected + 1);
        };

        context.drawCenteredTextWithShadow(this.textRenderer, text, this.width / 2, this.height / 2 - 92, -1);

        for (HotbarWidget hotbarWidget : this.hotbarWidgets) {
            hotbarWidget.render(context, mouseX, mouseY, delta);

            hotbarWidget.setSelected(hotbarWidget.getHotbarIndex() == selected);
            if (!mouseHasNotMoved && hotbarWidget.isSelected()) {
                this.selected = hotbarWidget.getHotbarIndex();
            }
        }
    }

    private void checkForCloseF3() {
        if (!InputUtil.isKeyPressed(this.client.getWindow().getHandle(), GLFW.GLFW_KEY_F3)) {
            apply();
        }
    }

    private boolean checkForClose(int keyCode, int scanCode) {
        switch (this.type) {
            case LOAD -> {
                if (UsefulSavedHotbarsClient.LoadHotbarsKeyBinding.matchesKey(keyCode, scanCode)) {
                    apply();
                    return true;
                }
            }
            case SAVE -> {
                if (UsefulSavedHotbarsClient.SaveHotbarsKeyBinding.matchesKey(keyCode, scanCode)) {
                    apply();
                    return true;
                }
            }
        }

        return false;
    }

    private void apply() {
        this.hotbarWidgets.get(selected).apply();
        this.close();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseUsedForSelection) {
            apply();
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (toggleKeyPressed(keyCode, scanCode)) {
            this.selected++;
            this.selected %= this.hotbarWidgets.size();
            this.mouseUsedForSelection = false;

            return true;
        }

        if (checkForClose(keyCode, scanCode)) {
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private boolean toggleKeyPressed(int keyCode, int scanCode) {
        switch (this.type) {
            case LOAD_F3 -> {
                return UsefulSavedHotbarsClient.LoadHotbarsKeyBindingF3.matchesKey(keyCode, scanCode);
            }
            case SAVE_F3 -> {
                return UsefulSavedHotbarsClient.SaveHotbarsKeyBindingF3.matchesKey(keyCode, scanCode);
            }
            case LOAD -> {
                return UsefulSavedHotbarsClient.SaveHotbarsKeyBinding.matchesKey(keyCode, scanCode);
            }
            case SAVE -> {
                return UsefulSavedHotbarsClient.LoadHotbarsKeyBinding.matchesKey(keyCode, scanCode);
            }
            default -> {
                return false;
            }
        }
    }

    public enum Type {
        LOAD_F3, SAVE_F3, LOAD, SAVE
    }
}
