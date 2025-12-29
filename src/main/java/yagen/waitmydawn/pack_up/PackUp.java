package yagen.waitmydawn.pack_up;

import com.mojang.logging.LogUtils;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

@Mod(PackUp.MODID)
public class PackUp {
    public static final String MODID = "pack_up";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);
    public static final RegistryObject<MenuType<LootStorageMenu>> LOOT_MENU = MENU_TYPES.register("loot_menu",
            () -> IForgeMenuType.create(LootStorageMenu::new));

    public static final Capability<PlayerLootData> LOOT_DATA_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<BlockLootFlag> CAN_QUICK_LOOT_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    @SuppressWarnings({"removal"})
    public PackUp() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        MENU_TYPES.register(modEventBus);

        NetworkHandler.register();

        MinecraftForge.EVENT_BUS.register(this);
    }
}