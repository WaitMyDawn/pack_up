package yagen.waitmydawn.pack_up;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
@SuppressWarnings({"removal"})
public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(PackUp.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 0;
        INSTANCE.messageBuilder(PacketStoreLoot.class, id++)
                .encoder(PacketStoreLoot::encode)
                .decoder(PacketStoreLoot::new)
                .consumerMainThread(PacketStoreLoot::handle)
                .add();

        INSTANCE.messageBuilder(PacketOpenStorage.class, id++)
                .encoder(PacketOpenStorage::encode)
                .decoder(PacketOpenStorage::new)
                .consumerMainThread(PacketOpenStorage::handle)
                .add();

        INSTANCE.messageBuilder(PacketChangePage.class, id++)
                .encoder(PacketChangePage::encode)
                .decoder(PacketChangePage::new)
                .consumerMainThread(PacketChangePage::handle)
                .add();
    }

    public static void sendToServer(Object msg) {
        INSTANCE.send(PacketDistributor.SERVER.noArg(), msg);
    }
}