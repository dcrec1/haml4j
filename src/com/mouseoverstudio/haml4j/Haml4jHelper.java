package com.mouseoverstudio.haml4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

import org.apache.log4j.Logger;

public class Haml4jHelper {

	private String text;
	private SimpleScriptContext context;

	private Haml4jHelper(String result) {
		this.text = result;
	}

	public static BufferedReader readerFor(String path) {
		return new BufferedReader(new InputStreamReader(classLoader()
				.getResourceAsStream(path)));
	}

	public static String scriptIn(String template) throws IOException {
		String line;
		BufferedReader reader = readerFor(template);
		StringBuilder sb = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			sb.append(line.concat("\n"));
		}
		return sb.toString();
	}

	public static String haml4jRubyScript() throws IOException {
		return scriptIn("com/mouseoverstudio/haml4j/haml4j.rb");
	}

	public static String urlFrom(String resource) {
		return classLoader().getResource(resource).toString().substring(5);
	}

	public static ClassLoader classLoader() {
		return new Haml4jHelper().getClass().getClassLoader();
	}

	public Haml4jHelper(SimpleScriptContext context) {
		this.context = context;
	}

	public Haml4jHelper() {
		// TODO Auto-generated constructor stub
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
		return Pattern.compile("(\\$[a-zA-Z0-9_-]+)").matcher(text);
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

	public static ScriptEngine jRubyEngine() {
		ScriptEngineManager manager = new ScriptEngineManager();
		return manager.getEngineByName("jruby");
	}

	public static Haml4jHelper newContext() {
		return new Haml4jHelper(new SimpleScriptContext());
	}

	public Haml4jHelper withVariablesFrom(String text) {
		this.text = text;
		return this;
	}

	public SimpleScriptContext availableIn(HttpServletRequest request) {
		Object obj;
		for (String match : match(text)) {
			obj = xInY(match, request);
			Logger.getLogger(this.getClass()).debug(
					"setting attribute in context: " + match);
			context.setAttribute(match, obj, ScriptContext.ENGINE_SCOPE);
		}
		return context;
	}

	// Taken from Freemarker
	public static String templateBindedTo(HttpServletRequest request) {
		// First, see if it is an included request
		String includeServletPath = (String) request
				.getAttribute("javax.servlet.include.servlet_path");
		if (includeServletPath != null) {
			// Try path info; only if that's null (servlet is mapped to an
			// URL extension instead of to prefix) use servlet path.
			String includePathInfo = (String) request
					.getAttribute("javax.servlet.include.path_info");
			return includePathInfo == null ? includeServletPath
					: includePathInfo;
		}
		// Seems that the servlet was not called as the result of a
		// RequestDispatcher.include(...). Try pathInfo then servletPath again,
		// only now directly on the request object:
		String path = request.getPathInfo();
		if (path != null)
			return path;
		path = request.getServletPath();
		if (path != null)
			return path;
		// Seems that it is a servlet mapped with prefix, and there was no extra
		// path info.
		return "";
	}

	public static void wrongJar() {
		String message = "Haml4j was packaged with problems, unable to start";
		Logger.getLogger("com.mouseoverstudio.haml4j.Haml4jHelper").fatal(
				message);
	}
}
