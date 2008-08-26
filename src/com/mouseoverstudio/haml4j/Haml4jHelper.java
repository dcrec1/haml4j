package com.mouseoverstudio.haml4j;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.servlet.http.HttpServletRequest;

public class Haml4jHelper {
	
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
		Pattern pattern = Pattern.compile("(\\$\\{[a-zA-Z0-9_-]*\\})");
		return pattern.matcher(text);
	}
	
	public static String clean(String text) {
		return text.substring(2, text.length() - 1);
	}
	
	public static String xInY(String key, HttpServletRequest req) {
		String value = (String) req.getAttribute(key);
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

}
