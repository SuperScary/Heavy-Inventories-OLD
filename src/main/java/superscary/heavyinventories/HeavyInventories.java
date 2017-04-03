package superscary.heavyinventories;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import superscary.heavyinventories.server.config.WeightsConfig;
import superscary.heavyinventories.util.Constants;
import superscary.supercore.info.Generator;
import superscary.supercore.proxy.IProxy;

import static superscary.heavyinventories.util.Constants.*;

@SuppressWarnings("unused")
@Mod(modid = MODID, version = VERSION, name = NAME, dependencies = "required-after:supercore@[1.0,)")
public class HeavyInventories
{

    @SidedProxy(serverSide = PROXY_SERVER, clientSide = PROXY_CLIENT)
    public static IProxy proxy;

    @Mod.Instance
    public static HeavyInventories instance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        Generator.Info.create(Constants.class, event);
        WeightsConfig.init(event.getModConfigurationDirectory());

        proxy.preInit();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        proxy.postInit();
    }

}
