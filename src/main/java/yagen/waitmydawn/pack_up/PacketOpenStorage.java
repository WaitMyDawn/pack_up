package yagen.waitmydawn.pack_up;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

import java.util.function.Supplier;

public class PacketOpenStorage {

    public PacketOpenStorage() {}
    public PacketOpenStorage(FriendlyByteBuf buf) {}
    public void encode(FriendlyByteBuf buf) {}

    public static void handle(PacketOpenStorage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                NetworkHooks.openScreen(player, new SimpleMenuProvider(
                        (id, inv, p) -> new LootStorageMenu(id, inv),
                        Component.translatable("ui.pack_up.loot_storage")
                ));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}