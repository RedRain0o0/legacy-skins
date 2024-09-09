//? if fabric {
/*package io.github.redrain0o0.legacyskins.migrator;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import io.github.redrain0o0.legacyskins.Legacyskins;
import io.github.redrain0o0.legacyskins.migrator.fixer.Fixer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class MigratorTest {

	@Test
	void dataFix() throws Throwable{

		Assertions.assertThrows(UnsupportedOperationException.class, () -> {
			Dynamic<JsonElement> jsonElementDynamic = new Dynamic<>(JsonOps.INSTANCE, load("999.json"));
			Migrator.CONFIG_FIXER.fix(jsonElementDynamic);
		});
		{
			Dynamic<JsonElement> jsonElementDynamic = new Dynamic<>(JsonOps.INSTANCE, load("1001.json"));
			jsonElementDynamic = Migrator.CONFIG_FIXER.fix(jsonElementDynamic);
			Assertions.assertEquals("default", jsonElementDynamic.get("skinsScreen").asString().result().orElse(null));
			Assertions.assertFalse(jsonElementDynamic.getValue().getAsJsonObject().has("skin"));
			Assertions.assertTrue(jsonElementDynamic.getValue().getAsJsonObject().has("currentSkin"));
			Assertions.assertTrue(jsonElementDynamic.getValue().getAsJsonObject().has("showSkinEditorButton"));
		}
		{
			Dynamic<JsonElement> jsonElementDynamic = new Dynamic<>(JsonOps.INSTANCE, load("1001-without-skin.json"));
			jsonElementDynamic = Migrator.CONFIG_FIXER.fix(jsonElementDynamic);
			Assertions.assertFalse(jsonElementDynamic.getValue().getAsJsonObject().has("skin"));
			Assertions.assertFalse(jsonElementDynamic.getValue().getAsJsonObject().has("currentSkin"));
			Assertions.assertTrue(jsonElementDynamic.getValue().getAsJsonObject().has("showSkinEditorButton"));
		}
		{
			Dynamic<JsonElement> jsonElementDynamic = new Dynamic<>(JsonOps.INSTANCE, load("1002.json"));
			jsonElementDynamic = Migrator.CONFIG_FIXER.fix(jsonElementDynamic);
			Assertions.assertTrue(jsonElementDynamic.getValue().getAsJsonObject().has("showSkinEditorButton"));
		}
		{
			Dynamic<JsonElement> jsonElementDynamic = new Dynamic<>(JsonOps.INSTANCE, load("2mig.json"));
			jsonElementDynamic = Migrators.migrator.fix(jsonElementDynamic);
			Assertions.assertEquals("1", jsonElementDynamic.get("value2").asString().resultOrPartial(Legacyskins.LOGGER::error).orElseThrow());
			Assertions.assertEquals("2", jsonElementDynamic.get("value1").asString().resultOrPartial(Legacyskins.LOGGER::error).orElseThrow());
		}
	}

	private JsonElement load(String str) throws Throwable {
		Gson gson = new Gson();
		String s = new String(MigratorTest.class.getResourceAsStream("/datafix/" + str).readAllBytes());
		return gson.fromJson(s, JsonElement.class);
	}

	private static class Migrators {
		public static final Migrator migrator = new Migrator(List.of(new S1(), new S2()), 1, 1, 3);
		private static class S1 extends Fixer {

			public S1() {
				super(2);
			}

			@Override
			public <T> Dynamic<T> fix(Dynamic<T> element) {
				return renameField(renameField(element, "value1", "newValue1"), "value2", "newValue2");
			}
		}
		private static class S2 extends Fixer {

			public S2() {
				super(3);
			}

			@Override
			public <T> Dynamic<T> fix(Dynamic<T> element) {
				return renameField(renameField(element, "newValue1", "value2"), "newValue2", "value1");
			}
		}
	}
}
*///?}