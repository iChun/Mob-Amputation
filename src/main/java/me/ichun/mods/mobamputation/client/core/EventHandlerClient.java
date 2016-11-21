package me.ichun.mods.mobamputation.client.core;

import me.ichun.mods.mobamputation.client.entity.EntityGib;
import me.ichun.mods.mobamputation.client.render.ModelGib;
import me.ichun.mods.mobamputation.common.MobAmputation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EventHandlerClient
{
    public boolean serverHasMod;

    public HashMap<EntityLivingBase, EntityGib[]> amputationMap = new HashMap<>();

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END)
        {
            Iterator<Map.Entry<EntityLivingBase, EntityGib[]>> ite = amputationMap.entrySet().iterator();
            while(ite.hasNext())
            {
                Map.Entry<EntityLivingBase, EntityGib[]> e = ite.next();
                if(e.getKey().isDead || e.getKey().isChild())
                {
                    e.getValue()[0].setDead();
                    e.getValue()[1].setDead();
                    e.getValue()[2].setDead();
                    if(e.getKey().isChild() && e.getKey() instanceof EntityZombie)
                    {
                        e.getKey().setSize(0F, 0F);
                        e.getKey().setSize(0.6F, 1.95F);
                        ((EntityZombie)e.getKey()).setChildSize(true);
                    }
                    ite.remove();
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderLivingPre(RenderLivingEvent.Pre event)
    {
        EntityGib[] gibs = amputationMap.get(event.getEntity());
        if(gibs != null)
        {
            boolean gibbed = false;
            for(int i = 0; i <= 2; i++)
            {
                if(!gibs[i].attached)
                {
                    gibbed = true;
                    break;
                }
            }
            if(gibbed)
            {
                if(event.getRenderer() instanceof RenderBiped || event.getRenderer() instanceof RenderPlayer)
                {
                    ModelBase model = event.getRenderer().mainModel;
                    ModelBiped bipedPass1 = null;
                    ModelBiped bipedPass2 = null;
                    for(int i = 0; i < event.getRenderer().layerRenderers.size(); i++)
                    {
                        LayerRenderer layer = (LayerRenderer)event.getRenderer().layerRenderers.get(i);
                        if(layer instanceof LayerBipedArmor)
                        {
                            LayerBipedArmor armor = (LayerBipedArmor)layer;
                            bipedPass1 = armor.modelLeggings;
                            bipedPass2 = armor.modelArmor;
                            break;
                        }
                    }
                    if(model instanceof ModelBiped && bipedPass1 != null && bipedPass2 != null)
                    {
                        ModelBiped biped = (ModelBiped)model;

                        ModelGib.bipedHead1 = bipedPass1.bipedHead;
                        ModelGib.bipedHeadwear1 = bipedPass1.bipedHeadwear;
                        ModelGib.bipedLeftArm1 = bipedPass1.bipedLeftArm;
                        ModelGib.bipedRightArm1 = bipedPass1.bipedRightArm;

                        ModelGib.bipedHead2 = bipedPass2.bipedHead;
                        ModelGib.bipedHeadwear2 = bipedPass2.bipedHeadwear;
                        ModelGib.bipedLeftArm2 = bipedPass2.bipedLeftArm;
                        ModelGib.bipedRightArm2 = bipedPass2.bipedRightArm;

                        if(!gibs[0].attached)
                        {
                            biped.bipedHead.showModel = false;
                            biped.bipedHeadwear.showModel = false;

                            bipedPass1.bipedHead = bipedPass2.bipedHead = bipedPass1.bipedHeadwear = bipedPass2.bipedHeadwear = ModelGib.emptyModel;
                        }
                        if(!gibs[1].attached)
                        {
                            biped.bipedLeftArm.showModel = false;

                            bipedPass1.bipedLeftArm = bipedPass2.bipedLeftArm = ModelGib.emptyModel;
                        }
                        if(!gibs[2].attached)
                        {
                            biped.bipedRightArm.showModel = false;

                            bipedPass1.bipedRightArm = bipedPass2.bipedRightArm = ModelGib.emptyModel;
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderLivingPost(RenderLivingEvent.Post event)
    {
        EntityGib[] gibs = amputationMap.get(event.getEntity());
        if(gibs != null)
        {
            boolean gibbed = false;
            for(int i = 0; i <= 2; i++)
            {
                if(!gibs[i].attached)
                {
                    gibbed = true;
                    break;
                }
            }
            if(gibbed)
            {
                if(event.getRenderer() instanceof RenderBiped || event.getRenderer() instanceof RenderPlayer)
                {
                    ModelBase model = event.getRenderer().mainModel;
                    ModelBiped bipedPass1 = null;
                    ModelBiped bipedPass2 = null;
                    for(int i = 0; i < event.getRenderer().layerRenderers.size(); i++)
                    {
                        LayerRenderer layer = (LayerRenderer)event.getRenderer().layerRenderers.get(i);
                        if(layer instanceof LayerBipedArmor)
                        {
                            LayerBipedArmor armor = (LayerBipedArmor)layer;
                            bipedPass1 = armor.modelLeggings;
                            bipedPass2 = armor.modelArmor;
                            break;
                        }
                    }
                    if(model instanceof ModelBiped && bipedPass1 != null && bipedPass2 != null)
                    {
                        ModelBiped biped = (ModelBiped)model;

                        bipedPass1.bipedHead = ModelGib.bipedHead1;
                        bipedPass1.bipedHeadwear = ModelGib.bipedHeadwear1;
                        bipedPass1.bipedLeftArm = ModelGib.bipedLeftArm1;
                        bipedPass1.bipedRightArm = ModelGib.bipedRightArm1;

                        bipedPass2.bipedHead = ModelGib.bipedHead2;
                        bipedPass2.bipedHeadwear = ModelGib.bipedHeadwear2;
                        bipedPass2.bipedLeftArm = ModelGib.bipedLeftArm2;
                        bipedPass2.bipedRightArm = ModelGib.bipedRightArm2;

                        biped.bipedHead.showModel = true;
                        biped.bipedHeadwear.showModel = true;
                        biped.bipedLeftArm.showModel = true;
                        biped.bipedRightArm.showModel = true;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onEntitySpawn(EntityJoinWorldEvent event)
    {
        if(event.getEntity().worldObj.isRemote)
        {
            Minecraft mc = Minecraft.getMinecraft();
            if(event.getEntity() instanceof EntityZombie && !((EntityZombie)event.getEntity()).isVillager() || event.getEntity() instanceof EntitySkeleton || MobAmputation.config.playerGibs == 1 && event.getEntity() instanceof EntityPlayer && event.getEntity() != mc.thePlayer)
            {
                EntityLivingBase living = (EntityLivingBase)event.getEntity();
                if(!amputationMap.containsKey(living) && !living.isChild())
                {
                    attachGibs(event.getEntity().worldObj, living);
                }
            }
        }
    }

    public void attachGibs(World world, EntityLivingBase living)
    {
        living.setSize(0.4F, 1.5F);
        living.setPosition(living.posX, living.posY, living.posZ);
        EntityGib[] gibs = new EntityGib[3];
        for(int i = 0; i <= 2; i++)
        {
            gibs[i] = new EntityGib(world, living, i);
            world.spawnEntityInWorld(gibs[i]);
        }
        amputationMap.put(living, gibs);
    }
}
