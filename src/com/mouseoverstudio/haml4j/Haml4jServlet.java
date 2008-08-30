package com.mouseoverstudio.haml4j;

import static com.mouseoverstudio.haml4j.Haml4jHelper.haml4jRubyScript;
import static com.mouseoverstudio.haml4j.Haml4jHelper.jRubyEngine;
import static com.mouseoverstudio.haml4j.Haml4jHelper.newContext;
import static com.mouseoverstudio.haml4j.Haml4jHelper.templateBindedTo;
import static com.mouseoverstudio.haml4j.Haml4jHelper.write;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

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

	@Override
	public void init() {
		engine = jRubyEngine();
		try {
			engine.eval(haml4jRubyScript());
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
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
		SimpleScriptContext context = newContext().withVariablesFrom(
				scriptIn(template)).availableIn(request);
		try {
			String call = "render('" + fullPathOf(template) + "')";
			String result = (String) engine.eval(call, context);
			write(result).in(response);
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String scriptIn(String template) throws IOException {
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

	private String fullPathOf(String file) {
		return classLoader().getResource(viewsRelativePath + file).toString()
				.substring(5);
	}

	private ClassLoader classLoader() {
		return getClass().getClassLoader();
	}

}
