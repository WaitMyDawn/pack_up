package yagen.waitmydawn.pack_up;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@SuppressWarnings({"removal"})
@Mod.EventBusSubscriber(modid = PackUp.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventHandler {

    @SubscribeEvent
    public static void onAttachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            if (!event.getObject().getCapability(PackUp.LOOT_DATA_CAPABILITY).isPresent()) {
                event.addCapability(new ResourceLocation(PackUp.MODID, "loot_data"), new PlayerLootData());
            }
        }
    }

    @SubscribeEvent
    public static void onAttachBlockEntityCapabilities(AttachCapabilitiesEvent<BlockEntity> event) {
        if (event.getObject() instanceof RandomizableContainerBlockEntity) {
            if (!event.getObject().getCapability(PackUp.CAN_QUICK_LOOT_CAPABILITY).isPresent()) {
                event.addCapability(new ResourceLocation(PackUp.MODID, "can_quick_loot"), new BlockLootFlag());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        var oldPlayer = event.getOriginal();
        oldPlayer.reviveCaps();

        oldPlayer.getCapability(PackUp.LOOT_DATA_CAPABILITY).ifPresent(oldData -> {
            event.getEntity().getCapability(PackUp.LOOT_DATA_CAPABILITY).ifPresent(newData -> {
                if (event.isWasDeath()) {
                    newData.copyFrom(oldData);
                }
            });
        });

        oldPlayer.invalidateCaps();
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide) return;

        BlockEntity blockEntity = event.getLevel().getBlockEntity(event.getPos());

        if (blockEntity instanceof RandomizableContainerBlockEntity) {
            CompoundTag nbt = blockEntity.saveWithoutMetadata();
            if (nbt.contains("LootTable") && !nbt.contains("LootrOpeners")) {
                blockEntity.getCapability(PackUp.CAN_QUICK_LOOT_CAPABILITY).ifPresent(cap -> {
                    cap.setCanQuickLoot(true);
                });
            }
        }
    }
}