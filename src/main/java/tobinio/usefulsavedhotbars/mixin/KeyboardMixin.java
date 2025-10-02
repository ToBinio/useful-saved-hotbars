package tobinio.usefulsavedhotbars.mixin;

import net.minecraft.client.Keyboard;
import net.minecraft.client.input.KeyInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tobinio.usefulsavedhotbars.client.UsefulSavedHotbarsClient;
import tobinio.usefulsavedhotbars.client.SavedHotbarScreen;

import static tobinio.usefulsavedhotbars.client.UsefulSavedHotbarsClient.setSavedHotbarScreen;

@Mixin (Keyboard.class)
public abstract class KeyboardMixin {

    @Inject (method = "processF3", at = @At (value = "TAIL"), cancellable = true)
    private void processF3(KeyInput keyInput, CallbackInfoReturnable<Boolean> cir) {
        KeyboardAccessor keyboardAccessor = (KeyboardAccessor) this;

        if (UsefulSavedHotbarsClient.LoadHotbarsKeyBindingF3.matchesKey(keyInput)) {
            setSavedHotbarScreen(keyboardAccessor, SavedHotbarScreen.Type.LOAD_F3);
            cir.setReturnValue(true);
        }

        if (UsefulSavedHotbarsClient.SaveHotbarsKeyBindingF3.matchesKey(keyInput)) {
            setSavedHotbarScreen(keyboardAccessor, SavedHotbarScreen.Type.SAVE_F3);
            cir.setReturnValue(true);
        }
    }
}
