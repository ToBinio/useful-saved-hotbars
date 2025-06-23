package tobinio.usefulsavedhotbars.mixin;

import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin (Keyboard.class)
public interface KeyboardAccessor {
    @Accessor
    MinecraftClient getClient();

    @Invoker ("debugLog")
    void invokeDebugLog(String key);
}
