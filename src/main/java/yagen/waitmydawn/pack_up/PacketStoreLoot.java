package yagen.waitmydawn.pack_up;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkEvent;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class PacketStoreLoot {

    public PacketStoreLoot() {
    }

    public PacketStoreLoot(FriendlyByteBuf buf) {
    }

    public void encode(FriendlyByteBuf buf) {
    }

    public static void handle(PacketStoreLoot msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            AbstractContainerMenu menu = player.containerMenu;
            boolean isValidContainer = menu instanceof ChestMenu || menu instanceof ShulkerBoxMenu;

            if (isValidContainer) {
                ItemStackHandler newPage = new ItemStackHandler(27);
                AtomicBoolean hasItems = new AtomicBoolean(false);
                Container container = menu.slots.get(0).container;
                if (container instanceof BlockEntity blockEntity) {
                    blockEntity.getCapability(PackUp.CAN_QUICK_LOOT_CAPABILITY).ifPresent(cap -> {
                        if (cap.canQuickLoot()) {
                            cap.setCanQuickLoot(false);
                            processLoot(player, menu, newPage, hasItems);
                        }
                    });
                } else if (ModList.get().isLoaded("lootr")) {
                    if (LootrHandler.canLoot(container, player)) {
                        processLoot(player, menu, newPage, hasItems);
                        LootrHandler.markLooted(container, player);
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

    private static void processLoot(ServerPlayer player, AbstractContainerMenu menu, ItemStackHandler newPage, AtomicBoolean hasItems) {
        int slotCount = 0;
        for (Slot slot : menu.slots) {
            if (slot.container == player.getInventory()) continue;
            if (slotCount >= 27) break;

            if (slot.hasItem()) {
                ItemStack stack = slot.getItem().copy();
                newPage.setStackInSlot(slotCount, stack);
                slot.set(ItemStack.EMPTY);
                hasItems.set(true);
            }
            slotCount++;
        }

        if (hasItems.get()) {
            player.getCapability(PackUp.LOOT_DATA_CAPABILITY).ifPresent(data -> {
                data.addPage(newPage);
                player.level().playSound(
                        null,
                        player.blockPosition(),
                        SoundEvents.BUNDLE_INSERT,
                        SoundSource.PLAYERS,
                        1.0f,
                        1.0f
                );
            });
        }
    }
}