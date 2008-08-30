package com.mouseoverstudio.haml4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleScriptContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Haml4jHelper {

	private String text;
	private SimpleScriptContext context;

	private Haml4jHelper(String result) {
		this.text = result;
	}

	public Haml4jHelper(SimpleScriptContext context) {
		this.context = context;
	}

	public static Haml4jHelper replaceVariablesIn(String result) {
		return new Haml4jHelper(result);
	}

	public static Haml4jHelper write(String result) {
		return new Haml4jHelper(result);
	}

	public void in(HttpServletResponse response) throws IOException {
		response.getWriter().write(text);
		response.setContentType("text/html");
	}

	public static List<String> match(String text) {
		Matcher match = matcherFor(text);
		List<String> matches = new ArrayList<String>();
		String group;
		while (match.find()) {
			group = clean(match.group());
			if (!matches.contains(group)) {
				matches.add(group);
			}
		}
		return matches;
	}

	public static Matcher matcherFor(String text) {
		return Pattern.compile("(\\$[a-zA-Z0-9_-]*)").matcher(text);
	}

	public static String clean(String text) {
		return text.substring(1);
	}

	public static Object xInY(String key, HttpServletRequest req) {
		Object value = req.getAttribute(key);
		if (value == null) {
			value = req.getParameter(key);
			if (value == null) {
				value = "";
			}
		}
		return value;
	}

	public static ScriptEngine jrubyEngine() {
		ScriptEngineManager manager = new ScriptEngineManager();
		return manager.getEngineByName("jruby");
	}

	public static Haml4jHelper putIn(SimpleScriptContext context) {
		return new Haml4jHelper(context);
	}

	public Haml4jHelper variablesFrom(String text) {
		this.text = text;
		return this;
	}

	public SimpleScriptContext availableIn(HttpServletRequest request) {
		Object obj;
		for (String match : match(text)) {
			obj = xInY(match, request);
			context.setAttribute(match, obj,
					ScriptContext.ENGINE_SCOPE);
		}
		return context;
	}
}
