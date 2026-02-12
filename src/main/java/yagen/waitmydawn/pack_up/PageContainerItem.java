package yagen.waitmydawn.pack_up;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.List;

public class PageContainerItem extends Item {

    public PageContainerItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (!level.isClientSide) {
            PlayerLootData containerData = stack.get(PackUp.PAGE_DATA_COMPONENT);

            if (containerData != null && !containerData.getPages().isEmpty()) {
                PlayerLootData playerData = player.getData(PackUp.LOOT_DATA);

                playerData.addAll(containerData.getPages());

                level.playSound(null, player.blockPosition(), SoundEvents.BUNDLE_DROP_CONTENTS, SoundSource.PLAYERS, 1.0f, 1.0f);
                stack.setCount(0);
                player.displayClientMessage(Component.translatable("message.pack_up.pages_restored"), true);
            } else {
                player.displayClientMessage(Component.translatable("message.pack_up.container_empty"), true);
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        PlayerLootData data = stack.get(PackUp.PAGE_DATA_COMPONENT);

        if (data != null) {
            List<ItemStackHandler> pages = data.getPages();
            int totalPages = pages.size();
            int totalItems = 0;

            for (ItemStackHandler handler : pages) {
                for (int i = 0; i < handler.getSlots(); i++) {
                    if (!handler.getStackInSlot(i).isEmpty()) {
                        totalItems += handler.getStackInSlot(i).getCount();
                    }
                }
            }

            tooltipComponents.add(Component.translatable("tooltip.pack_up.container.pages", totalPages).withStyle(ChatFormatting.GOLD));
            tooltipComponents.add(Component.translatable("tooltip.pack_up.container.items", totalItems).withStyle(ChatFormatting.BLUE));
            tooltipComponents.add(Component.empty());
            tooltipComponents.add(Component.translatable("tooltip.pack_up.container.usage").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
        }
    }
}