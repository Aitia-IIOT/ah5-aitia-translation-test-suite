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
package ai.aitia.arrowhead.djxt;

public final class DummyJsonXmlTranslatorConstants {

	//=================================================================================================
	// members
	
	public static final String HTTP_API_DM_TRANSLATION_PATH = "/data-model/translation";
	public static final String HTTP_API_OP_INIT_PATH = "/init";
	public static final String HTTP_API_OP_GET_PATH = "/get";
	public static final String HTTP_API_OP_ABORT_PATH = "/abort";
	
	public static final String TASK_QUEUE = "taskQueue";
	public static final String TASK_STORE = "taskStore";

	//=================================================================================================
	// assistant methods
	
	//-------------------------------------------------------------------------------------------------
	private DummyJsonXmlTranslatorConstants() {
		throw new UnsupportedOperationException();
	}
}
