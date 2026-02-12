package yagen.waitmydawn.pack_up;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlayerLootData {
    public static final Codec<PlayerLootData> CODEC = CompoundTag.CODEC.xmap(
            PlayerLootData::fromNbt,
            PlayerLootData::toNbt
    );

    public static final StreamCodec<ByteBuf, PlayerLootData> STREAM_CODEC = ByteBufCodecs.COMPOUND_TAG.map(
            PlayerLootData::fromNbt,
            PlayerLootData::toNbt
    );

    private final List<ItemStackHandler> pages = new ArrayList<>();

    public PlayerLootData() {
    }

    public PlayerLootData(List<ItemStackHandler> pages) {
        this.pages.addAll(pages);
    }

    public List<ItemStackHandler> getPages() {
        return pages;
    }

    public void addPage(ItemStackHandler handler) {
        this.pages.add(handler);
    }

    public void addAll(List<ItemStackHandler> newPages) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        HolderLookup.Provider provider = server != null ? server.registryAccess() : null;

        for (ItemStackHandler handler : newPages) {
            ItemStackHandler newHandler = new ItemStackHandler(handler.getSlots());
            if (provider != null) {
                newHandler.deserializeNBT(provider, handler.serializeNBT(provider));
            } else {
                for (int i = 0; i < handler.getSlots(); i++) {
                    newHandler.setStackInSlot(i, handler.getStackInSlot(i).copy());
                }
            }
            this.pages.add(newHandler);
        }
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

    public static CompoundTag toNbt(PlayerLootData data) {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        HolderLookup.Provider provider = server != null ? server.registryAccess() : null;

        for (ItemStackHandler handler : data.pages) {
            if (provider != null) {
                list.add(handler.serializeNBT(provider));
            } else {
                try {
                    list.add(handler.serializeNBT(provider));
                } catch (Exception e) {
                    list.add(new CompoundTag());
                }
            }
        }
        tag.put("Pages", list);
        return tag;
    }

    public static PlayerLootData fromNbt(CompoundTag tag) {
        PlayerLootData data = new PlayerLootData();

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        HolderLookup.Provider provider = server != null ? server.registryAccess() : null;

        if (tag.contains("Pages", Tag.TAG_LIST)) {
            ListTag list = tag.getList("Pages", Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                ItemStackHandler handler = new ItemStackHandler(27);
                if (provider != null) {
                    handler.deserializeNBT(provider, list.getCompound(i));
                }
                data.addPage(handler);
            }
        }
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerLootData that = (PlayerLootData) o;
        return this.pages.size() == that.pages.size();
    }

    @Override
    public int hashCode() {
        return Objects.hash(pages.size());
    }
}