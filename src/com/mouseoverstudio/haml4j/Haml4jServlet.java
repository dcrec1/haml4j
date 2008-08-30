package com.mouseoverstudio.haml4j;

import static com.mouseoverstudio.haml4j.Haml4jHelper.jrubyEngine;
import static com.mouseoverstudio.haml4j.Haml4jHelper.putIn;
import static com.mouseoverstudio.haml4j.Haml4jHelper.write;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Haml4jServlet extends HttpServlet {

	private String viewsRelativePath = "../../";
	private ScriptEngine engine;
	private Map<String, String> cache;

	@Override
	public void init() {
		cache = new HashMap<String, String>();
		engine = jrubyEngine();
		try {
			engine.eval("require 'rubygems'");
			engine.eval("require 'haml'");
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Haml4jServlet() {
	}

	public Haml4jServlet(String viewsRelativePath) {
		this.viewsRelativePath = viewsRelativePath;
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		process(req, resp);
	}

	protected void process(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String template = templateBindedTo(request);
		String scriptText = cache.get(template);
		if (scriptText == null) {
			scriptText = textFrom(template);
			cache.put(template, scriptText);
		}
		SimpleScriptContext scriptContext = new SimpleScriptContext();
		scriptContext.setAttribute("haml", scriptText,
				SimpleScriptContext.ENGINE_SCOPE);
		putIn(scriptContext).variablesFrom(scriptText).availableIn(request);
		try {
			String result = (String) engine.eval(
					"Haml::Engine.new($haml).render", scriptContext);
			write(result).in(response);
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String textFrom(String template) throws IOException {
		String line;
		BufferedReader reader = readerFor(template);
		StringBuilder sb = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			sb.append(line.concat("\n"));
		}
		return sb.toString();
	}

	private BufferedReader readerFor(String path) {
		return new BufferedReader(new InputStreamReader(classLoader()
				.getResourceAsStream(viewsRelativePath + path)));
	}

	private ClassLoader classLoader() {
		return getClass().getClassLoader();
	}

	// Taken from Freemarker
	protected String templateBindedTo(HttpServletRequest request) {
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

}
