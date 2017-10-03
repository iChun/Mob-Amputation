package me.ichun.mods.mobamputation.common.core;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class EventHandlerServer
{
    @SubscribeEvent
    public void worldTick(TickEvent.ServerTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END)
        {
            Iterator<Entry<EntityLivingBase, Integer>> iterator = headlessTime.entrySet().iterator();
            while(iterator.hasNext())
            {
                Entry<EntityLivingBase, Integer> e = iterator.next();
                e.setValue(e.getValue() - 1);
                if(e.getValue() <= 0)
                {
                    e.getKey().attackEntityFrom(headlessHarmer.get(e.getKey()) == null ? DamageSource.GENERIC : DamageSource.causePlayerDamage(headlessHarmer.get(e.getKey())), 200);
                    headlessHarmer.remove(e.getKey());
                    iterator.remove();
                }
            }
        }
    }

    public HashMap<EntityLivingBase, Integer> headlessTime = new HashMap<>();
    public HashMap<EntityLivingBase, EntityPlayer> headlessHarmer = new HashMap<>();
}
