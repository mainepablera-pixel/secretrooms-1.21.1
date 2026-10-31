package com.wynprice.secretrooms.server.items;

import com.mojang.datafixers.util.Either;
import com.wynprice.secretrooms.client.SwitchProbeTooltip;
import com.wynprice.secretrooms.client.SwitchProbeTooltipComponent;
import com.wynprice.secretrooms.server.blocks.SecretBaseBlock;
import com.wynprice.secretrooms.server.data.SecretData;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;

public class SwitchProbe extends Item {
   public static final String PROBE_HIT_DATA = "probe_hit_data";

   public SwitchProbe(Item.Properties properties) {
      super(properties);
   }

   public InteractionResultHolder<ItemStack> m_7203_(Level world, Player playerIn, InteractionHand handIn) {
      ItemStack stack = playerIn.m_21120_(handIn);
      return new InteractionResultHolder(InteractionResult.SUCCESS, stack);
   }

   public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
      Optional<SecretData> data = SecretBaseBlock.getMirrorData(context.m_43725_(), context.m_8083_());
      BlockState state = context.m_43725_().m_8055_(context.m_8083_());
      if (data.isPresent()) {
         CompoundTag compound = stack.m_41784_().m_128469_("probe_hit_data");
         if (!compound.m_128456_()) {
            SecretData d = new SecretData((BlockEntity)null);
            d.readNBT(compound);
            boolean solid = d.getBlockState().m_60815_();
            if (solid != state.m_60815_()) {
               context.m_43725_().m_46597_(context.m_8083_(), (BlockState)state.m_61124_(SecretBaseBlock.SOLID, solid));
            }

            if (state.m_61138_(BlockStateProperties.f_61362_) && d.getBlockState().m_61138_(BlockStateProperties.f_61362_)) {
               d.setBlockState((BlockState)d.getBlockState().m_61124_(BlockStateProperties.f_61362_, (Boolean)state.m_61143_(BlockStateProperties.f_61362_)));
            }

            ((SecretData)data.get()).setFrom(d);
         }
      } else {
         SecretData d = new SecretData((BlockEntity)null);
         d.setBlockState(state);
         BlockEntity tileEntity = context.m_43725_().m_7702_(context.m_8083_());
         d.setTileEntityNBT(tileEntity != null ? tileEntity.serializeNBT() : null);
         d.writeNBT(stack.m_41698_("probe_hit_data"));
      }

      return InteractionResult.SUCCESS;
   }

   public static void onRegisterTooltipFactories(RegisterClientTooltipComponentFactoriesEvent event) {
      event.register(SwitchProbeTooltipComponent.class, SwitchProbeTooltip::new);
   }

   public static void appendHover(RenderTooltipEvent.GatherComponents event) {
      if (event.getItemStack().m_41720_() == SecretItems.SWITCH_PROBE.get()) {
         ItemStack stack = event.getItemStack();
         List<Either<FormattedText, TooltipComponent>> elements = event.getTooltipElements();
         CompoundTag compound = stack.m_41784_().m_128469_("probe_hit_data");
         elements.add(Either.right(new SwitchProbeTooltipComponent(Component.m_237115_("secretroomsmod.probe.containedblock").m_130940_(ChatFormatting.GOLD).m_7532_(), stack)));
         if (compound.m_128456_()) {
            elements.add(Either.left(Component.m_237115_("secretroomsmod.probe.noneset")));
         } else if (Screen.m_96638_()) {
            SecretData data = new SecretData((BlockEntity)null);
            data.readNBT(compound);
            BlockState state = data.getBlockState();
            elements.add(Either.left(Component.m_237115_("secretroomsmod.probe.data").m_130940_(ChatFormatting.BLUE)));
            elements.add(Either.left(Component.m_237110_("secretroomsmod.probe.blockset", new Object[]{state.m_60734_().m_49954_()}).m_130940_(ChatFormatting.AQUA)));

            for(Property<?> property : state.m_61147_()) {
               elements.add(Either.left(Component.m_237110_("secretroomsmod.probe.blockproperty", new Object[]{property.m_61708_(), propertyString(property, state.m_61143_(property))}).m_130940_(ChatFormatting.DARK_AQUA)));
            }
         }

      }
   }

   private static <T extends Comparable<T>> String propertyString(Property<T> property, Comparable<?> value) {
      return property.m_6940_(value);
   }
}
