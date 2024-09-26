package org.mattiadr.MultiComparer;


import burp.api.montoya.http.message.responses.HttpResponse;

import java.awt.*;
import java.util.Objects;

public class ComparisonResult {

	public static Color GREEN = new Color(204, 255, 153);
	public static Color ORANGE = new Color(255, 204, 153);
	public static Color RED = new Color(255, 153, 153);

	public String path;
	public boolean foundBaseline;
	public boolean codeMatches;
	public String code;
	public boolean lengthMatches;
	public String length;
	public boolean responseMatches;
	public HttpResponse baselineResponse;
	public HttpResponse comparedResponse;

	public ComparisonResult(String path, HttpResponse baselineResponse, HttpResponse comparedResponse) {
		this.path = path;
		this.baselineResponse = baselineResponse;
		this.comparedResponse = comparedResponse;
		this.foundBaseline = baselineResponse != null;

		if (foundBaseline) {
			codeMatches = baselineResponse.statusCode() == comparedResponse.statusCode();
			lengthMatches = baselineResponse.body().length() == comparedResponse.body().length();
			responseMatches = Objects.equals(baselineResponse.bodyToString(), comparedResponse.bodyToString());
			code = codeMatches
					? String.valueOf(comparedResponse.statusCode())
					: (baselineResponse.statusCode() + " -> " + comparedResponse.statusCode());
			length = lengthMatches
					? String.valueOf(comparedResponse.body().length())
					: (baselineResponse.body().length() + " -> " + comparedResponse.body().length());
		} else {
			code = String.valueOf(comparedResponse.statusCode());
			length = String.valueOf(comparedResponse.body().length());
			responseMatches = false;
		}
	}

	public Color getCodeColor() {
		return foundBaseline ? codeMatches ? GREEN : ORANGE : RED;
	}

	public Color getLengthColor() {
		return foundBaseline ? lengthMatches ? GREEN : ORANGE : RED;
	}

	public Color getResponseColor() {
		return foundBaseline ? responseMatches ? GREEN : ORANGE : RED;
	}

}
