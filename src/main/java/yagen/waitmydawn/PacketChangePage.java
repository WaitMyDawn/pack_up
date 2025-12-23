package yagen.waitmydawn;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record PacketChangePage(int pageOffset) {

    public PacketChangePage(FriendlyByteBuf buf) {
        this(buf.readInt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(pageOffset);
    }

    public static void handle(PacketChangePage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getSender() != null && ctx.get().getSender().containerMenu instanceof LootStorageMenu menu) {
                menu.changePage(msg.pageOffset);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}