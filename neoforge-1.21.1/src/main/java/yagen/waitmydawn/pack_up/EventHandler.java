package yagen.waitmydawn.pack_up;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = PackUp.MODID, bus = EventBusSubscriber.Bus.GAME)
public class EventHandler {
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide) return;

        BlockEntity blockEntity = event.getLevel().getBlockEntity(event.getPos());

        if (blockEntity instanceof RandomizableContainerBlockEntity container) {
            CompoundTag nbt = blockEntity.saveWithoutMetadata(event.getLevel().registryAccess());
            if (nbt.contains("LootTable")) {
                blockEntity.setData(PackUp.CAN_QUICK_LOOT, true);
            }
        }
    }
}
