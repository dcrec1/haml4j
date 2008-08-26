package com.mouseoverstudio.haml4j.test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

import com.mouseoverstudio.haml4j.Haml4jServlet;

public class Haml4jServletTest {

	private HttpServletRequest request;
	private HttpServletResponse response;
	private Haml4jServlet servlet;
	private MockedPrintWriter writer;

	@Before
	public void setUp() throws Exception {
		request = createNiceMock(HttpServletRequest.class);
		response = createMock(HttpServletResponse.class);
		writer = new MockedPrintWriter();
		servlet = new Haml4jServlet("resources/");
		expect(response.getWriter()).andReturn(writer);
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

}
