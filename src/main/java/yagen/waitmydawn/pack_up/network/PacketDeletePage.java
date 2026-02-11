package yagen.waitmydawn.pack_up.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import yagen.waitmydawn.pack_up.LootStorageMenu;
import yagen.waitmydawn.pack_up.PackUp;

public record PacketDeletePage(boolean deleteAll) implements CustomPacketPayload {
    public static final Type<PacketDeletePage> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(PackUp.MODID, "delete_page"));

    public static final StreamCodec<ByteBuf, PacketDeletePage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, PacketDeletePage::deleteAll,
            PacketDeletePage::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(PacketDeletePage payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            if (player.containerMenu instanceof LootStorageMenu menu) {
                menu.deletePage(payload.deleteAll);
            }
        });
    }
}