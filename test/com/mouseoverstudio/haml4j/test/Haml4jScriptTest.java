package com.mouseoverstudio.haml4j.test;

import static com.mouseoverstudio.haml4j.Haml4jHelper.haml4jRubyScript;
import static com.mouseoverstudio.haml4j.Haml4jHelper.jRubyEngine;
import static com.mouseoverstudio.haml4j.Haml4jHelper.urlFrom;
import static org.junit.Assert.fail;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.SimpleScriptContext;

import org.junit.Test;

public class Haml4jScriptTest {

	@Test
	public void testCache() {
		ScriptEngine engine = jRubyEngine();
		try {
			engine.eval(haml4jRubyScript());
			String template = "resources/simple.yaml";
			SimpleScriptContext context = new SimpleScriptContext();
			context.setAttribute("file", urlFrom(template), ScriptContext.ENGINE_SCOPE);
			String call = "render()";
			engine.eval(call, context);
			engine.eval(call, context);
			engine.eval(call, context);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

}
