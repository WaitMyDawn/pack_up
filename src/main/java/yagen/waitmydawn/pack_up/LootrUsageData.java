package yagen.waitmydawn.pack_up;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.*;

public class LootrUsageData extends SavedData {
    private static final String DATA_NAME = "pack_up_lootr_tracking";

    private final Map<UUID, Set<UUID>> usedPlayers = new HashMap<>();

    public static LootrUsageData get(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            return serverLevel.getServer().overworld().getDataStorage()
                    .computeIfAbsent(
                            LootrUsageData::load,
                            LootrUsageData::new
                    , DATA_NAME);
        }
        return null;
    }

    public LootrUsageData() {}

    public boolean hasPlayerUsed(UUID tileId, UUID playerUUID) {
        if (!usedPlayers.containsKey(tileId)) return false;
        return usedPlayers.get(tileId).contains(playerUUID);
    }

    public void markPlayerUsed(UUID tileId, UUID playerUUID) {
        usedPlayers.computeIfAbsent(tileId, k -> new HashSet<>()).add(playerUUID);
        setDirty();
    }

    public static LootrUsageData load(CompoundTag nbt) {
        LootrUsageData data = new LootrUsageData();
        ListTag list = nbt.getList("Records", Tag.TAG_COMPOUND);
        for (Tag t : list) {
            CompoundTag entry = (CompoundTag) t;
            if (entry.hasUUID("TileId")) {
                UUID tileId = entry.getUUID("TileId");
                ListTag playerList = entry.getList("Players", Tag.TAG_INT_ARRAY);
                Set<UUID> pSet = new HashSet<>();
                for (Tag p : playerList) {
                    pSet.add(NbtUtils.loadUUID(p));
                }
                data.usedPlayers.put(tileId, pSet);
            }
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        ListTag list = new ListTag();
        for (Map.Entry<UUID, Set<UUID>> entry : usedPlayers.entrySet()) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putUUID("TileId", entry.getKey());
            ListTag playerList = new ListTag();
            for (UUID pid : entry.getValue()) {
                playerList.add(NbtUtils.createUUID(pid));
            }
            entryTag.put("Players", playerList);
            list.add(entryTag);
        }
        nbt.put("Records", list);
        return nbt;
    }
}