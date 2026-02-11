package yagen.waitmydawn.pack_up.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import yagen.waitmydawn.pack_up.PackUp;

public class NetworkHandler {

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PackUp.MODID);

        registrar.playToServer(
                PacketStoreLoot.TYPE,
                PacketStoreLoot.STREAM_CODEC,
                PacketStoreLoot::handle
        );

        registrar.playToServer(
                PacketOpenStorage.TYPE,
                PacketOpenStorage.STREAM_CODEC,
                PacketOpenStorage::handle
        );

        registrar.playToServer(
                PacketChangePage.TYPE,
                PacketChangePage.STREAM_CODEC,
                PacketChangePage::handle
        );

        registrar.playToServer(
                PacketDeletePage.TYPE,
                PacketDeletePage.STREAM_CODEC,
                PacketDeletePage::handle
        );
    }
}
