package yagen.waitmydawn.pack_up.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import yagen.waitmydawn.pack_up.LootStorageMenu;
import yagen.waitmydawn.pack_up.PackUp;

public record PacketExtractPage(boolean extractAll) implements CustomPacketPayload {
    public static final Type<PacketExtractPage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(PackUp.MODID, "extract_page"));

    public static final StreamCodec<ByteBuf, PacketExtractPage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, PacketExtractPage::extractAll,
            PacketExtractPage::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(PacketExtractPage payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            if (player.containerMenu instanceof LootStorageMenu menu) {
                menu.extractPage(player, payload.extractAll);
            }
        });
    }
}