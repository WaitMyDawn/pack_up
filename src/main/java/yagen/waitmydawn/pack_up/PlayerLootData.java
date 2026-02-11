package yagen.waitmydawn.pack_up;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;

public class PlayerLootData implements INBTSerializable<CompoundTag> {

    private final List<ItemStackHandler> pages = new ArrayList<>();

    public PlayerLootData() {}

    public List<ItemStackHandler> getPages() {
        return pages;
    }

    public void addPage(ItemStackHandler handler) {
        this.pages.add(handler);
    }

    public void cleanEmptyPages() {
        pages.removeIf(this::isHandlerEmpty);
    }

    private boolean isHandlerEmpty(ItemStackHandler handler) {
        for (int i = 0; i < handler.getSlots(); i++) {
            if (!handler.getStackInSlot(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        for (ItemStackHandler handler : pages) {
            list.add(handler.serializeNBT(provider));
        }
        tag.put("Pages", list);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        pages.clear();
        ListTag list = tag.getList("Pages", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            ItemStackHandler handler = new ItemStackHandler(27);
            handler.deserializeNBT(provider, list.getCompound(i));
            pages.add(handler);
        }
    }
}
