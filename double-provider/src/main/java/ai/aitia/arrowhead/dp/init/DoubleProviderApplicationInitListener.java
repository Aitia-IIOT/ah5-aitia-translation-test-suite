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
package ai.aitia.arrowhead.dp.init;

import javax.naming.ConfigurationException;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import ai.aitia.arrowhead.Constants;
import eu.arrowhead.common.init.ApplicationInitListener;
import eu.arrowhead.dto.AuthorizationGrantRequestDTO;
import eu.arrowhead.dto.AuthorizationPolicyRequestDTO;
import eu.arrowhead.dto.AuthorizationPolicyResponseDTO;

@Component
public class DoubleProviderApplicationInitListener extends ApplicationInitListener {

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	@Override
	protected void customInit(final ContextRefreshedEvent event) throws InterruptedException, ConfigurationException {
		final AuthorizationGrantRequestDTO payload = new AuthorizationGrantRequestDTO(
				"LOCAL",
				"SERVICE_DEF",
				"doubleService",
				"can be used by every system in the local cloud",
				new AuthorizationPolicyRequestDTO("ALL", null, null),
				null);

		try {
			arrowheadHttpService.consumeService(
					Constants.SERVICE_DEF_AUTHORIZATION,
					Constants.SERVICE_OP_GRANT,
					AuthorizationPolicyResponseDTO.class,
					payload);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}
}