package me.ichun.mods.mobamputation.common.packet;

import io.netty.buffer.ByteBuf;
import me.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import me.ichun.mods.mobamputation.client.entity.EntityGib;
import me.ichun.mods.mobamputation.common.MobAmputation;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAttackRangedBow;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketDetachLimb extends AbstractPacket
{
    public int entId;
    public int limbType;
    public boolean nonPlayer;

    public PacketDetachLimb() {}

    public PacketDetachLimb(int id, int type, boolean nonPlayer1)
    {
        entId = id;
        limbType = type;
        nonPlayer = nonPlayer1;
    }

    @Override
    public void writeTo(ByteBuf buffer)
    {
        buffer.writeInt(entId);
        buffer.writeInt(limbType);
        buffer.writeBoolean(nonPlayer);
    }

    @Override
    public void readFrom(ByteBuf buffer)
    {
        entId = buffer.readInt();
        limbType = buffer.readInt();
        nonPlayer = buffer.readBoolean();
    }

    @Override
    public AbstractPacket execute(Side side, EntityPlayer player)
    {
        if(side.isServer())
        {
            if(!nonPlayer)
            {
                MobAmputation.channel.sendToDimension(this, player.dimension);
            }

            Entity ent = player.worldObj.getEntityByID(entId);
            if(MobAmputation.config.headlessDeath == 1 && limbType == 0)
            {
                if(ent instanceof EntityLivingBase && !(ent instanceof EntityPlayer))
                {
                    EntityLivingBase living = (EntityLivingBase)ent;
                    MobAmputation.eventHandlerServer.headlessTime.put(living, 40 + living.getRNG().nextInt(60));
                    MobAmputation.eventHandlerServer.headlessHarmer.put(living, player);
                }
            }
            if(limbType == 2 && ent instanceof EntitySkeleton)
            {
                EntitySkeleton skele = (EntitySkeleton)ent;
                EntityAIAttackRangedBow aiArrowAttack = null;

                for(EntityAITasks.EntityAITaskEntry entry : skele.tasks.taskEntries)
                {
                    if(entry.priority == 4 && entry.action instanceof EntityAIAttackRangedBow)
                    {
                        aiArrowAttack = (EntityAIAttackRangedBow)entry.action;
                        break;
                    }
                }
                if(aiArrowAttack != null)
                {
                    skele.tasks.removeTask(aiArrowAttack);
                }

                skele.tasks.addTask(4, new EntityAIAttackMelee(skele, 1.2D, false));
            }
        }
        else
        {
            handleClient();
        }
        return null;
    }

    @Override
    public Side receivingSide()
    {
        return null;
    }

    @SideOnly(Side.CLIENT)
    public void handleClient()
    {
        EntityGib[] gibs = MobAmputation.eventHandlerClient.amputationMap.get(Minecraft.getMinecraft().theWorld.getEntityByID(entId));
        if(gibs != null)
        {
            EntityGib gib = gibs[limbType];
            if(gib.attached)
            {
                gib.detach = true;
            }
        }
    }
}
