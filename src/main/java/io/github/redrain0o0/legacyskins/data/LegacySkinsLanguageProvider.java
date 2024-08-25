package io.github.redrain0o0.legacyskins.data;

import io.github.redrain0o0.legacyskins.util.PlatformUtils;


import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.function.Consumer;
//? if fabric {
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;
//?} else if neoforge {
/*import net.neoforged.neoforge.common.data.LanguageProvider;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.data.PackOutput;
import java.io.Reader;
import java.nio.file.Files;
*///?}

//? if fabric
public class LegacySkinsLanguageProvider extends FabricLanguageProvider {
//? if neoforge
/*public class LegacySkinsLanguageProvider extends LanguageProvider {*/
	//? if fabric {
	protected LegacySkinsLanguageProvider(FabricDataOutput dataOutput /*? if >=1.20.6 {*/, CompletableFuture<HolderLookup.Provider> registryLookup /*?}*/) {
		super(dataOutput /*? if >=1.20.6 {*/, registryLookup /*?}*/);
	}

	protected LegacySkinsLanguageProvider(FabricDataOutput dataOutput, String languageCode /*? if >=1.20.6 {*/, CompletableFuture<HolderLookup.Provider> registryLookup /*?}*/) {
		super(dataOutput, languageCode /*? if >=1.20.6 {*/, registryLookup /*?}*/);
	}

	//?} else if neoforge {
	/*protected LegacySkinsLanguageProvider(PackOutput dataOutput) {
		super(dataOutput, "legacyskins", "en_us");
	}
	*///?}

	@Override
	//? if fabric {
	public void generateTranslations(/*? if >=1.20.6 {*/HolderLookup.Provider registryLookup, /*?}*/ TranslationBuilder translationBuilder) {
	//?} elif neoforge
	/*public void addTranslations() {*/
		Path existingFilePath = PlatformUtils.findInMod("assets/legacyskins/lang/en_us.existing.json");
		try {
			//? if fabric {
			translationBuilder.add(existingFilePath);
			//?} else if neoforge {
			/*try (Reader reader = Files.newBufferedReader(existingFilePath)) {
				JsonObject translations = JsonParser.parseReader(reader).getAsJsonObject();

				for (String key : translations.keySet()) {
					add(key, translations.get(key).getAsString());
				}
			}
			*///?}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		//? if !fabric {
		/*DeferredTranslation deferredTranslation = new DeferredTranslation() {
			@Override
			public void add(String key, String value) {
				LegacySkinsLanguageProvider.this.add(key, value);
			}
		};
		*///?}
		for (Consumer</*? if fabric {*/TranslationBuilder/*?} else {*//*DeferredTranslation*//*?}*/> translationBuilderConsumer : queued) {
			translationBuilderConsumer.accept(/*? if fabric {*/translationBuilder/*?} else {*//*deferredTranslation*//*?}*/);
		}
	}

	public static final ArrayList<Consumer</*? if fabric {*/TranslationBuilder/*?} else {*//*DeferredTranslation*//*?}*/>> queued = new ArrayList<>();
	public static void addQueuedTranslation(Consumer</*? if fabric {*/TranslationBuilder/*?} else {*//*DeferredTranslation*//*?}*/> builder) {
		queued.add(builder);
	}

	//? if !fabric {
	/*public interface DeferredTranslation {
		void add(String key, String value);
	}
	*///?}
}