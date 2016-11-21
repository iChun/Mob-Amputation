package me.ichun.mods.mobamputation.common.core;

import me.ichun.mods.ichunutil.common.core.network.PacketChannel;
import me.ichun.mods.mobamputation.common.MobAmputation;
import me.ichun.mods.mobamputation.common.packet.PacketDetachLimb;
import net.minecraftforge.common.MinecraftForge;

public class ProxyCommon
{
    public void preInitMod()
    {
        MobAmputation.channel = new PacketChannel(MobAmputation.MOD_NAME, PacketDetachLimb.class);

        MobAmputation.eventHandlerServer = new EventHandlerServer();
        MinecraftForge.EVENT_BUS.register(MobAmputation.eventHandlerServer);
    }
}
