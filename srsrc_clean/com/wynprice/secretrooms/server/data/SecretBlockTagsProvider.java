package com.wynprice.secretrooms.server.data;

import com.wynprice.secretrooms.server.blocks.SecretBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags.Blocks;
import net.minecraftforge.common.data.ExistingFileHelper;

public class SecretBlockTagsProvider extends BlockTagsProvider {
   public SecretBlockTagsProvider(DataGenerator generatorIn, ExistingFileHelper existingFileHelper) {
      super(generatorIn, "secretroomsmod", existingFileHelper);
   }

   protected void m_6577_() {
      this.m_206424_(SecretBlockTags.ONE_WAY_GLASS_CULL).m_126582_((Block)SecretBlocks.ONE_WAY_GLASS.get()).addTags(new TagKey[]{Blocks.GLASS});
   }
}
