package yagen.waitmydawn;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class EventHandler {

    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide()) return;

        BlockEntity blockEntity = event.getLevel().getBlockEntity(event.getPos());

        if (blockEntity instanceof RandomizableContainerBlockEntity container) {
            CompoundTag nbt = blockEntity.saveWithoutMetadata(event.getLevel().registryAccess());
            if (nbt.contains("LootTable")) {
                blockEntity.setData(PackUp.CAN_QUICK_LOOT, true);
            }
        }
    }
}
