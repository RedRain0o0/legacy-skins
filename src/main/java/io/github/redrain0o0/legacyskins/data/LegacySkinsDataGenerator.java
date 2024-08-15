package io.github.redrain0o0.legacyskins.data;

import io.github.redrain0o0.legacyskins.Legacyskins;
import io.github.redrain0o0.legacyskins.client.LegacySkin;
import io.github.redrain0o0.legacyskins.client.LegacySkinPack;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class LegacySkinsDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		FabricDataGenerator.Pack pack = generator.createPack();
		pack.addProvider((DataProvider.Factory<DataProvider>) packOutput -> new LegacyPackProvider((FabricDataOutput) packOutput) {
			@Override
			public void addPacks() {
				// Temp pack
				{
					List<String> models = List.of(
							"alex",
							"athletealex",
							"athletesteve",
							"boxeralex",
							"boxersteve",
							"cyclistalex",
							"cycliststeve",
							"devalex",
							"devsteve",
							"prisoneralex",
							"prisonersteve",
							"scottishsteve",
							"steve",
							"swedishalex",
							"tennisalex",
							"tennissteve",
							"tuxedoalex",
							"tuxedosteve"
					);
					LegacySkinPack legacySkinPack = new LegacySkinPack(ResourceLocation.fromNamespaceAndPath(Legacyskins.MOD_ID, "textures/skin_packs/default.png"),
							models.stream().map(a -> new LegacySkin(ResourceLocation.fromNamespaceAndPath(Legacyskins.MOD_ID, "skinpacks/default/%s.cpmmodel".formatted(a)))).toList());
					this.addPack("temporary", legacySkinPack);
				}
			}
		});
	}
}
