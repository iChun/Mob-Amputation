package me.ichun.mods.mobamputation.client.core;

import me.ichun.mods.mobamputation.client.entity.EntityGib;
import me.ichun.mods.mobamputation.client.render.RenderGib;
import me.ichun.mods.mobamputation.common.MobAmputation;
import me.ichun.mods.mobamputation.common.core.ProxyCommon;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ProxyClient extends ProxyCommon
{
    @Override
    public void preInitMod()
    {
        super.preInitMod();

        RenderingRegistry.registerEntityRenderingHandler(EntityGib.class, new RenderGib.RenderFactory());

        MobAmputation.eventHandlerClient = new EventHandlerClient();
        MinecraftForge.EVENT_BUS.register(MobAmputation.eventHandlerClient);
    }
}
