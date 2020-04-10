package astavie.spellcrafting;

import astavie.spellcrafting.apiimpl.SpellcraftingAPI;
import astavie.spellcrafting.client.render.RendererManager;
import astavie.spellcrafting.common.caster.CasterProvider;
import astavie.spellcrafting.common.item.ItemSpellTest;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Spellcrafting.MODID)
@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.FORGE)
public class Spellcrafting {

    public static final String MODID = "spellcrafting";

    public Spellcrafting() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);

        SpellcraftingAPI.deliver();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(CasterProvider.class);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        RendererManager.register();
    }

    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {

        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> event) {
        }

        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> event) {
            event.getRegistry().register(new ItemSpellTest().setRegistryName(MODID, "spell_test"));
        }

    }

}
