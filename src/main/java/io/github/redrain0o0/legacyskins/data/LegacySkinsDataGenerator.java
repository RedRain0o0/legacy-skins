package io.github.redrain0o0.legacyskins.data;

//? if neoforge {
/*import io.github.redrain0o0.legacyskins.Legacyskins;

import net.neoforged.bus.api.SubscribeEvent;
//? if >=1.20.6 {
/^import net.neoforged.fml.common.EventBusSubscriber;
^///?} else {
import net.neoforged.fml.common.Mod.EventBusSubscriber;
//?}
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataGenerator;

import java.io.IOException;
*///?} else if fabric {
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
//?}

//? if neoforge
/*@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = Legacyskins.MOD_ID)*/
public class LegacySkinsDataGenerator {
	//? if fabric {
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		FabricDataGenerator.Pack pack = generator.createPack();
		pack.addProvider(LegacySkinsLegacyPackProvider::new);
		pack.addProvider(/*? if <=1.20.4 {*//*(FabricDataGenerator.Pack.Factory<LegacySkinsLanguageProvider>) *//*?}*/ LegacySkinsLanguageProvider::new);
		pack.addProvider((FabricDataGenerator.Pack.Factory<LegacySkinsMcmetaProvider>) LegacySkinsMcmetaProvider::new);
	}
	//?} else if neoforge {
	/*@SubscribeEvent
	public static void event(GatherDataEvent event) throws IOException {
		DataGenerator generator = event.getGenerator();
		generator.addProvider(true, (DataProvider.Factory<LegacySkinsLegacyPackProvider>) p -> new LegacySkinsLegacyPackProvider(p, event.getLookupProvider()));
		generator.addProvider(true, (DataProvider.Factory<LegacySkinsLanguageProvider>) LegacySkinsLanguageProvider::new);
		generator.addProvider(true, (DataProvider.Factory<LegacySkinsMcmetaProvider>) LegacySkinsMcmetaProvider::new);
	}
	*///?}
}
