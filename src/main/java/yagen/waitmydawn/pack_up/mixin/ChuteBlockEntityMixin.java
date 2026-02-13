package yagen.waitmydawn.pack_up.mixin;

import com.simibubi.create.content.logistics.chute.ChuteBlockEntity;
import com.simibubi.create.content.logistics.chute.SmartChuteBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yagen.waitmydawn.pack_up.PackUp;
import yagen.waitmydawn.pack_up.capabilities.PageContainerItemHandler;

import java.util.List;

@Mixin(ChuteBlockEntity.class)
public abstract class ChuteBlockEntityMixin extends SmartBlockEntity {

    @Shadow public abstract void setItem(ItemStack stack);
    @Shadow ItemStack item;

    public ChuteBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void pack_up$extractFromBag(CallbackInfo ci) {
        if (level == null || level.isClientSide) return;
        if (!this.item.isEmpty()) return;
        if (!((Object) this instanceof SmartChuteBlockEntity)) return;
        FilteringBehaviour filtering = getBehaviour(FilteringBehaviour.TYPE);
        if (filtering == null) return;
        ItemStack filter = filtering.getFilter();
        if (filter.isEmpty()) return;

        AABB searchArea = new AABB(worldPosition.above()).inflate(0.5, 0.25, 0.5);
        List<ItemEntity> entities = level.getEntitiesOfClass(ItemEntity.class, searchArea);
        for (ItemEntity entity : entities) {
            ItemStack stackInWorld = entity.getItem();
            if (stackInWorld.getItem() == PackUp.PAGE_CONTAINER.get()) {
                if (ItemStack.isSameItem(filter, stackInWorld)) continue;
                PageContainerItemHandler handler = new PageContainerItemHandler(stackInWorld);
                for (int i = 0; i < handler.getSlots(); i++) {
                    ItemStack insideStack = handler.getStackInSlot(i);
                    if (!insideStack.isEmpty() && filtering.test(insideStack)) {
                        int maxExtract = insideStack.getMaxStackSize();
                        if (!filtering.anyAmount()) {
                            maxExtract = Math.min(maxExtract, filtering.getAmount());
                        }
                        ItemStack extracted = handler.extractItem(i, maxExtract, false);
                        if (!extracted.isEmpty()) {
                            this.setItem(extracted);
                            entity.setItem(stackInWorld);
                            return;
                        }
                    }
                }
            }
        }
    }
}