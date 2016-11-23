package me.ichun.mods.mobamputation.common.core;

import me.ichun.mods.ichunutil.common.core.config.ConfigBase;
import me.ichun.mods.ichunutil.common.core.config.annotations.ConfigProp;
import me.ichun.mods.ichunutil.common.core.config.annotations.IntBool;
import me.ichun.mods.ichunutil.common.core.config.annotations.IntMinMax;
import me.ichun.mods.mobamputation.common.MobAmputation;

import java.io.File;

public class Config extends ConfigBase
{
    @ConfigProp(category = "clientOnly")
    @IntMinMax(min = 0)
    public int gibTime = 1000;

    @ConfigProp(category = "clientOnly")
    @IntMinMax(min = 0)
    public int gibGroundTime = 100;

    @ConfigProp(category = "clientOnly")
    @IntBool
    public int blood = 1;

    @ConfigProp(category = "clientOnly")
    @IntMinMax(min = 1, max = 1000)
    public int bloodCount = 20;

    @ConfigProp(category = "clientOnly")
    @IntBool
    public int bloodSplurt = 1;

    @ConfigProp(category = "clientOnly")
    @IntBool
    public int greenBlood = 0;

    @ConfigProp(category = "clientOnly")
    @IntBool
    public int playerGibs = 1;

    @ConfigProp(category = "clientOnly")
    @IntBool
    public int gibPushing = 1;

    @ConfigProp(category = "gameplay", useSession = true)
    @IntBool
    public int headlessDeath = 1;

    @ConfigProp(category = "gameplay", useSession = true)
    @IntBool
    public int toolEffect = 1;

    @ConfigProp(category = "gameplay", useSession = true)
    @IntMinMax(min = 0, max = 100)
    public int gibChance = 100;

    @ConfigProp(category = "gameplay", useSession = true)
    public String projectileList = "";

    @ConfigProp(category = "gameplay", useSession = true)
    @IntBool
    public int allowProjectileGibbing = 1;


    public Config(File file)
    {
        super(file);
    }

    @Override
    public String getModId()
    {
        return MobAmputation.MOD_ID;
    }

    @Override
    public String getModName()
    {
        return "Mob Amputation";
    }

    @Override
    public void storeSession()
    {
        super.storeSession();
        if(hasSetup())
        {
            MobAmputation.eventHandlerClient.serverHasMod = false;
            MobAmputation.eventHandlerClient.amputationMap.clear();
            MobAmputation.eventHandlerClient.fishHooks.clear();
        }
    }

    @Override
    public void onReceiveSession()
    {
        MobAmputation.eventHandlerClient.serverHasMod = true;
    }
}
