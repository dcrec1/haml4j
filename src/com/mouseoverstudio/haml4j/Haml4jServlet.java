package com.mouseoverstudio.haml4j;

import static com.mouseoverstudio.haml4j.Haml4jHelper.match;
import static com.mouseoverstudio.haml4j.Haml4jHelper.xInY;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Haml4jServlet extends HttpServlet {
	
	private String viewsRelativePath = "../../";
	
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

	protected void process(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String relativePath = templatePathFrom(req);
		BufferedReader reader = readerFor(relativePath);

		String line;
		StringBuilder sb = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			sb.append(line.concat("\n"));
		}
		
		String fullText = sb.toString();
		
		for (String match : match(fullText)) {
			System.out.println(match);
			fullText.replaceAll(match, xInY(match, req));
		}
		
		
		
//		matcher.

		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("jruby");
		ScriptContext context = engine.getContext();

		context.setAttribute("yaml", fullText, ScriptContext.ENGINE_SCOPE);
		try {
			engine.eval("require 'rubygems'");
			engine.eval("require 'haml'");
			String result = (String) engine
					.eval("Haml::Engine.new($yaml).render");
			resp.getWriter().write(result);
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected BufferedReader readerFor(String path) {
		return new BufferedReader(new InputStreamReader(classLoader()
				.getResourceAsStream(viewsRelativePath + path)));
	}

	protected ClassLoader classLoader() {
		return getClass().getClassLoader();
	}

	// Taken from Freemarker
	protected String templatePathFrom(HttpServletRequest request) {
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
