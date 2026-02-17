package yagen.waitmydawn.pack_up.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import yagen.waitmydawn.pack_up.gui.loot_storage.LootStorageMenu;
import yagen.waitmydawn.pack_up.PackUp;

public record PacketJumpToPage(int pageIndex) implements CustomPacketPayload {
    public static final Type<PacketJumpToPage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(PackUp.MODID, "jump_to_page"));

    public static final StreamCodec<ByteBuf, PacketJumpToPage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, PacketJumpToPage::pageIndex,
            PacketJumpToPage::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(PacketJumpToPage payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            if (player.containerMenu instanceof LootStorageMenu menu) {
                menu.jumpToPage(payload.pageIndex);
            }
        });
    }
}