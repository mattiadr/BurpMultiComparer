package org.mattiadr.MultiComparer;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class MultiComparerMenuItemProvider implements ContextMenuItemsProvider {

	MontoyaApi api;
	Logging logging;

	Map<String, HttpRequestResponse> baseline = null;

	public MultiComparerMenuItemProvider(MontoyaApi api) {
		this.api = api;
		this.logging = api.logging();
	}

	@Override
	public List<Component> provideMenuItems(ContextMenuEvent event) {
		// we only care about request/responses
		if (event.selectedRequestResponses().isEmpty()) {
			return null;
		}

		// initialize menu items
		JMenuItem setAsBaselineMenuItem = new JMenuItem("Set as Baseline");
		setAsBaselineMenuItem.addActionListener(al -> setAsBaseline(event.selectedRequestResponses()));

		JMenuItem compareWithBaselineMenuItem = new JMenuItem("Compare with Baseline");
		compareWithBaselineMenuItem.addActionListener(al -> compareWithBaseline(event.selectedRequestResponses()));

		// can't compare if we don't have a baseline set
		compareWithBaselineMenuItem.setEnabled(baseline != null);

		return Arrays.asList(setAsBaselineMenuItem, compareWithBaselineMenuItem);
	}

	private void setAsBaseline(List<HttpRequestResponse> requestResponseList) {
		AtomicBoolean foundDuplicates = new AtomicBoolean(false);
		baseline = requestResponseList.stream().collect(Collectors.toMap(
				item -> item.request().path(),
				item -> item,
				(a, b) -> {
					foundDuplicates.set(true);
					return a;
				}
		));

		// if duplicates have been found show a warning
		if (foundDuplicates.get()) {
			SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null,
					"Only the first of the same path responses will be used as baseline.",
					"Duplicate paths found.",
					JOptionPane.WARNING_MESSAGE
			));
		}
	}

	private void compareWithBaseline(List<HttpRequestResponse> requestResponseList) {
		// compare responses
		List<ComparisonResult> comparisonResults = requestResponseList.stream().map(requestResponse -> {
			String path = requestResponse.request().path();
			if (!baseline.containsKey(path)) {
				return new ComparisonResult(path, null, requestResponse.response());
			}
			return new ComparisonResult(path, baseline.get(path).response(), requestResponse.response());
		}).toList();

		// display results
		SwingUtilities.invokeLater(() -> new ResultsWindow(api, comparisonResults));
	}

}
