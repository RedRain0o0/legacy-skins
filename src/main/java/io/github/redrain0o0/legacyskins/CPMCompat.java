package io.github.redrain0o0.legacyskins;

import com.tom.cpm.api.ICPMPlugin;
import com.tom.cpm.api.IClientAPI;
import com.tom.cpm.api.ICommonAPI;

public class CPMCompat implements ICPMPlugin {

	@Override
	public void initClient(IClientAPI api) {

	}

	@Override
	public void initCommon(ICommonAPI api) {

	}

	@Override
	public String getOwnerModId() {
		return "legacyskins";
	}
}
