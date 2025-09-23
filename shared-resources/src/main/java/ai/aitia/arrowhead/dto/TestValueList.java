/*******************************************************************************
 *
 * Copyright (c) 2025 AITIA
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 *
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *  	AITIA
 *
 *******************************************************************************/
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