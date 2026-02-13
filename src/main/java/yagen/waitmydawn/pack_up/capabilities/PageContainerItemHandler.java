package yagen.waitmydawn.pack_up.capabilities;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import yagen.waitmydawn.pack_up.PackUp;

import javax.annotation.Nonnull;
import java.util.List;

public class PageContainerItemHandler implements IItemHandlerModifiable {
    private final ItemStack containerStack;

    public PageContainerItemHandler(ItemStack containerStack) {
        this.containerStack = containerStack;
    }

    private PlayerLootData getData() {
        return containerStack.getOrDefault(PackUp.PAGE_DATA_COMPONENT, new PlayerLootData());
    }

    private void saveData(PlayerLootData data) {
        containerStack.set(PackUp.PAGE_DATA_COMPONENT, data);
    }

    @Override
    public int getSlots() {
        return getData().getPages().size() * 27;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        PlayerLootData data = getData();
        List<ItemStackHandler> pages = data.getPages();

        int pageIndex = slot / 27;
        int subSlot = slot % 27;

        if (pageIndex >= 0 && pageIndex < pages.size()) {
            return pages.get(pageIndex).getStackInSlot(subSlot);
        }
        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) return ItemStack.EMPTY;

        PlayerLootData data = getData();
        List<ItemStackHandler> pages = data.getPages();

        int pageIndex = slot / 27;
        int subSlot = slot % 27;

        if (pageIndex >= 0 && pageIndex < pages.size()) {
            ItemStackHandler handler = pages.get(pageIndex);
            ItemStack remainder = handler.insertItem(subSlot, stack, simulate);

            if (!simulate && remainder.getCount() < stack.getCount()) {
                saveData(data);
            }
            return remainder;
        }
        return stack;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        PlayerLootData data = getData();
        List<ItemStackHandler> pages = data.getPages();

        int pageIndex = slot / 27;
        int subSlot = slot % 27;

        if (pageIndex >= 0 && pageIndex < pages.size()) {
            ItemStackHandler handler = pages.get(pageIndex);
            ItemStack extracted = handler.extractItem(subSlot, amount, simulate);

            if (!simulate && !extracted.isEmpty()) {
                saveData(data);
            }
            return extracted;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return true;
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        PlayerLootData data = getData();
        List<ItemStackHandler> pages = data.getPages();

        int pageIndex = slot / 27;
        int subSlot = slot % 27;

        if (pageIndex >= 0 && pageIndex < pages.size()) {
            ItemStackHandler handler = pages.get(pageIndex);
            handler.setStackInSlot(subSlot, stack);
            saveData(data);
        }
    }
}