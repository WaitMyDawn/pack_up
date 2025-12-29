package yagen.waitmydawn.pack_up;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;


public record PacketStoreLoot() implements CustomPacketPayload {
    public static final Type<PacketStoreLoot> TYPE = new Type<>(Identifier.fromNamespaceAndPath(PackUp.MODID, "store_loot"));
    public static final StreamCodec<ByteBuf, PacketStoreLoot> STREAM_CODEC = StreamCodec.unit(new PacketStoreLoot());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(PacketStoreLoot payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            AbstractContainerMenu menu = player.containerMenu;

            boolean isValidContainer = menu instanceof ChestMenu || menu instanceof ShulkerBoxMenu;

            if (isValidContainer) {
                SimpleContainer newPage = new SimpleContainer(27);
                boolean hasItems = false;

                if (menu.slots.getFirst().container instanceof BlockEntity blockEntity) {
                    if (!blockEntity.getData(PackUp.CAN_QUICK_LOOT))
                        return;
                    else
                        blockEntity.setData(PackUp.CAN_QUICK_LOOT, false);
                }

                int slotCount = 0;
                for (Slot slot : menu.slots) {
                    if (slot.container == player.getInventory()) continue;
                    if (slotCount >= 27) break;

                    if (slot.hasItem()) {
                        newPage.setItem(slotCount, slot.getItem().copy());
                        slot.set(ItemStack.EMPTY);
                        hasItems = true;
                    }
                    slotCount++;
                }

                if (hasItems) {
                    PlayerLootData data = player.getData(PackUp.LOOT_DATA);
                    data.addPage(newPage);
                    player.level().playSound(
                            null,
                            player.blockPosition(),
                            SoundEvents.BUNDLE_INSERT,
                            SoundSource.PLAYERS,
                            1.0f,
                            1.0f
                    );
                }
            }
        });
    }
}
