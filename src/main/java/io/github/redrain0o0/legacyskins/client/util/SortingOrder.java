package io.github.redrain0o0.legacyskins.client.util;

import io.github.redrain0o0.legacyskins.util.PlatformUtils;
//? if >=1.21 {
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
//?}
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

// either absolute, or relative
public class SortingOrder<T> {
	public static <T> SortingOrder<T> absolute(double pos) {
		return new Absolute<>(pos);
	}
	public static <T> SortingOrder<T> after(T t) {
		return new After<>(t);
	}
	public static <T> SortingOrder<T> before(T t) {
		return new Before<>(t);
	}

	@Deprecated
	public static <T> SortingOrder<T> between(T t0, T t1) {
		return new Between<>(t0, t1);
	}

	public static final Logger LOGGER = LoggerFactory.getLogger("legacyskins-sorter");

	public static <T> List<T> sorted(List<T> input, Map<T, SortingOrder<T>> sortingOrder) {
		if (!(input instanceof ArrayList<T>)) input = new ArrayList<>(input);
		Grabber<T> grabber = grabber(input, sortingOrder);
		List<T> output = new ArrayList<>();
		input.stream().filter(grabber::isAbsolute).sorted(Comparator.comparingDouble(grabber::getAbsolute)).forEachOrdered(output::add);
		input.removeIf(grabber::isAbsolute);
		List<T> operatingOn = input;
		while(!operatingOn.isEmpty()) {
			LOGGER.debug("S{}", operatingOn);
			List<T> unfulfilled = new ArrayList<>();
			for (T t : operatingOn) {
				LOGGER.debug("B{}", output);
				SortingOrder<T> tSortingOrder = grabber.fromT(t);
				if (tSortingOrder instanceof SortingOrder.After<T> after) {
					if (output.contains(after.t)) output.add(output.indexOf(after.t) + 1, t);
					else unfulfilled.add(t);
				} else if (tSortingOrder instanceof SortingOrder.Before<T> before) {
					if (output.contains(before.t)) output.add(output.indexOf(before.t), t);
					else unfulfilled.add(t);
				}
				LOGGER.debug("A{}", output);
			}
			if (unfulfilled.size() == operatingOn.size()) {
				if (!PlatformUtils.isDevelopmentEnvironment()) throw new IllegalStateException("Infinite loop while sorting! %s".formatted(operatingOn));
				LOGGER.error("Infinite loop while sorting!");
				LOGGER.error("The following elements are not able to be sorted: {}", operatingOn);
				LOGGER.error("Adding the rest of the elements to the end of the list.");
				output.addAll(unfulfilled);
				break;
			}
			operatingOn = unfulfilled;
		}
		return output;
	}

	//?if >=1.21 {
	// For testing
	public static void main(String[] args) {
		List<String> f = new ArrayList<>(List.of("A", "B", "C", "D", "E", "F", "G", "H", "I"));
		Map<String, SortingOrder<String>> map = Map.of(
				"A", absolute(-2),
				"B", after("C"),
				"C", after("D"),
				"D", after("A"),
				"G", after("E"),
				"H", absolute(-5),
				"I", after("E")
		);
		System.out.println(sorted(f, map));

		JsonElement jsonElementDataResult = SortingOrderCodecs.CODEC.encodeStart(JsonOps.INSTANCE, absolute(50)).resultOrPartial().get();
		System.out.println(jsonElementDataResult);

		jsonElementDataResult = SortingOrderCodecs.CODEC.encodeStart(JsonOps.INSTANCE, after(ResourceLocation.parse("hola:hola"))).resultOrPartial().get();
		System.out.println(jsonElementDataResult);
	}
	//?}

	private interface Grabber<T> {
		boolean isAbsolute(T t);
		double getAbsolute(T t);
		SortingOrder<T> fromT(T t);
	}
	private static <T> Grabber<T> grabber(List<T> input, Map<T, SortingOrder<T>> sortingOrder) {
		return new Grabber<>() {
			public SortingOrder<T> fromT(T t) {
				return sortingOrder.getOrDefault(t, SortingOrder.absolute(0));
			}
			@Override
			public boolean isAbsolute(T t) {
				return fromT(t) instanceof SortingOrder.Absolute<T>;
			}

			public double getAbsolute(T t) {
				return ((Absolute<T>) fromT(t)).pos;
			}
		};
	}

	static class Absolute<T> extends SortingOrder<T> {
		private final double pos;
		Absolute(double pos) {
			this.pos = pos;
		}

		// for method reference
		double pos() {
			return pos;
		}
	}

	static class After<T> extends SortingOrder<T> {
		private final T t;
		After(T t) {
			this.t = t;
		}

		T t() {
			return t;
		}
	}
	static class Before<T> extends SortingOrder<T> {
		private final T t;
		Before(T t) {
			this.t = t;
		}

		T t() {
			return t;
		}
	}

	@Deprecated
	static class Between<T> extends SortingOrder<T> {
		private final T t0, t1;
		Between(T t0, T t1) {
			this.t0 = t0;
			this.t1 = t1;
		}
	}
}
