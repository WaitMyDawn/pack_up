package yagen.waitmydawn;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockLootFlag implements ICapabilitySerializable<CompoundTag> {
    private boolean canQuickLoot = false;
    private final LazyOptional<BlockLootFlag> instance = LazyOptional.of(() -> this);

    public void setCanQuickLoot(boolean value) {
        this.canQuickLoot = value;
    }

    public boolean canQuickLoot() {
        return canQuickLoot;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("CanQuickLoot", canQuickLoot);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.canQuickLoot = tag.getBoolean("CanQuickLoot");
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return PackUp.CAN_QUICK_LOOT_CAPABILITY.orEmpty(cap, instance);
    }
}