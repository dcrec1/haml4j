package com.mouseoverstudio.haml4j.test;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class MockedPrintWriter extends PrintWriter {
	
	private String text;

	public MockedPrintWriter() throws FileNotFoundException {
		super("temp");
	}
	
	@Override
	public void write(String text) {
		this.text = text;
	}
	
	public String getWritted() {
		return this.text;
	}

}
