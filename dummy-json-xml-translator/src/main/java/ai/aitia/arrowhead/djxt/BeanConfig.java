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

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ai.aitia.arrowhead.djxt.service.TranslationTask;

@Configuration
public class BeanConfig {

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Bean(DummyJsonXmlTranslatorConstants.TASK_QUEUE)
	BlockingQueue<UUID> getTaskQueue() {
		return new LinkedBlockingQueue<>();
	}
	
	//-------------------------------------------------------------------------------------------------
	@Bean(DummyJsonXmlTranslatorConstants.TASK_STORE)
	Map<UUID, TranslationTask> getTaskStore() {
		return new ConcurrentHashMap<>();
	}
}