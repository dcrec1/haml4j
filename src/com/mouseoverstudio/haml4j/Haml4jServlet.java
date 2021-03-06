package com.mouseoverstudio.haml4j;

import static com.mouseoverstudio.haml4j.Haml4jHelper.haml4jRubyScript;
import static com.mouseoverstudio.haml4j.Haml4jHelper.jRubyEngine;
import static com.mouseoverstudio.haml4j.Haml4jHelper.newContext;
import static com.mouseoverstudio.haml4j.Haml4jHelper.scriptIn;
import static com.mouseoverstudio.haml4j.Haml4jHelper.templateBindedTo;
import static com.mouseoverstudio.haml4j.Haml4jHelper.urlFrom;
import static com.mouseoverstudio.haml4j.Haml4jHelper.write;
import static com.mouseoverstudio.haml4j.Haml4jHelper.wrongJar;

import java.io.IOException;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Haml4jServlet extends HttpServlet {

	private String viewsRelativePath = "../../";
	private CompiledScript compiledScript;
	
	public void init() {
		final ScriptEngine engine = jRubyEngine();
		try {
			engine.eval(haml4jRubyScript());
			compiledScript = ((Compilable) engine).compile("render()");
		} catch (final Exception e) {
			e.printStackTrace();
			wrongJar();
		}
	}

	public Haml4jServlet() {
	}

	public Haml4jServlet(final String viewsRelativePath) {
		this.viewsRelativePath = viewsRelativePath;
	}

	@Override
	public void doGet(final HttpServletRequest req,
			final HttpServletResponse resp) throws IOException {
		process(req, resp);
	}

	public void process(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {
		final String templateUrl = viewsRelativePath
				+ templateBindedTo(request);
		final SimpleScriptContext context = newContext().withVariablesFrom(
				scriptIn(templateUrl)).availableIn(request);
		context.setAttribute("file", urlFrom(templateUrl),
				ScriptContext.ENGINE_SCOPE);
		try {
			if (compiledScript == null) {
				this.init();
			}
			final String result = (String) compiledScript.eval(context);
			write(result).in(response);
		} catch (final ScriptException e) {
			wrongJar();
		}
	}

}
