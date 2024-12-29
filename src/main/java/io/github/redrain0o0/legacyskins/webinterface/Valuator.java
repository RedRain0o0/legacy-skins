package io.github.redrain0o0.legacyskins.webinterface;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpServer;

import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class Valuator {
	public static final ConcurrentHashMap<String, Consumer<Double>> applier = new ConcurrentHashMap<>();
	public static void startOnNewThread() {
		if (true) return; // does not work well with data generation
		new Thread(wrap(Valuator::start)).start();
	}

	public static void main(String[] args) {
		startOnNewThread();
	}

	private static Runnable wrap(UncheckedRunnable runnable) {
		return () -> {
			try {
				runnable.run();
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		};
	}

	private interface UncheckedRunnable {
		void run() throws Throwable;
	}

	public static void start() throws Throwable {
		HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 51648), 0);
		String depos = """
				<!DOCTYPE html>
				<html>
				<body>
				<input type="text" id="key0"/><input type="text" id="value0"/><button onclick="furegu(0)">Fire!</button>
				<br><input type="text" id="key1"/><input type="text" id="value1"/><button onclick="furegu(1)">Fire!</button>
				<br><input type="text" id="key2"/><input type="text" id="value2"/><button onclick="furegu(2)">Fire!</button>
				<script>
				function furegu(val) {
				(async () => {
				   var obj = {};
				   obj[document.getElementById("key" + val).value] = document.getElementById("value" + val).value;
				  const rawResponse = await fetch('http://localhost:51648/send', {
				    method: 'POST',
				    headers: {
				      'Accept': 'application/json',
				      'Content-Type': 'application/json'
				    },
				    body: JSON.stringify(obj)
				  });
				  const content = await rawResponse.text();
				
				  console.log(content);
				})();
				}</script></body></html>
				""";
		byte[] bytes = depos.getBytes(StandardCharsets.UTF_8);
		int length = bytes.length;
		server.createContext("/", exchange -> {
			exchange.sendResponseHeaders(200, length);
			exchange.getResponseBody().write(bytes);
			exchange.close();
		});
		server.createContext("/send", exchange -> {
			if (!exchange.getRequestMethod().equals("POST")) {
				exchange.sendResponseHeaders(404, 0);
				exchange.close();
			}
			String s = new String(exchange.getRequestBody().readAllBytes());
			JsonObject jsonObject = new Gson().fromJson(s, JsonObject.class);
			for (Map.Entry<String, JsonElement> stringJsonElementEntry : jsonObject.asMap().entrySet()) {
				System.out.println(stringJsonElementEntry.getKey() + ", " + stringJsonElementEntry.getValue().getAsDouble());
				if (applier.containsKey(stringJsonElementEntry.getKey())) {
					System.out.println("Applying valuator");
					applier.get(stringJsonElementEntry.getKey()).accept(stringJsonElementEntry.getValue().getAsDouble());
				}
			}
			exchange.sendResponseHeaders(200, 0);
			exchange.close();
		});

		server.start();
	}
}
