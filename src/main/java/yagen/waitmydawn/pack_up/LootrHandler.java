package yagen.waitmydawn.pack_up;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import noobanidus.mods.lootr.data.SpecialChestInventory;

import java.util.UUID;

public class LootrHandler {

    public static boolean canLoot(Container container, ServerPlayer player) {
        if (container instanceof SpecialChestInventory lootrInv) {
            UUID tileId = lootrInv.getTileId();

            if (tileId != null) {
                LootrUsageData usageData = LootrUsageData.get(player.level());
                return usageData == null || !usageData.hasPlayerUsed(tileId, player.getUUID());
            }
        }
        return false;
    }

    public static void markLooted(Container container, ServerPlayer player) {
        if (container instanceof SpecialChestInventory lootrInv) {
            UUID tileId = lootrInv.getTileId();
            if (tileId != null) {
                LootrUsageData usageData = LootrUsageData.get(player.level());
                if (usageData != null) {
                    usageData.markPlayerUsed(tileId, player.getUUID());
                }
            }
        }
    }
}
