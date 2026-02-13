package yagen.waitmydawn.pack_up.mixin;

import com.simibubi.create.content.logistics.funnel.AbstractFunnelBlock;
import com.simibubi.create.content.logistics.funnel.FunnelBlockEntity;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yagen.waitmydawn.pack_up.PackUp;
import yagen.waitmydawn.pack_up.capabilities.PageContainerItemHandler;

import java.util.List;

@Mixin(FunnelBlockEntity.class)
public abstract class FunnelBlockEntityMixin extends SmartBlockEntity {
    @Shadow private FilteringBehaviour filtering;
    @Shadow private InvManipulationBehaviour invManipulation;
    @Shadow public abstract void onTransfer(ItemStack stack);
    @Shadow public abstract void flap(boolean inward);

    public FunnelBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void pack_up$checkBeltExtraction(CallbackInfo ci) {
        if (level == null || level.isClientSide) return;
        ItemStack filter = filtering.getFilter();
        if (filter.isEmpty()) return;
        BlockState state = this.getBlockState();
        Direction funnelFacing = AbstractFunnelBlock.getFunnelFacing(state);
        if (funnelFacing == null) return;

        BlockPos beltPos = worldPosition.relative(funnelFacing);
        BeltBlockEntity belt = BeltHelper.getSegmentBE(level, beltPos);
        if (belt == null) {
            belt = BeltHelper.getSegmentBE(level, worldPosition.below());
        }
        if (belt == null) return;

        List<TransportedItemStack> transport = belt.getInventory().getTransportedItems();

        for (TransportedItemStack transported : transport) {
            ItemStack stackOnBelt = transported.stack;
            if (stackOnBelt.getItem() == PackUp.PAGE_CONTAINER.get()) {
                if (ItemStack.isSameItem(filter, stackOnBelt)) continue;
                PageContainerItemHandler handler = new PageContainerItemHandler(stackOnBelt);
                for (int i = 0; i < handler.getSlots(); i++) {
                    ItemStack insideStack = handler.getStackInSlot(i);
                    if (!insideStack.isEmpty() && filtering.test(insideStack)) {
                        int amountToExtract = Math.min(insideStack.getCount(), 64);
                        if (!filtering.anyAmount()) {
                            amountToExtract = Math.min(amountToExtract, filtering.getAmount());
                        }
                        ItemStack simulatedExtract = handler.extractItem(i, amountToExtract, true);
                        if (simulatedExtract.isEmpty()) continue;
                        ItemStack remainderSim = invManipulation.simulate().insert(simulatedExtract);
                        int actualCount = simulatedExtract.getCount() - remainderSim.getCount();

                        if (actualCount > 0) {
                            ItemStack extracted = handler.extractItem(i, actualCount, false);
                            invManipulation.insert(extracted);
                            flap(true);
                            onTransfer(extracted);
                            belt.setChanged();
                            belt.sendData();
                            return;
                        }
                    }
                }
            }
        }
    }
}