package io.github.redrain0o0.legacyskins.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import io.github.redrain0o0.legacyskins.client.util.SortingOrder;
import io.github.redrain0o0.legacyskins.client.util.SortingOrderCodecs;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class SkinPackFileAutoOrderer {
	public static void main(String[] args) throws Throwable {
		String loc = "file.json";
		boolean devenv = true;
		if (devenv) loc = "run/" + loc;
		String file = Files.readString(Path.of(loc));
		JsonObject jsonObject = new Gson().fromJson(file, JsonObject.class);
		Map<ResourceLocation, SortingOrder<ResourceLocation>> f = new LinkedHashMap<>();
		ResourceLocation before = null;
		for (Map.Entry<String, JsonElement> stringJsonElementEntry : jsonObject.entrySet()) {
			ResourceLocation parse = VersionUtils.parse(stringJsonElementEntry.getKey());
			if (before != null) f.put(parse, SortingOrder.after(before));
			else f.put(parse, SortingOrder.absolute(0));
			before = parse;
		}
		JsonElement jsonElement = Codec.unboundedMap(ResourceLocation.CODEC, SortingOrderCodecs.CODEC).encodeStart(JsonOps.INSTANCE, f).resultOrPartial().orElseThrow();
		System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(jsonElement).replaceAll("\\{\n {4}", "{").replaceAll("\n {2}}", "}"));
	}
}
