package io.github.redrain0o0.legacyskins.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class LegacySkinsLanguageProvider extends FabricLanguageProvider {
	protected LegacySkinsLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
		super(dataOutput, registryLookup);
	}

	protected LegacySkinsLanguageProvider(FabricDataOutput dataOutput, String languageCode, CompletableFuture<HolderLookup.Provider> registryLookup) {
		super(dataOutput, languageCode, registryLookup);
	}

	@Override
	public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder) {
		Path existingFilePath = dataOutput.getModContainer().findPath("assets/legacyskins/lang/en_us.existing.json").get();
		try {
			translationBuilder.add(existingFilePath);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		for (Consumer<TranslationBuilder> translationBuilderConsumer : queued) {
			translationBuilderConsumer.accept(translationBuilder);
		}
	}

	public static final ArrayList<Consumer<TranslationBuilder>> queued = new ArrayList<>();
	public static void addQueuedTranslation(Consumer<TranslationBuilder> builder) {
		queued.add(builder);
	}
}
