package me.ichun.mods.mobamputation.common;

import me.ichun.mods.ichunutil.common.core.config.ConfigHandler;
import me.ichun.mods.ichunutil.common.core.network.PacketChannel;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.module.update.UpdateChecker;
import me.ichun.mods.mobamputation.client.core.EventHandlerClient;
import me.ichun.mods.mobamputation.common.core.Config;
import me.ichun.mods.mobamputation.common.core.EventHandlerServer;
import me.ichun.mods.mobamputation.common.core.ProxyCommon;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;

@Mod(modid = MobAmputation.MOD_ID, name = MobAmputation.MOD_NAME,
        version = MobAmputation.VERSION,
        guiFactory = "me.ichun.mods.ichunutil.common.core.config.GenericModGuiFactory",
        dependencies = "required-after:ichunutil@[" + iChunUtil.VERSION_MAJOR +".0.0," + (iChunUtil.VERSION_MAJOR + 1) + ".0.0)"
)
public class MobAmputation
{
    public static final String VERSION = iChunUtil.VERSION_MAJOR + ".0.0";
    public static final String MOD_ID = "mobamputation";
    public static final String MOD_NAME = "MobAmputation";

    @Mod.Instance(MOD_ID)
    public static MobAmputation instance;

    @SidedProxy(clientSide = "me.ichun.mods.mobamputation.client.core.ProxyClient", serverSide = "me.ichun.mods.mobamputation.common.core.ProxyCommon")
    public static ProxyCommon proxy;

    public static Config config;

    public static PacketChannel channel;

    public static EventHandlerClient eventHandlerClient;
    public static EventHandlerServer eventHandlerServer;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        config = ConfigHandler.registerConfig(new Config(event.getSuggestedConfigurationFile()));

        proxy.preInitMod();

        UpdateChecker.registerMod(new UpdateChecker.ModVersionInfo(MOD_NAME, iChunUtil.VERSION_OF_MC, VERSION, false));
    }

    @Mod.EventHandler
    public void serverStopping(FMLServerStoppingEvent event)
    {
        eventHandlerServer.headlessHarmer.clear();
        eventHandlerServer.headlessTime.clear();
    }
}
