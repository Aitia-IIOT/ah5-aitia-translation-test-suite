package ai.aitia.arrowhead.dto;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class TestValueList {

	//=================================================================================================
	// members

	@JacksonXmlElementWrapper(localName = "TestElements")
	@JacksonXmlProperty(localName = "TestElement")
	private List<TestElement> testElements;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public TestValueList() {
	}
	
	//-------------------------------------------------------------------------------------------------
	public TestValueList(final List<TestElement> testElements) {
		this.testElements = testElements;
	}

	//-------------------------------------------------------------------------------------------------
	public List<TestElement> getTestElements() {
		return testElements;
	}

	//-------------------------------------------------------------------------------------------------
	public void setTestElements(final List<TestElement> testElements) {
		this.testElements = testElements;
	}
}