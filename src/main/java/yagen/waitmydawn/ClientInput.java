package yagen.waitmydawn;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = PackUp.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientInput {

    public static final KeyMapping KEY_P = new KeyMapping(
            "key.pack_up.open_menu",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_P,
            "key.pack_up.group"
    );

    public static final KeyMapping KEY_X = new KeyMapping(
            "key.pack_up.pack_up",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_X,
            "key.pack_up.group"
    );

    @SubscribeEvent
    public static void onRegisterKeys(RegisterKeyMappingsEvent event) {
        event.register(KEY_P);
        event.register(KEY_X);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        MenuScreens.register(PackUp.LOOT_MENU.get(), LootStorageScreen::new);
    }
}

@Mod.EventBusSubscriber(modid = PackUp.MODID, value = Dist.CLIENT)
class ClientEvents {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (ClientInput.KEY_P.consumeClick()) {
            NetworkHandler.sendToServer(new PacketOpenStorage());
        }
    }

    @SubscribeEvent
    public static void onScreenKeyPressed(ScreenEvent.KeyPressed.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen instanceof AbstractContainerScreen<?>) {
            if (event.getKeyCode() == ClientInput.KEY_X.getKey().getValue()) {
                NetworkHandler.sendToServer(new PacketStoreLoot());
                event.setCanceled(true);
            }
        }
    }
}