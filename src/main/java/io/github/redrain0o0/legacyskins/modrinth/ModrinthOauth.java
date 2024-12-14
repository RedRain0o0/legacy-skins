package io.github.redrain0o0.legacyskins.modrinth;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sun.net.httpserver.HttpServer;
import io.github.redrain0o0.legacyskins.modrinth.data.JavaCodecs;
import io.github.redrain0o0.legacyskins.modrinth.data.ModrinthDataObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import static io.github.redrain0o0.legacyskins.modrinth.ModrinthSkinPackCollection.builder;
import static io.github.redrain0o0.legacyskins.modrinth.ModrinthSkinPackCollection.client;

public class ModrinthOauth {
	public static final String OAUTH_SECRET = "RAwYwN9XnHUwkIZXfv5WhxLwspQhN1Im";
	public static final String OAUTH_URL = "https://modrinth.com/auth/authorize?client_id=U16bR0EJ&redirect_uri=http://localhost:11443&scope=USER_READ+PROJECT_READ+COLLECTION_READ+ORGANIZATION_READ";

	private static final Logger LOGGER = LoggerFactory.getLogger("legacyskins-oauth");
	private static boolean serverActive = false;

	public static Runnable serverStopper = () -> {};
	public static ModrinthAuthentication auth;

	public static BiConsumer<Status, String> callbackInfo = (a, b) -> {};

	public static void main(String[] args) {
		enableOauthServer();
	}
	public static void enableOauthServer() {
		if (serverActive) return;
		serverActive = true;

		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					run0();
				} catch (Throwable e) {
					throw new RuntimeException(e);
				}
			}

			private void run0() throws Throwable {
				this.setName("Legacy Skins Modrinth OAuth");
				HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 11443), 0);
				server.createContext("/", exchange -> {
					LOGGER.debug("Received at URL: " + exchange.getRequestURI());
					LOGGER.debug("Received something? " + new String(exchange.getRequestBody().readAllBytes()));
					String query = exchange.getRequestURI().getQuery();
					LOGGER.info(query);
					if (query.contains("code=") && query.split("=").length == 2) {
						String code = query.split("=")[1];
						LOGGER.debug("Modrinth has responded with a code: " + code);
						LOGGER.debug("Attempting to get token...");
						callbackInfo.accept(Status.AUTH_TEMP_CODE_RECEIVED, "Received a single-use authentication code.");
						try {
							ModrinthDataObjects.OauthTokenResponse join = getToken(new ModrinthDataObjects.OauthTokenPostDto(
									code,
									"U16bR0EJ",
									"http://localhost:11443",
									"authorization_code"
							)).join();
							auth = new ModrinthAuthentication(join);
							byte[] bytes = "Successfully authenticated via Modrinth, you can close this tab now.".getBytes(StandardCharsets.UTF_8);
							exchange.sendResponseHeaders(200, bytes.length);
							exchange.getResponseBody().write(bytes);
							exchange.close();
							serverStopper.run();
							callbackInfo.accept(Status.AUTH_SUCCESS, "Authentication Successful");
						} catch (Throwable t) {
							callbackInfo.accept(Status.AUTH_FAILURE, "Authentication Failed");
							byte[] bytes = "Failed to authenticate via Modrinth".getBytes(StandardCharsets.UTF_8);
							exchange.sendResponseHeaders(500, bytes.length);
							exchange.getResponseBody().write(bytes);
							exchange.close();
						}
					}
				});
				server.start();
				callbackInfo.accept(Status.SERVER_STARTED, "Server Started");
				serverStopper = () -> {
					server.stop(0);
					callbackInfo.accept(Status.SERVER_CLOSED, "Server Closed");
					serverStopper = () -> {}; // prevent memory leak
				};
			}


		};
		thread.setDaemon(false);
		thread.start();

	}

	private static final String TOKEN_URL = "https://api.modrinth.com/_internal/oauth/token";
	public static CompletableFuture<ModrinthDataObjects.OauthTokenResponse> getToken(ModrinthDataObjects.OauthTokenPostDto dto) {
		String formText = "code=%s&client_id=%s&redirect_uri=%s&grant_type=%s".formatted(URLEncoder.encode(dto.code(), StandardCharsets.UTF_8), URLEncoder.encode(dto.clientId(), StandardCharsets.UTF_8), URLEncoder.encode(dto.redirectUri(), StandardCharsets.UTF_8), URLEncoder.encode(dto.grantType(), StandardCharsets.UTF_8));
		System.out.println(formText);
		return client.sendAsync(builder(false).header("Content-Type", "application/x-www-form-urlencoded").header("Authorization", OAUTH_SECRET).POST(HttpRequest.BodyPublishers.ofString(formText, StandardCharsets.UTF_8 /*TODO*/)).uri(URI.create(TOKEN_URL)).build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)).thenApply(response -> {
			// we end up with a list of a list
			String body = response.body();
			LOGGER.debug(body);
			JsonElement element = new Gson().fromJson(body, JsonElement.class);
			return ModrinthDataObjects.OauthTokenResponse.CODEC.parse(JsonOps.INSTANCE, element).resultOrPartial(LOGGER::error).orElseThrow();
		});
	}

	public record ModrinthAuthentication(String token, Instant expiry) {
		public ModrinthAuthentication(ModrinthDataObjects.OauthTokenResponse response) {
			this(response.accessToken(), Instant.now().plusSeconds(response.expiresIn() - 60 /* Account for 60000ms ping */));
		}
		public static final Codec<ModrinthAuthentication> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.STRING.fieldOf("token").forGetter(ModrinthAuthentication::token),
				JavaCodecs.INSTANT.fieldOf("expiry").forGetter(ModrinthAuthentication::expiry)
		).apply(instance, ModrinthAuthentication::new));
	}

	public static boolean isAuthenticated() {
		return auth != null && Instant.now().isBefore(auth.expiry);
	}

	public enum Status {
		SERVER_STARTED,
		AUTH_TEMP_CODE_RECEIVED,
		AUTH_SUCCESS,
		AUTH_FAILURE,
		SERVER_CLOSED
	}
}
