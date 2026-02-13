package yagen.waitmydawn.pack_up.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import yagen.waitmydawn.pack_up.gui.loot_storage.LootStorageMenu;
import yagen.waitmydawn.pack_up.PackUp;

public record PacketChangePage(int pageOffset) implements CustomPacketPayload {
    public static final Type<PacketChangePage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(PackUp.MODID, "change_page"));
    public static final StreamCodec<ByteBuf, PacketChangePage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, PacketChangePage::pageOffset,
            PacketChangePage::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(PacketChangePage payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().containerMenu instanceof LootStorageMenu menu) {
                menu.changePage(payload.pageOffset);
            }
        });
    }
}
