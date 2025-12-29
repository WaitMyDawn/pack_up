package yagen.waitmydawn.pack_up;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = PackUp.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientInput {

    public static final Lazy<KeyMapping> KEY_P = Lazy.of(() -> new KeyMapping(
            "key.pack_up.open_menu",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_P,
            "key.pack_up.group"
    ));

    public static final Lazy<KeyMapping> KEY_X = Lazy.of(() -> new KeyMapping(
            "key.pack_up.pack_up",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_X,
            "key.pack_up.group"
    ));

    @SubscribeEvent
    public static void onRegisterKeys(RegisterKeyMappingsEvent event) {
        event.register(KEY_P.get());
        event.register(KEY_X.get());
    }

    @SubscribeEvent
    public static void onRegisterScreens(RegisterMenuScreensEvent event) {
        event.register(PackUp.LOOT_MENU.get(), LootStorageScreen::new);
    }
}

@EventBusSubscriber(modid = PackUp.MODID, value = Dist.CLIENT)
class ClientEvents {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (ClientInput.KEY_P.get().consumeClick()) {
            PacketDistributor.sendToServer(new PacketOpenStorage());
        }
    }

    @SubscribeEvent
    public static void onScreenKeyPressed(ScreenEvent.KeyPressed.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen instanceof AbstractContainerScreen<?>) {
            if (event.getKeyCode() == ClientInput.KEY_X.get().getKey().getValue()) {
                PacketDistributor.sendToServer(new PacketStoreLoot());
                event.setCanceled(true);
            }
        }
    }
}
