package tobinio.usefulsavedhotbars.client.hotbarWidget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.HotbarStorageEntry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import static tobinio.usefulsavedhotbars.UsefulSavedHotbars.id;

@Environment (EnvType.CLIENT)
public class LoadHotbarWidget extends HotbarWidget {

    public static final Identifier BASE_TEXTURE = id("textures/gui/container/base.png");
    public static final Identifier BOARDER_TEXTURE = id("textures/gui/container/selected.png");


    public LoadHotbarWidget(int x, int y, int hotbarIndex, HotbarStorageEntry hotbar, MinecraftClient client) {
        super(x, y, hotbarIndex, hotbar, client);

    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawTexture(RenderLayer::getGuiTexturedOverlay,
                BASE_TEXTURE,
                this.getX(),
                this.getY(),
                0,
                0,
                this.getWidth(),
                this.getHeight(),
                this.getWidth(),
                this.getHeight());

        for (int i = 0; i < PlayerInventory.getHotbarSize(); i++) {
            ItemStack itemStack = this.items.get(i);

            context.drawItem(itemStack, this.getX() + i * 16 + 2, this.getY() + 2);
        }

        if (this.selected) {
            context.drawTexture(RenderLayer::getGuiTexturedOverlay,
                    BOARDER_TEXTURE,
                    this.getX(),
                    this.getY(),
                    0,
                    0,
                    this.getWidth(),
                    this.getHeight(),
                    this.getWidth(),
                    this.getHeight());
        }
    }

    @Override
    public void apply() {
        for (int i = 0; i < this.items.size(); i++) {
            ItemStack itemStack = this.items.get(i);

            ItemStack itemStack2 = itemStack.isItemEnabled(this.client.world.getEnabledFeatures()) ? itemStack.copy() : ItemStack.EMPTY;
            this.client.player.getInventory().setStack(i, itemStack2);
            client.interactionManager.clickCreativeStack(itemStack2, 36 + i);
        }

        this.client.player.playerScreenHandler.sendContentUpdates();
    }
}
