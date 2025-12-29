package yagen.waitmydawn.pack_up;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PlayerLootData implements ICapabilitySerializable<CompoundTag> {

    private final List<ItemStackHandler> pages = new ArrayList<>();
    private final LazyOptional<PlayerLootData> instance = LazyOptional.of(() -> this);

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
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        for (ItemStackHandler handler : pages) {
            list.add(handler.serializeNBT());
        }
        tag.put("Pages", list);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        pages.clear();
        ListTag list = tag.getList("Pages", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            ItemStackHandler handler = new ItemStackHandler(27);
            handler.deserializeNBT(list.getCompound(i));
            pages.add(handler);
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return PackUp.LOOT_DATA_CAPABILITY.orEmpty(cap, instance);
    }

    public void copyFrom(PlayerLootData source) {
        this.pages.clear();
        for (ItemStackHandler sourceHandler : source.pages) {
            ItemStackHandler newHandler = new ItemStackHandler(sourceHandler.getSlots());
            for (int i = 0; i < sourceHandler.getSlots(); i++) {
                newHandler.setStackInSlot(i, sourceHandler.getStackInSlot(i).copy());
            }
            this.pages.add(newHandler);
        }
    }
}