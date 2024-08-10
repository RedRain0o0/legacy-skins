package io.github.redrain0o0.legacyskins.client;

import com.google.gson.JsonObject;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import wily.legacy.Legacy4J;
import wily.legacy.util.JsonUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public record LegacySkinPack(Component buttonName, ResourceLocation icon, ResourceLocation skins) {
    public static final List<LegacySkinPack> list = new ArrayList<>();
    private static final String PACKS = "skin_packs.json";
    public static class Manager extends SimplePreparableReloadListener<List<LegacySkinPack>> {
        @Override
        protected List<LegacySkinPack> prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
            List<LegacySkinPack> packs = new ArrayList<>();
            JsonUtil.getOrderedNamespaces(resourceManager).forEach(name->{
                resourceManager.getResource(ResourceLocation.tryBuild(name, PACKS)).ifPresent(r->{
                    try {
                        BufferedReader bufferedReader = r.openAsReader();
                        JsonObject obj = GsonHelper.parse(bufferedReader);
                        obj.asMap().forEach((s,element)->{
                            if (element instanceof JsonObject tabObj) {
                                packs.add(new LegacySkinPack(Component.translatable(s),ResourceLocation.parse(GsonHelper.getAsString(tabObj,"icon")),ResourceLocation.parse(GsonHelper.getAsString(tabObj,"skins"))));
                            }
                        });
                        bufferedReader.close();
                    } catch (IOException var8) {
                        Legacy4J.LOGGER.warn(var8.getMessage());
                    }
                });
            });
            return packs;
        }

        @Override
        protected void apply(List<LegacySkinPack> object, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
            list.clear();
            list.addAll(object);;
        }
    }
}
