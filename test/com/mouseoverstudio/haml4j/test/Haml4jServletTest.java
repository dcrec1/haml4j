package com.mouseoverstudio.haml4j.test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import resources.Entry;
import resources.MockedPrintWriter;

import com.mouseoverstudio.haml4j.Haml4jServlet;

public class Haml4jServletTest {

	private HttpServletRequest request;
	private HttpServletResponse response;
	private static Haml4jServlet servlet;
	private MockedPrintWriter writer;

	@BeforeClass
	public static void init() throws ServletException {
		servlet = new Haml4jServlet("resources/");
		servlet.init();
	}

	@Before
	public void setUp() throws Exception {
		request = createNiceMock(HttpServletRequest.class);
		response = createMock(HttpServletResponse.class);
		writer = new MockedPrintWriter();
		expect(response.getWriter()).andReturn(writer);
		response.setContentType("text/html");
		expectLastCall().once();
	}

	@Test
	public void simpleLayout() {
		withResource("simple.yaml");
		shouldReturn("<p>Hello World!</p>\n");
	}

	@Test
	public void multilineLayout() {
		withResource("multiline.yaml");
		shouldReturn("<ul>\n  <li>Salt</li>\n  <li>Pepper</li>\n</ul>\n");
	}

	@Test
	public void multilineLayoutWithVariables() {
		withResource("multiline_with_variables.yaml");
		expect(request.getAttribute("xpto")).andReturn("Not Salt");
		expect(request.getParameter("otpx")).andReturn("Not Pepper");
		shouldReturn("<ul>\n  <li>Not Salt</li>\n  <li>Not Pepper</li>\n</ul>\n");
	}

	@Test
	public void complexLayout() throws IOException {
		withResource("complex.yaml");
		expect(request.getAttribute("title")).andReturn("The title");
		expect(request.getAttribute("header1")).andReturn("The header 1");
		expect(request.getAttribute("header2")).andReturn("The header 2");
		List<Entry> entries = new ArrayList<Entry>();
		entries.add(new Entry("Title 1", new Date(123456789), "Body 1"));
		entries.add(new Entry("Title 2", new Date(123456789), "Body 2"));
		entries.add(new Entry("Title 3", new Date(123456789), "Body 3"));
		expect(request.getAttribute("entries")).andReturn(entries);
		shouldReturn(textFrom("complex.html"));
	}

	protected void withResource(String resource) {
		expect(request.getAttribute("javax.servlet.include.servlet_path"))
				.andReturn(resource);
		expect(request.getAttribute("javax.servlet.include.path_info"))
				.andReturn(null);
	}

	protected void shouldReturn(String result) {
		replay(request);
		replay(response);
		try {
			servlet.doGet(request, response);
			assertEquals(result, writer.getWritted());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	private String textFrom(String template) throws IOException {
		String line;
		BufferedReader reader = readerFor(template);
		StringBuilder sb = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			sb.append(line.concat("\n"));
		}
		return sb.toString();
	}

	private BufferedReader readerFor(String path) {
		return new BufferedReader(new InputStreamReader(getClass()
				.getClassLoader().getResourceAsStream("resources/" + path)));
	}
}
