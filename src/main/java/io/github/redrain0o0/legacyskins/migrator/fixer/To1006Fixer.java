package io.github.redrain0o0.legacyskins.migrator.fixer;

import com.mojang.serialization.Dynamic;


import net.minecraft.client.Minecraft;
import net.minecraft.core.UUIDUtil;
import java.util.Optional;
import java.util.UUID;

//? if <=1.20.4 && >1.20.2 {
/*import net.minecraft.util.JavaOps;
*/
//?} elif >1.20.4 {
import com.mojang.serialization.JavaOps;

//?} else
/*import com.mojang.serialization.JsonOps;*/
import io.github.redrain0o0.legacyskins.Legacyskins;

// Nothing to do here, just a new value "nonLegacy4J" is available in the config file.
public class To1006Fixer extends Fixer {

	public To1006Fixer() {
		super(1006);
	}

	@Override
	public <T> Dynamic<T> fix(Dynamic<T> element) {
		return element;
	}
}
