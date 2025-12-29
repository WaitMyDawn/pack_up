package yagen.waitmydawn.pack_up;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketOpenStorage() implements CustomPacketPayload {
    public static final Type<PacketOpenStorage> TYPE = new Type<>(Identifier.fromNamespaceAndPath(PackUp.MODID, "open_storage"));
    public static final StreamCodec<ByteBuf, PacketOpenStorage> STREAM_CODEC = StreamCodec.unit(new PacketOpenStorage());

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(PacketOpenStorage payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            player.openMenu(new SimpleMenuProvider(
                    (id, inv, p) -> new LootStorageMenu(id, inv),
                    Component.translatable("ui.pack_up.loot_storage")
            ));
        });
    }
}