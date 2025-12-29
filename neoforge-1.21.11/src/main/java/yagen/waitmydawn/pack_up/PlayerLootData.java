package yagen.waitmydawn.pack_up;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PlayerLootData {

    private final List<SimpleContainer> pages = new ArrayList<>();

    public PlayerLootData() {}

    private static final Codec<SimpleContainer> PAGE_CODEC = ItemStack.OPTIONAL_CODEC.listOf().xmap(
            list -> {
                SimpleContainer container = new SimpleContainer(27);
                for (int i = 0; i < list.size() && i < 27; i++) {
                    container.setItem(i, list.get(i));
                }
                return container;
            },
            container -> {
                NonNullList<ItemStack> list = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);
                for(int i=0; i<container.getContainerSize(); i++) {
                    list.set(i, container.getItem(i));
                }
                return list;
            }
    );

    public static final MapCodec<PlayerLootData> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            PAGE_CODEC.listOf().fieldOf("pages").forGetter(data -> data.pages)
    ).apply(instance, PlayerLootData::new));


    public PlayerLootData(List<SimpleContainer> pages) {
        this.pages.addAll(pages);
    }

    public List<SimpleContainer> getPages() {
        return pages;
    }

    public void addPage(SimpleContainer page) {
        this.pages.add(page);
    }

    public void cleanEmptyPages() {
        pages.removeIf(SimpleContainer::isEmpty);
    }
}
