package com.wynprice.secretrooms.server.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.ExistingFileHelper;

public class SecretItemTagsProvider extends ItemTagsProvider {
   public SecretItemTagsProvider(DataGenerator dataGenerator, ExistingFileHelper existingFileHelper) {
      super(dataGenerator, new SecretBlockTagsProvider(dataGenerator, existingFileHelper), "secretroomsmod", existingFileHelper);
   }

   protected void m_6577_() {
      this.m_206424_(SecretItemTags.EARTH_ITEM).m_126584_(new Item[]{Items.f_42461_, Items.f_42329_, Items.f_41830_});
      this.m_206424_(SecretItemTags.SECRET_RECIPE_ITEMS).m_126582_(Items.f_42583_).addTags(new TagKey[]{ItemTags.f_13167_});
      this.m_206424_(SecretItemTags.CLEAR_GLASS).m_126582_(Items.f_41904_);
   }
}
