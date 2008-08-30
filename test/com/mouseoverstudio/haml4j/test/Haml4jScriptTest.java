package com.mouseoverstudio.haml4j.test;

import static com.mouseoverstudio.haml4j.Haml4jHelper.haml4jRubyScript;
import static com.mouseoverstudio.haml4j.Haml4jHelper.jRubyEngine;

import java.io.FileNotFoundException;
import java.util.HashMap;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
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
			//context.setAttribute("cache", new HashMap<String, Object>(), ScriptContext.GLOBAL_SCOPE);
			String call = "render('" + fullPathOf(template) + "')";
			engine.eval(call, context);
			engine.eval(call, context);
			engine.eval(call, context);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String fullPathOf(String template) {
		return getClass().getClassLoader().getResource(template).toString()
				.substring(5);
	}

}
