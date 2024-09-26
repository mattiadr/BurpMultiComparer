package org.mattiadr.MultiComparer;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;

public class MultiComparer implements BurpExtension {

	MontoyaApi api;

	@Override
	public void initialize(MontoyaApi api) {
		this.api = api;
		api.extension().setName("MultiComparer");

		MultiComparerMenuItemProvider menuItemProvider = new MultiComparerMenuItemProvider(api);
		api.userInterface().registerContextMenuItemsProvider(menuItemProvider);
	}

}
