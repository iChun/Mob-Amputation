package me.ichun.mods.mobamputation.client.render;

import me.ichun.mods.mobamputation.client.entity.EntityGib;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;

public class ModelGib extends ModelBase
{
    public ModelRenderer arm64;
    public ModelRenderer head64;
    public ModelRenderer arm;
    public ModelRenderer head;
    public ModelRenderer armSkele;

    public static ModelRenderer bipedLeftArm1;
    public static ModelRenderer bipedRightArm1;
    public static ModelRenderer bipedHead1;
    public static ModelRenderer bipedHeadwear1;
    public static ModelRenderer bipedLeftArm2;
    public static ModelRenderer bipedRightArm2;
    public static ModelRenderer bipedHead2;
    public static ModelRenderer bipedHeadwear2;

    public static ModelRenderer emptyModel;

    public ModelGib()
    {
        textureWidth = 64;
        textureHeight = 64;

        arm64 = new ModelRenderer(this, 40, 16);
        arm64.setTextureSize(64, 64);
        arm64.addBox(-1F, -2F, -2F, 4, 12, 4);
        arm64.setRotationPoint(-1F, 22F, 0F);
        arm64.rotateAngleX = 0F;
        arm64.rotateAngleY = 0F;
        arm64.rotateAngleZ = 0F;
        arm64.mirror = false;

        head64 = new ModelRenderer(this, 0, 0);
        head64.setTextureSize(64, 64);
        head64.addBox(-4F, -4F, -4F, 8, 8, 8);
        head64.setRotationPoint(0F, 20F, 0F);
        head64.rotateAngleX = 0F;
        head64.rotateAngleY = 0F;
        head64.rotateAngleZ = 0F;
        head64.mirror = false;

        arm = new ModelRenderer(this, 40, 16);
        arm.setTextureSize(64, 32);
        arm.addBox(-1F, -2F, -2F, 4, 12, 4);
        arm.setRotationPoint(-1F, 22F, 0F);
        arm.rotateAngleX = 0F;
        arm.rotateAngleY = 0F;
        arm.rotateAngleZ = 0F;
        arm.mirror = false;

        head = new ModelRenderer(this, 0, 0);
        head.setTextureSize(64, 32);
        head.addBox(-4F, -4F, -4F, 8, 8, 8);
        head.setRotationPoint(0F, 20F, 0F);
        head.rotateAngleX = 0F;
        head.rotateAngleY = 0F;
        head.rotateAngleZ = 0F;
        head.mirror = false;

        armSkele = new ModelRenderer(this, 40, 16);
        armSkele.setTextureSize(64, 32);
        armSkele.addBox(-1F, -2F, -2F, 2, 12, 2);
        armSkele.setRotationPoint(-1F, 22F, 0F);
        armSkele.rotateAngleX = 0F;
        armSkele.rotateAngleY = 0F;
        armSkele.rotateAngleZ = 0F;
        armSkele.mirror = false;

        emptyModel = new ModelRenderer(this, 0, 0);
        emptyModel.addBox(0F, 0F, 0F, 0, 0, 0);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);

        if(entity instanceof EntityGib)
        {
            EntityGib gib = (EntityGib)entity;

            if(!gib.attached)
            {
                if(gib.type == 0)
                {
                    if(gib.parent instanceof EntityZombie || gib.parent instanceof EntityPlayer)
                    {
                        head64.render(f5);
                    }
                    else
                    {
                        head.render(f5);
                    }
                }
                else if(gib.type > 0)
                {
                    if(gib.parent instanceof EntityZombie || gib.parent instanceof EntityPlayer)
                    {
                        arm64.render(f5);
                    }
                    else if(gib.parent instanceof EntitySkeleton)
                    {
                        armSkele.render(f5);
                    }
                    else
                    {
                        arm.render(f5);
                    }
                }
            }
        }
    }

    @Override
    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity)
    {
        head64.rotateAngleY = f3 / 57.29578F;
        head64.rotateAngleX = f4 / 57.29578F;

        head.rotateAngleY = f3 / 57.29578F;
        head.rotateAngleX = f4 / 57.29578F;

        arm64.rotateAngleY = f3 / 57.29578F;
        arm64.rotateAngleX = f4 / 57.29578F;

        armSkele.rotateAngleY = f3 / 57.29578F;
        armSkele.rotateAngleX = f4 / 57.29578F;

        arm.rotateAngleY = f3 / 57.29578F;
        arm.rotateAngleX = f4 / 57.29578F;
    }

}
