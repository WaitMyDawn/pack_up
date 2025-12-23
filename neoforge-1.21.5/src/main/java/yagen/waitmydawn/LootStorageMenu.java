package yagen.waitmydawn;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class LootStorageMenu extends AbstractContainerMenu {
    private final PlayerLootData lootData;
    private final Inventory playerInventory;

    private final PageProxyHandler proxyHandler;

    private int clientTotalPages = 0;
    private int currentPage = 0;

    private static final ItemStackHandler EMPTY_HANDLER = new ItemStackHandler(27);

    public LootStorageMenu(int containerId, Inventory playerInv, FriendlyByteBuf extraData) {
        this(containerId, playerInv);
    }

    public LootStorageMenu(int containerId, Inventory playerInv) {
        super(PackUp.LOOT_MENU.get(), containerId);
        this.playerInventory = playerInv;

        this.lootData = playerInv.player.getData(PackUp.LOOT_DATA);

        this.proxyHandler = new PageProxyHandler();
        refreshProxyTarget();

        addStorageSlots();
        addPlayerSlots(playerInv);

        addDataSlot(new DataSlot() {
            @Override
            public int get() { return currentPage; }
            @Override
            public void set(int value) { currentPage = value; }
        });

        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return lootData.getPages().isEmpty() ? 0 : lootData.getPages().size();
            }
            @Override
            public void set(int value) {
                clientTotalPages = value;
            }
        });
    }

    private void refreshProxyTarget() {
        if (lootData.getPages().isEmpty()) {
            proxyHandler.setTarget(EMPTY_HANDLER);
        } else {
            int max = lootData.getPages().size();
            if (currentPage >= max) currentPage = max - 1;
            if (currentPage < 0) currentPage = 0;

            proxyHandler.setTarget(lootData.getPages().get(currentPage));
        }
    }

    private void addStorageSlots() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new SlotItemHandler(proxyHandler, col + row * 9, 8 + col * 18, 18 + row * 18) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return false;
                    }
                });
            }
        }
    }

    private void addPlayerSlots(Inventory playerInv) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, 142));
        }
    }

    public void changePage(int offset) {
        int max = lootData.getPages().isEmpty() ? 0 : lootData.getPages().size();
        int newPage = currentPage + offset;
        if (newPage >= 0 && newPage < max) {
            this.currentPage = newPage;
            refreshProxyTarget();
            this.broadcastChanges();
        }
    }

    public int getCurrentPage() { return currentPage; }
    public int getTotalPages() {
        return this.playerInventory.player.level().isClientSide ? clientTotalPages : lootData.getPages().size();
    }

    @Override
    public void removed(Player player) {
        super.removed(player);

        if (!player.level().isClientSide) {
            lootData.cleanEmptyPages();
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index < 27) {
                if (!this.moveItemStackTo(itemstack1, 27, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    private static class PageProxyHandler implements IItemHandlerModifiable {
        private IItemHandlerModifiable target = EMPTY_HANDLER;

        public void setTarget(IItemHandlerModifiable target) {
            this.target = target;
        }

        @Override public void setStackInSlot(int slot, ItemStack stack) { target.setStackInSlot(slot, stack); }
        @Override public int getSlots() { return target.getSlots(); }
        @Override public ItemStack getStackInSlot(int slot) { return target.getStackInSlot(slot); }
        @Override public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) { return target.insertItem(slot, stack, simulate); }
        @Override public ItemStack extractItem(int slot, int amount, boolean simulate) { return target.extractItem(slot, amount, simulate); }
        @Override public int getSlotLimit(int slot) { return target.getSlotLimit(slot); }
        @Override public boolean isItemValid(int slot, ItemStack stack) { return target.isItemValid(slot, stack); }
    }
}