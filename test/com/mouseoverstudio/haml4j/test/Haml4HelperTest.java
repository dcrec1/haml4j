package com.mouseoverstudio.haml4j.test;

import static com.mouseoverstudio.haml4j.Haml4jHelper.clean;
import static com.mouseoverstudio.haml4j.Haml4jHelper.match;
import static com.mouseoverstudio.haml4j.Haml4jHelper.matcherFor;
import static com.mouseoverstudio.haml4j.Haml4jHelper.xInY;
import static junit.framework.Assert.assertEquals;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.util.List;
import java.util.regex.Matcher;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;

public class Haml4HelperTest {

	private HttpServletRequest request;

	@Test
	public void matchMultiline() {
		String text = "aaa${a}\nfdf${bc}fefe\n${def}zzz";
		Matcher match = matcherFor(text);
		match.find();
		assertEquals("${a}", match.group());
		match.find();
		assertEquals("${bc}", match.group());
		match.find();
		assertEquals("${def}", match.group());
	}

	@Test
	public void matchTwoInALine() {
		String text = ",,,${abc},,,,${cba}...";
		Matcher match = matcherFor(text);
		match.find();
		assertEquals("${abc}", match.group());
		match.find();
		assertEquals("${cba}", match.group());
	}

	@Test
	public void matchCleaning() {
		assertEquals("abc123", clean("${abc123}"));
	}

	@Test
	public void matches() {
		List<String> matches = match("fewfewg${abc}\ngeg${cba},,,${abc}");
		assertEquals(2, matches.size());
		assertEquals("abc", matches.get(0));
		assertEquals("cba", matches.get(1));
	}

	@Test
	public void valueInRequestAttribute() {
		String key = "432";
		String value = "shake it shake it";
		prepareRequest();
		expect(request.getAttribute(key)).andReturn(value);
		replay(request);
		assertEquals(value, (String) xInY(key, request));
	}
	
	@Test
	public void valueInRequestParameter() {
		String key = "hey_jo";
		String value = "whats up";
		prepareRequest();
		expect(request.getParameter(key)).andReturn(value);
		replay(request);
		assertEquals(value, xInY(key, request));
	}

	public void prepareRequest() {
		request = createNiceMock(HttpServletRequest.class);
	}

}
