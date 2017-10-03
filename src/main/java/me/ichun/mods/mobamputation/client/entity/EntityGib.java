package me.ichun.mods.mobamputation.client.entity;

import me.ichun.mods.ichunutil.client.render.RendererHelper;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.mobamputation.client.particle.ParticleBlood;
import me.ichun.mods.mobamputation.common.MobAmputation;
import me.ichun.mods.mobamputation.common.packet.PacketDetachLimb;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class EntityGib extends Entity
{
    public EntityLivingBase parent;
    public int type;
    public float pitchSpin;
    public float yawSpin;

    public int groundTime;
    public int liveTime;
    public int hitTimeout;

    public boolean attached;
    public boolean detach;

    public EntityFishHook fishHook;
    public Entity projectile;
    public double projMotionX;
    public double projMotionY;
    public double projMotionZ;

    public EntityGib(World world)
    {
        super(world);
        parent = null;
        type = 0; //0 == head; 1 == left arm; 2 == right arm
        pitchSpin = 15F;
        yawSpin = 15F;

        groundTime = 0;
        liveTime = iChunUtil.eventHandlerClient.ticks;
        attached = true;
        detach = false;
        ignoreFrustumCheck = true;

        fishHook = null;
    }

    public EntityGib(World world, EntityLivingBase gibParent, int gibType)
    {
        this(world);
        parent = gibParent;
        type = gibType;
        setLocationAndAngles(parent.posX, parent.posY + parent.getEyeHeight(), parent.posZ, parent.rotationYaw, parent.rotationPitch);
    }

    @Override
    public void onUpdate()
    {
        if(parent == null || !parent.isEntityAlive() && attached)
        {
            setDead();
            return;
        }
        super.onUpdate();
        if(hitTimeout > 0)
        {
            hitTimeout--;
        }
        if(projectile != null && attached)
        {
            detach = true;
        }
        if(fishHook != null && fishHook.isDead && attached)
        {
            float chanceFloat = (float)(MobAmputation.config.toolEffect == 1 ? 33 : MobAmputation.config.gibChance) / 100F;
            if(rand.nextFloat() <= chanceFloat)
            {
                detach = true;
            }
            else
            {
                fishHook = null;
            }
        }
        if(detach && (parent.hurtTime < parent.maxHurtTime - 3 || parent instanceof EntityPlayer || fishHook != null && fishHook.isDead || projectile != null))
        {
            detach = false;
            attached = false;

            if(type == 1)
            {
                parent.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, ItemStack.EMPTY);
            }
            else if(type == 2)
            {
                parent.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
            }

            motionX = parent.motionX * 1.05D;
            motionY = parent.motionY * 1.05D + (rand.nextDouble() * 0.2D);
            motionZ = parent.motionZ * 1.05D;

            if(fishHook != null && fishHook.isDead)
            {
                motionX = (fishHook.getAngler().posX - parent.posX) * 0.1D;
                motionY = (fishHook.getAngler().posY - parent.posY) * 0.1D + (rand.nextDouble() * 0.4D);
                motionZ = (fishHook.getAngler().posZ - parent.posZ) * 0.1D;
            }
            if(projectile != null)
            {
                motionX = projMotionX * 0.8D;
                motionY = projMotionY * 0.8D;
                motionZ = projMotionZ * 0.8D;
            }

            if(MobAmputation.eventHandlerClient.serverHasMod)
            {
                MobAmputation.channel.sendToServer(new PacketDetachLimb(parent.getEntityId(), type, fishHook != null && fishHook.isDead || projectile != null));
            }

            if(MobAmputation.config.blood == 1 && (parent instanceof EntityZombie || parent instanceof EntityPlayer) && parent.isEntityAlive())
            {
                for(int k = 0; k < MobAmputation.config.bloodCount; k++)
                {
                    float var4 = 0.3F;
                    double mX = (double)(-MathHelper.sin(parent.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(parent.rotationPitch / 180.0F * (float)Math.PI) * var4);
                    double mZ = (double)(MathHelper.cos(parent.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(parent.rotationPitch / 180.0F * (float)Math.PI) * var4);
                    double mY = (double)(-MathHelper.sin(parent.rotationPitch / 180.0F * (float)Math.PI) * var4 + 0.1F);
                    var4 = 0.02F;
                    float var5 = parent.getRNG().nextFloat() * (float)Math.PI * 2.0F;
                    var4 *= parent.getRNG().nextFloat();
                    mX += Math.cos((double)var5) * (double)var4;
                    mY += (double)((parent.getRNG().nextFloat() - parent.getRNG().nextFloat()) * 0.1F);
                    mZ += Math.sin((double)var5) * (double)var4;

                    RendererHelper.spawnParticle(new ParticleBlood(world, posX, posY + (rand.nextDouble() * 0.2D), posZ, parent.motionX + mX, parent.motionY + mY, parent.motionZ + mZ, parent instanceof EntityPlayer));
                }
            }
        }
        if(attached)
        {
            liveTime = iChunUtil.eventHandlerClient.ticks;

            prevPosX = parent.prevPosX;
            prevPosY = parent.getEntityBoundingBox().minY + 1.275D;
            prevPosZ = parent.prevPosZ;
            posX = parent.posX;
            posY = parent.getEntityBoundingBox().minY + 1.275D;
            posZ = parent.posZ;
            rotationYaw = parent.rotationYawHead;
            rotationPitch = parent.rotationPitch;
            prevRotationYaw = parent.prevRotationYawHead;
            prevRotationPitch = parent.prevRotationPitch;
            if(type >= 1)
            {
                setSize(0.2F, 0.25F);
                double offset = 0.350D;
                if(type == 1)
                {
                    prevPosX += offset * Math.cos(Math.toRadians(parent.renderYawOffset));
                    prevPosZ += offset * Math.sin(Math.toRadians(parent.renderYawOffset));
                    posX += offset * Math.cos(Math.toRadians(parent.renderYawOffset));
                    posZ += offset * Math.sin(Math.toRadians(parent.renderYawOffset));
                }
                else
                {
                    prevPosX -= offset * Math.cos(Math.toRadians(parent.renderYawOffset));
                    prevPosZ -= offset * Math.sin(Math.toRadians(parent.renderYawOffset));
                    posX -= offset * Math.cos(Math.toRadians(parent.renderYawOffset));
                    posZ -= offset * Math.sin(Math.toRadians(parent.renderYawOffset));
                }
                prevRotationYaw = rotationYaw = parent.prevRenderYawOffset;

                prevRotationPitch = rotationPitch = -90F;
            }
            else if(type == 0)
            {
                if(parent instanceof EntitySkeleton)
                {
                    setSize(parent.width + 0.05F, parent.width * 0.8F); //skeletons set size every tick, which can't be overriden. Increase the hitbox of the head isntead.. ANNOYING!
                }
                else
                {
                    setSize(0.5F, 0.5F);
                }
                rotationYaw = parent.rotationYawHead;
                rotationPitch = parent.rotationPitch;
                prevPosY += 0.225D;
                posY += 0.225D;
            }

            motionX = motionY = motionZ = 0.0D;

            setPosition(posX, posY, posZ);
        }
        else
        {
            move(MoverType.SELF, motionX, motionY, motionZ);

            this.motionY -= 0.08D;

            this.motionY *= 0.98D;
            this.motionX *= 0.91D;
            this.motionZ *= 0.91D;

            if(onGround)
            {
                rotationPitch += (-90F - (rotationPitch % 360F)) / 2;

                this.motionY *= 0.8D;
                this.motionX *= 0.8D;
                this.motionZ *= 0.8D;
            }
            else if(projectile != null && collided)
            {
                if((motionX != 0.0D || motionZ != 0.0D) && MobAmputation.config.blood == 1 && (parent instanceof EntityZombie || parent instanceof EntityPlayer) && parent.isEntityAlive())
                {
                    for(int k = 0; k < MobAmputation.config.bloodCount; k++)
                    {
                        float var4 = 0.3F;
                        double mX = (double)(-MathHelper.sin(parent.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(parent.rotationPitch / 180.0F * (float)Math.PI) * var4);
                        double mZ = (double)(MathHelper.cos(parent.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(parent.rotationPitch / 180.0F * (float)Math.PI) * var4);
                        double mY = (double)(-MathHelper.sin(parent.rotationPitch / 180.0F * (float)Math.PI) * var4 + 0.1F);
                        var4 = 0.02F;
                        float var5 = parent.getRNG().nextFloat() * (float)Math.PI * 2.0F;
                        var4 *= parent.getRNG().nextFloat();
                        mX += Math.cos((double)var5) * (double)var4;
                        mY += (double)((parent.getRNG().nextFloat() - parent.getRNG().nextFloat()) * 0.1F);
                        mZ += Math.sin((double)var5) * (double)var4;

                        RendererHelper.spawnParticle(new ParticleBlood(world, posX, posY + (rand.nextDouble() * 0.2D), posZ, parent.motionX + mX, parent.motionY + mY, parent.motionZ + mZ, parent instanceof EntityPlayer));
                    }
                }

                motionX = motionY = motionZ = 0.0D;
            }
            else
            {
                rotationPitch += pitchSpin;
                rotationYaw += yawSpin;
                pitchSpin *= 0.98F;
                yawSpin *= 0.98F;
            }
            if(MobAmputation.config.bloodSplurt == 1)
            {
                if(rand.nextFloat() < 0.1F)
                {
                    if(MobAmputation.config.blood == 1 && (parent instanceof EntityZombie || parent instanceof EntityPlayer) && parent.isEntityAlive())
                    {

                        double pX = parent.posX;
                        double pY = parent.getEntityBoundingBox().minY + 1.35D;
                        double pZ = parent.posZ;
                        if(type >= 1)
                        {
                            double offset = 0.320D;
                            if(type == 1)
                            {
                                pX += offset * Math.cos(Math.toRadians(parent.renderYawOffset));
                                pZ += offset * Math.sin(Math.toRadians(parent.renderYawOffset));
                            }
                            else
                            {
                                pX -= offset * Math.cos(Math.toRadians(parent.renderYawOffset));
                                pZ -= offset * Math.sin(Math.toRadians(parent.renderYawOffset));
                            }
                        }
                        else if(type == 0)
                        {
                            pY += 0.225D;
                        }

                        for(int k = 0; k < MobAmputation.config.bloodCount / 2; k++)
                        {
                            float var4 = 0.3F;
                            double mX = (double)(-MathHelper.sin(parent.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(parent.rotationPitch / 180.0F * (float)Math.PI) * var4);
                            double mZ = (double)(MathHelper.cos(parent.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(parent.rotationPitch / 180.0F * (float)Math.PI) * var4);
                            double mY = (double)(-MathHelper.sin(parent.rotationPitch / 180.0F * (float)Math.PI) * var4 + 0.1F);
                            var4 = 0.02F;
                            float var5 = parent.getRNG().nextFloat() * (float)Math.PI * 2.0F;
                            var4 *= parent.getRNG().nextFloat();
                            mX += Math.cos((double)var5) * (double)var4;
                            mY += (double)((parent.getRNG().nextFloat() - parent.getRNG().nextFloat()) * 0.1F);
                            mZ += Math.sin((double)var5) * (double)var4;

                            RendererHelper.spawnParticle(new ParticleBlood(world, pX, pY + (rand.nextDouble() * 0.2D), pZ, parent.motionX + mX, parent.motionY + mY, parent.motionZ + mZ, parent instanceof EntityPlayer));
                        }
                    }
                }
            }
        }

        if(!attached && MobAmputation.config.gibPushing == 1)
        {
            List var2 = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().grow(0.15D, 0.0D, 0.15D));
            if(var2 != null && !var2.isEmpty())
            {
                Iterator var10 = var2.iterator();

                while(var10.hasNext())
                {
                    Entity var4 = (Entity)var10.next();

                    if(var4.canBePushed())
                    {
                        var4.applyEntityCollision(this);
                    }
                }
            }
        }

        if(onGround)
        {
            groundTime++;
            if(groundTime > MobAmputation.config.gibGroundTime + 20)
            {
                setDead();
            }
        }
        else if(groundTime > MobAmputation.config.gibGroundTime)
        {
            groundTime--;
        }
        else
        {
            groundTime = 0;
        }
        if(liveTime + MobAmputation.config.gibTime < iChunUtil.eventHandlerClient.ticks)
        {
            setDead();
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource ds, float f)
    {
        if(parent.hurtTime > 0 || ds.getTrueSource() == parent)
        {
            if(ds.getTrueSource() == parent && ds.getImmediateSource() != parent && ds.getImmediateSource() != null)
            {
                ds.getImmediateSource().motionX *= -10;
                ds.getImmediateSource().motionY *= -10;
                ds.getImmediateSource().motionZ *= -10;
                ds.getImmediateSource().rotationYaw += 180.0F;
                ds.getImmediateSource().prevRotationYaw += 180.0F;
            }
            return false;
        }

        if(parent instanceof EntityPlayer && type != 0 || parent instanceof EntitySkeleton && type == 2 && !MobAmputation.eventHandlerClient.serverHasMod || hitTimeout > 0)
        {
            return false;
        }

        if(ds.getImmediateSource() instanceof EntityPlayer && attached && !detach)
        {
            Minecraft.getMinecraft().playerController.attackEntity((EntityPlayer)ds.getImmediateSource(), parent); //TODO follow up with Tinker's Construct's Scythe
            hitTimeout = 10;

            if(parent instanceof EntityPlayer && type == 2 || parent instanceof EntitySkeleton && type == 2 && !MobAmputation.eventHandlerClient.serverHasMod)
            {
                return parent.attackEntityFrom(ds, f);
            }

            ItemStack is = ((EntityPlayer)ds.getImmediateSource()).getHeldItemMainhand();
            if(MobAmputation.config.toolEffect == 0 && rand.nextFloat() > ((float)MobAmputation.config.gibChance / 100F) || MobAmputation.config.toolEffect == 1 && (is.isEmpty() || (is.getItem() instanceof ItemSword || is.getItem() instanceof ItemAxe) && rand.nextDouble() < 0.5D || is.getItem() instanceof ItemPickaxe && rand.nextDouble() < 0.667D || is.getItem() instanceof ItemSpade && rand.nextDouble() < 0.75D))
            {
                return parent.attackEntityFrom(ds, f);
            }

            detach = true;

            float i = 55F * rand.nextFloat() + 20F;
            float j = 55F * rand.nextFloat() + 20F;

            if(rand.nextFloat() < 0.5F)
            {
                i *= -1F;
            }
            if(rand.nextFloat() < 0.5F)
            {
                j *= -1F;
            }

            pitchSpin = i * (float)(parent.motionY + 0.3D);
            yawSpin = j * (float)(Math.sqrt(parent.motionX * parent.motionZ) + 0.3D);

            return parent.attackEntityFrom(ds, f);
        }

        String[] split = MobAmputation.config.projectileList.split(", *");

        HashMap<String, ArrayList<String>> projectileList = new HashMap<String, ArrayList<String>>();

        if(split.length > 0 && !split[0].equalsIgnoreCase(""))
        {
            for(String splits : split)
            {
                String[] split1 = splits.split(": *");
                if(split1.length > 0 && !split1[0].equalsIgnoreCase(""))
                {
                    if(split1.length == 1)
                    {
                        projectileList.put(split1[0], new ArrayList<>());
                    }
                    else
                    {
                        ArrayList<String> ints = new ArrayList<>();
                        for(int i = 1; i < split1.length; i++)
                        {
                            ints.add(split1[i]);
                        }
                        projectileList.put(split1[0], ints);
                    }
                }
            }
        }

        if(!projectileList.containsKey("Arrow"))
        {
            ArrayList<String> s = new ArrayList<>();
            s.add(MobAmputation.config.toolEffect == 1 ? "33" : Integer.toString(MobAmputation.config.gibChance));

            projectileList.put("Arrow", s);
        }

        if(ds.getImmediateSource() != null)
        {
            String key = EntityList.getEntityString(ds.getImmediateSource());

            Entity ent = ds.getImmediateSource();
            for(String clz : projectileList.keySet())
            {
                try
                {
                    Class.forName(clz).isInstance(ent);
                    key = clz;
                    break;
                }
                catch(ClassNotFoundException ignored)
                {
                }
                ;
            }

            if(projectileList.containsKey(key) || ds.isProjectile() && MobAmputation.config.allowProjectileGibbing == 1)
            {
                ArrayList<String> chances = projectileList.get(key);
                int chance = MobAmputation.config.gibChance;
                if(chances != null && !chances.isEmpty())
                {
                    try
                    {
                        chance = Integer.parseInt(chances.get(0));
                    }
                    catch(NumberFormatException e)
                    {
                    }
                }
                float chanceFloat = (float)chance / 100F;
                if(rand.nextFloat() <= chanceFloat)
                {
                    projectile = ds.getImmediateSource();
                    projMotionX = projectile.motionX;
                    projMotionY = projectile.motionY;
                    projMotionZ = projectile.motionZ;
                }
            }
        }

        return true;
    }

    @Override
    public void fall(float dist, float amplifier)
    {
    }

    public boolean canBeCollidedWith()
    {
        return !this.isDead;
    }

    public boolean canBePushed()
    {
        return !this.isDead;
    }

    @Override
    public boolean isEntityAlive()
    {
        return !this.isDead;
    }

    @Override
    protected void entityInit()
    {
    }

    @Override
    public boolean writeToNBTOptional(NBTTagCompound par1NBTTagCompound)
    {
        return false;
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbttagcompound) {}

    @Override
    public void writeEntityToNBT(NBTTagCompound nbttagcompound) {}
}
