package io.github.redrain0o0.legacyskins.schema;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LegacySkinsDataFixerTest {

	@Test
	void dataFix() throws Throwable{

		Assertions.assertThrows(UnsupportedOperationException.class, () -> {
			Dynamic<JsonElement> jsonElementDynamic = new Dynamic<>(JsonOps.INSTANCE, load("999.json"));
			LegacySkinsDataFixer.fix(jsonElementDynamic);
		});
		{
			Dynamic<JsonElement> jsonElementDynamic = new Dynamic<>(JsonOps.INSTANCE, load("1001.json"));
			jsonElementDynamic = LegacySkinsDataFixer.fix(jsonElementDynamic);
			Assertions.assertEquals("default", jsonElementDynamic.get("skinsScreen").asString().result().orElse(null));
			Assertions.assertFalse(jsonElementDynamic.getValue().getAsJsonObject().has("skin"));
			Assertions.assertTrue(jsonElementDynamic.getValue().getAsJsonObject().has("currentSkin"));
		}
		{
			Dynamic<JsonElement> jsonElementDynamic = new Dynamic<>(JsonOps.INSTANCE, load("1001-without-skin.json"));
			jsonElementDynamic = LegacySkinsDataFixer.fix(jsonElementDynamic);
			Assertions.assertFalse(jsonElementDynamic.getValue().getAsJsonObject().has("skin"));
			Assertions.assertFalse(jsonElementDynamic.getValue().getAsJsonObject().has("currentSkin"));
		}
	}

	private JsonElement load(String str) throws Throwable {
		Gson gson = new Gson();
		String s = new String(LegacySkinsDataFixerTest.class.getResourceAsStream("/datafix/" + str).readAllBytes());
		return gson.fromJson(s, JsonElement.class);
	}
}
