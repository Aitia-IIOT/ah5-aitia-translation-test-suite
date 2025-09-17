package ai.aitia.arrowhead.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

public class TestElement {
	
	//=================================================================================================
	// members

	@JacksonXmlProperty(isAttribute = true)
	private String key;

	@JacksonXmlText
	private String value;
	
	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	public TestElement() {
	}
	
	//-------------------------------------------------------------------------------------------------
	public TestElement(final String key, final String value) {
		this.key = key;
		this.value = value;
	}

	//-------------------------------------------------------------------------------------------------
	public String getKey() {
		return key;
	}

	//-------------------------------------------------------------------------------------------------
	public void setKey(final String key) {
		this.key = key;
	}

	//-------------------------------------------------------------------------------------------------
	public String getValue() {
		return value;
	}

	//-------------------------------------------------------------------------------------------------
	public void setValue(final String value) {
		this.value = value;
	}
}