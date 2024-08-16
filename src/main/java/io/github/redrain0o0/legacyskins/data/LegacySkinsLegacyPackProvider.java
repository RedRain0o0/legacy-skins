package io.github.redrain0o0.legacyskins.data;

import io.github.redrain0o0.legacyskins.Legacyskins;
import io.github.redrain0o0.legacyskins.client.LegacySkin;
import io.github.redrain0o0.legacyskins.client.LegacySkinPack;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.concurrent.CompletableFuture;

// For API users, use LegacyPackProvider!
public final class LegacySkinsLegacyPackProvider extends LegacyPackProvider {
	public LegacySkinsLegacyPackProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
		super(dataOutput, registryLookup);
	}

	@Override
	public void addPacks(PackBuilder builder) {
		// Default pack
		{
			LegacySkinPack pack = new LegacySkinPack(ResourceLocation.fromNamespaceAndPath(Legacyskins.MOD_ID, "textures/skin_packs/default.png"), List.of(new LegacySkin(ResourceLocation.fromNamespaceAndPath(Legacyskins.MOD_ID, "skinpacks/default/b.cpmmodel"))));
			builder.addPack("default", pack);
		}
		// Temp pack
		{
			List<String> models = List.of(
					"steve",
					"tennissteve",
					"tuxedosteve",
					"athletesteve",
					"scottishsteve",
					"prisonersteve",
					"cycliststeve",
					"boxersteve",
					"alex",
					"tennisalex",
					"tuxedoalex",
					"athletealex",
					"swedishalex",
					"prisoneralex",
					"cyclistalex",
					"boxeralex",
					"devsteve",
					"devalex"
			);
			LegacySkinPack legacySkinPack = new LegacySkinPack(ResourceLocation.fromNamespaceAndPath(Legacyskins.MOD_ID, "null.png"),
					models.stream().map(a -> new LegacySkin(ResourceLocation.fromNamespaceAndPath(Legacyskins.MOD_ID, "skinpacks/temporary/%s.cpmmodel".formatted(a)))).toList());
			builder.addPack("temporary", legacySkinPack);
		}
	}
}
