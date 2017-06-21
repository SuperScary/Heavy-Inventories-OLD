package superscary.heavyinventories;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import superscary.heavyinventories.server.config.WeightsConfig;
import superscary.heavyinventories.server.player.network.PlayerUpdate;
import superscary.heavyinventories.server.player.network.PlayerUpdateRequest;
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

    private static SimpleNetworkWrapper networkWrapper;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        Generator.Info.create(Constants.class, event);
        WeightsConfig.init(event.getModConfigurationDirectory());
        this.setupNetwork(event);

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

    private void setupNetwork(FMLPreInitializationEvent event)
    {
        networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
        int id = 0;

        networkWrapper.registerMessage(PlayerUpdateRequest.Handler.class, PlayerUpdateRequest.class, id++, Side.SERVER);
        networkWrapper.registerMessage(PlayerUpdate.Handler.class, PlayerUpdate.class, id++, Side.CLIENT);
    }

    public static SimpleNetworkWrapper getNetwork()
    {
        return networkWrapper;
    }

}
