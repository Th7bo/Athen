package xyz.aerii.athen.mixin.mixins;

import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import xyz.aerii.athen.ducks.entity.item.ItemEntityDuck;
import xyz.aerii.athen.modules.impl.slayer.BigSlayerDrops;

@Mixin(ItemEntity.class)
public class ItemEntityMixin implements ItemEntityDuck {
    @Unique
    private int athen$big = -1;

    @Override
    public int athen$big() {
        if (athen$big == -1) athen$big = BigSlayerDrops.fn((ItemEntity) (Object) this) ? 1 : 0;
        return athen$big;
    }
}
