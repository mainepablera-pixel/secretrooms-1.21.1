package com.wynprice.secretrooms.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.event.EntityRenderersEvent;

public class TrueVisionGogglesModel extends HumanoidModel<LivingEntity> {
   public static final ModelLayerLocation TRUE_VISION_GOGGLES_MODEL = new ModelLayerLocation(new ResourceLocation("secretroomsmod", "true_vision"), "main");

   public TrueVisionGogglesModel(ModelPart part) {
      super(part, RenderType::m_110473_);
   }

   protected Iterable<ModelPart> m_5608_() {
      return ImmutableList.of();
   }

   protected Iterable<ModelPart> m_5607_() {
      return ImmutableList.of(this.f_102808_, this.f_102809_);
   }

   public static void onModelDefinitionRegister(EntityRenderersEvent.RegisterLayerDefinitions event) {
   }

   public static MeshDefinition createMesh(CubeDeformation deformation, float yOff) {
      MeshDefinition mesh = HumanoidModel.m_170681_(deformation, yOff);
      PartDefinition root = mesh.m_171576_();
      root.m_171599_("hat", CubeListBuilder.m_171558_().m_171514_(32, 0).m_171488_(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, deformation.m_171469_(0.2F)), PartPose.m_171419_(0.0F, 0.0F + yOff, 0.0F));
      return mesh;
   }
}
