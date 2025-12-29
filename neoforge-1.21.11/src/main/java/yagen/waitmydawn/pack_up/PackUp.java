package yagen.waitmydawn.pack_up;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.slf4j.Logger;

import java.util.function.Supplier;

@Mod(PackUp.MODID)
public class PackUp {
    public static final String MODID = "pack_up";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MODID);
    public static final Supplier<AttachmentType<PlayerLootData>> LOOT_DATA = ATTACHMENT_TYPES.register("loot_data",
            () -> AttachmentType.builder(() -> new PlayerLootData())
                    .serialize(PlayerLootData.MAP_CODEC).copyOnDeath()
                    .build());

    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, MODID);
    public static final Supplier<MenuType<LootStorageMenu>> LOOT_MENU = MENU_TYPES.register("loot_menu",
            () -> IMenuTypeExtension.create(LootStorageMenu::new));

    public static final Supplier<AttachmentType<Boolean>> CAN_QUICK_LOOT = ATTACHMENT_TYPES.register("can_quick_loot",
            () -> AttachmentType.builder(() -> false)
                    .serialize(Codec.BOOL.fieldOf("value"))
                    .build());



    public PackUp(IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
        MENU_TYPES.register(modEventBus);

        NeoForge.EVENT_BUS.addListener(EventHandler::onRightClickBlock);

        modEventBus.addListener(NetworkHandler::register);
    }
}
