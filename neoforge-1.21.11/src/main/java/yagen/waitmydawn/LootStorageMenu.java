package yagen.waitmydawn;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class LootStorageMenu extends AbstractContainerMenu {
    private final PlayerLootData lootData;
    private final Inventory playerInventory;

    private final PageProxyContainer proxyContainer;

    private int clientTotalPages = 0;
    private int currentPage = 0;

    private static final SimpleContainer EMPTY_CONTAINER = new SimpleContainer(27);

    public LootStorageMenu(int containerId, Inventory playerInv, FriendlyByteBuf extraData) {
        this(containerId, playerInv);
    }

    public LootStorageMenu(int containerId, Inventory playerInv) {
        super(PackUp.LOOT_MENU.get(), containerId);
        this.playerInventory = playerInv;
        this.lootData = playerInv.player.getData(PackUp.LOOT_DATA);

        this.proxyContainer = new PageProxyContainer();
        refreshProxyTarget();

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(proxyContainer, col + row * 9, 8 + col * 18, 18 + row * 18) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return false;
                    }
                });
            }
        }

        addPlayerSlots(playerInv);

        addDataSlot(new DataSlot() {
            @Override public int get() { return currentPage; }
            @Override public void set(int value) { currentPage = value; }
        });
        addDataSlot(new DataSlot() {
            @Override public int get() { return playerInv.player.level().isClientSide() ? clientTotalPages : lootData.getPages().size(); }
            @Override public void set(int value) { clientTotalPages = value; }
        });
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

    private void refreshProxyTarget() {
        if (lootData.getPages().isEmpty()) {
            proxyContainer.setTarget(EMPTY_CONTAINER);
        } else {
            int max = lootData.getPages().size();
            if (currentPage >= max) currentPage = max - 1;
            if (currentPage < 0) currentPage = 0;
            proxyContainer.setTarget(lootData.getPages().get(currentPage));
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

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!player.level().isClientSide()) {
            lootData.cleanEmptyPages();
        }
    }

    public int getCurrentPage() { return currentPage; }
    public int getTotalPages() { return playerInventory.player.level().isClientSide() ? clientTotalPages : lootData.getPages().size(); }

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

    private static class PageProxyContainer implements Container {
        private SimpleContainer target = EMPTY_CONTAINER;

        public void setTarget(SimpleContainer target) { this.target = target; }

        @Override public int getContainerSize() { return target.getContainerSize(); }
        @Override public boolean isEmpty() { return target.isEmpty(); }
        @Override public ItemStack getItem(int slot) { return target.getItem(slot); }
        @Override public ItemStack removeItem(int slot, int amount) { return target.removeItem(slot, amount); }
        @Override public ItemStack removeItemNoUpdate(int slot) { return target.removeItemNoUpdate(slot); }
        @Override public void setItem(int slot, ItemStack stack) { target.setItem(slot, stack); }
        @Override public void setChanged() { target.setChanged(); }
        @Override public boolean stillValid(Player player) { return true; }
        @Override public void clearContent() { target.clearContent(); }
    }
}