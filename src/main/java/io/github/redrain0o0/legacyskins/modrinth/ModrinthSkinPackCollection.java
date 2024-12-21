package io.github.redrain0o0.legacyskins.modrinth;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import io.github.redrain0o0.legacyskins.Legacyskins;
import io.github.redrain0o0.legacyskins.modrinth.data.ModrinthDataObjects;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModrinthSkinPackCollection {
	private static final String userAgent = /*$ userAgent {*/"RedRainOoO/legacy-skins/1.3.0-beta.3+fabric+1.21"/*$}*/;
	private static final String collectionUrl = "https://api.modrinth.com/v3/collection/bJ8YVFtd";
	private static final String projectsUrl = "https://api.modrinth.com/v3/projects";
	private static final String teamsUrl = "https://api.modrinth.com/v3/teams";
	private static final String organizationUrl = "https://api.modrinth.com/v3/organizations";
	private static final String projectVersionsUrl = "https://api.modrinth.com/v3/project/%s/version";
	private static final Gson GSON = new Gson();
	static final HttpClient client = HttpClient.newBuilder().build();
	public static void main(String[] args) throws IOException, InterruptedException {
		// {"id":"bJ8YVFtd","user":"6NJak7g0","name":"Skin Packs","description":"Skin Packs that work with Legacy Skins","icon_url":null,"color":null,"status":"listed","created":"2024-12-11T00:06:03.043533Z","updated":"2024-12-11T00:09:07.280128Z","projects":["8fM4nRG5","IU7pUM86","M4781Cfl","btzz80tF"]}
		// https://api.modrinth.com/v3/collection/bJ8YVFtd
		ModrinthDataObjects.Collection join = loadCollection().join();
		System.out.println(join);
		List<ModrinthDataObjects.Project> join1 = loadProjects(join).join();
		System.out.println(join1);
		System.out.println(getOwnersOfProjects(join1).join());
		System.out.println(getVersionsOfProjects(join1).join());
	}

	public static CompletableFuture<ModrinthDataObjects.Collection> loadCollection() {
		return client.sendAsync(builder().GET().uri(URI.create(collectionUrl)).build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)).thenApply(response -> {
			String body = response.body();
			JsonElement element = GSON.fromJson(body, JsonElement.class);
			return ModrinthDataObjects.Collection.CODEC.parse(JsonOps.INSTANCE, element).resultOrPartial(Legacyskins.LOGGER::error).orElseThrow();
		});
	}

	public static CompletableFuture<List<ModrinthDataObjects.Project>> loadProjects(ModrinthDataObjects.Collection collection) {
		return client.sendAsync(builder().GET().uri(URI.create(projectsUrl + "?ids=" + URLEncoder.encode("[" + collection.projects().stream().map(a -> "\"" + a.str() + "\"").collect(Collectors.joining(",")) + "]", StandardCharsets.UTF_8))).build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)).thenApply(response -> {
			String body = response.body();
			JsonElement element = GSON.fromJson(body, JsonElement.class);
			return ModrinthDataObjects.Project.CODEC.listOf().parse(JsonOps.INSTANCE, element).resultOrPartial(Legacyskins.LOGGER::error).orElseThrow();
		});
	}

	public static CompletableFuture<List<List<ModrinthDataObjects.TeamMember>>> getTeamMembers(List<ModrinthDataObjects.Project> projects) {
		return client.sendAsync(builder().GET().uri(URI.create(teamsUrl + "?ids=" + URLEncoder.encode("[" + projects.stream().map(ModrinthDataObjects.Project::teamId).map(a -> "\"" + a.str() + "\"").collect(Collectors.joining(",")) + "]", StandardCharsets.UTF_8))).build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)).thenApply(response -> {
			// we end up with a list of a list
			String body = response.body();
			JsonElement element = GSON.fromJson(body, JsonElement.class);
			return ModrinthDataObjects.TeamMember.CODEC.listOf().listOf().parse(JsonOps.INSTANCE, element).resultOrPartial(Legacyskins.LOGGER::error).orElseThrow();
		});
	}

	public static CompletableFuture<List<ModrinthDataObjects.Organization>> getOrganizations(List<ModrinthDataObjects.Project> projects) {
		return client.sendAsync(builder().GET().uri(URI.create(organizationUrl + "?ids=" + URLEncoder.encode("[" + projects.stream().map(ModrinthDataObjects.Project::organization).filter(Optional::isPresent).map(a -> "\"" + a.get().str() + "\"").collect(Collectors.joining(",")) + "]", StandardCharsets.UTF_8))).build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)).thenApply(response -> {
			// we end up with a list of a list
			String body = response.body();
			JsonElement element = GSON.fromJson(body, JsonElement.class);
			return ModrinthDataObjects.Organization.CODEC.listOf().parse(JsonOps.INSTANCE, element).resultOrPartial(Legacyskins.LOGGER::error).orElseThrow();
		});
	}

	public static CompletableFuture<List<ModrinthDataObjects.Version>> getProjectVersions(ModrinthDataObjects.Project project) {
		return client.sendAsync(builder().GET().uri(URI.create(projectVersionsUrl.formatted(project.id().str()))).build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)).thenApply(response -> {
			// we end up with a list of a list
			String body = response.body();
			JsonElement element = GSON.fromJson(body, JsonElement.class);
			return ModrinthDataObjects.Version.CODEC.listOf().parse(JsonOps.INSTANCE, element).resultOrPartial(Legacyskins.LOGGER::error).orElseThrow();
		});
	}

	public static CompletableFuture<Map<ModrinthDataObjects.Project, List<ModrinthDataObjects.Version>>> getVersionsOfProjects(List<ModrinthDataObjects.Project> projects) {
		Map<ModrinthDataObjects.Project, CompletableFuture<List<ModrinthDataObjects.Version>>> completableFutures = new HashMap<>();
		for (ModrinthDataObjects.Project project : projects) {
			completableFutures.put(project, getProjectVersions(project));
		}
		return all(completableFutures);
	}

	// modified solution of https://stackoverflow.com/a/63540731
	private static <R, T> CompletableFuture<Map<R, T>> all(Map<R, CompletableFuture<T>> futures) {
		CompletableFuture<Void> cfv = CompletableFuture.allOf(futures.values().toArray(CompletableFuture[]::new));
		return cfv.thenApply(future -> {
			HashMap<R, T> map = new HashMap<>();
			Stream<Pair<R, T>> pairStream = futures.entrySet().stream().map(f -> Pair.of(f.getKey(), f.getValue().join()));
			pairStream.forEachOrdered(a -> map.put(a.getFirst(), a.getSecond()));
			return map;
		});
	}

	static HttpRequest.Builder builder() {
		return builder(true);
	}

	static HttpRequest.Builder builder(boolean autoAuth) {
		HttpRequest.Builder header = HttpRequest.newBuilder().header("User-Agent", userAgent);
		if (!autoAuth || !ModrinthOauth.isAuthenticated()) return header;
		else return header.header("Authorization", ModrinthOauth.auth.token());
	}

	// get a list of authors from a list of projects, either the owner of a team, or the name of an organization.
	public static CompletableFuture<Map<ModrinthDataObjects.Project, String>> getOwnersOfProjects(List<ModrinthDataObjects.Project> projects) {
		List<ModrinthDataObjects.Project> projectsWithOrganization = projects.stream().filter(a -> a.organization().isPresent()).toList();
		List<ModrinthDataObjects.Project> projectsWithoutOrganization = projects.stream().filter(a -> a.organization().isEmpty()).toList();
		return getTeamMembers(projectsWithoutOrganization).thenCombineAsync(getOrganizations(projectsWithOrganization), (a, b) -> {
			HashMap<ModrinthDataObjects.Project, String> map = new HashMap<>();
			for (ModrinthDataObjects.Project project : projectsWithOrganization) {
				ModrinthDataObjects.Organization organization = b.stream().filter(c -> project.organization().get().equals(c.id())).findFirst().orElseThrow();
				map.put(project, organization.name());
			}
			for (ModrinthDataObjects.Project project : projectsWithoutOrganization) {
				List<ModrinthDataObjects.TeamMember> team = a.stream().filter(c -> project.teamId().equals(c.get(0).teamId())).findFirst().orElseThrow();
				map.put(project, team.stream().filter(ModrinthDataObjects.TeamMember::isOwner).findFirst().orElseThrow().user().username());
			}
			return map;
		});
	}

	private static final String userUrl = "https://api.modrinth.com/v3/user";
	public static CompletableFuture<ModrinthDataObjects.User> getSignedInUser() {
		return client.sendAsync(builder().GET().uri(URI.create(userUrl)).build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)).thenApply(response -> {
			// we end up with a list of a list
			String body = response.body();
			JsonElement element = new Gson().fromJson(body, JsonElement.class);
			return ModrinthDataObjects.User.CODEC.parse(JsonOps.INSTANCE, element).resultOrPartial(Legacyskins.LOGGER::error).orElseThrow();
		});
	}

	public static CompletableFuture<Pair<ModrinthDataObjects.VersionFile, Path>> downloadFile(ModrinthDataObjects.VersionFile file, Path to) {
		return client.sendAsync(builder().GET().uri(URI.create(file.url())).build(), HttpResponse.BodyHandlers.ofFile(to)).thenApply(vf -> {
			// TODO: this doesn't work
//			try {
//				Path body = vf.body();
//				HashCode hashCode = Hashing.sha512().hashBytes(Files.readAllBytes(body));
//				if (!hashCode.equals(HashCode.fromBytes(file.hashes().sha512()))) {
//					Files.delete(body);
//					throw new IllegalStateException("Hashes for " + file.url() + " don't match!");
//				}
//			} catch (Throwable t) {
//				throw new RuntimeException("An error occured while downloading " + file.url() + ".", t);
//			}
			return Pair.of(file, to);
		});
	}
}
