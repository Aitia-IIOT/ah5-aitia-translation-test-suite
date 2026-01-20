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
package ai.aitia.arrowhead.tq.init;

import javax.naming.ConfigurationException;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import ai.aitia.arrowhead.Constants;
import ai.aitia.arrowhead.tq.GeneralMqttClient;
import eu.arrowhead.common.init.ApplicationInitListener;
import eu.arrowhead.dto.AuthorizationGrantRequestDTO;
import eu.arrowhead.dto.AuthorizationPolicyRequestDTO;
import eu.arrowhead.dto.AuthorizationPolicyResponseDTO;

@Component
public class TQProviderApplicationInitListener extends ApplicationInitListener {

	//=================================================================================================
	// members

	@Autowired
	private GeneralMqttClient mqttClient;

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	@Override
	protected void customInit(final ContextRefreshedEvent event) throws InterruptedException, ConfigurationException {
		final AuthorizationGrantRequestDTO payload = getPayload("tripleService");
		final AuthorizationGrantRequestDTO payload2 = getPayload("quadrupleService");

		try {
			arrowheadHttpService.consumeService(
					Constants.SERVICE_DEF_AUTHORIZATION,
					Constants.SERVICE_OP_GRANT,
					AuthorizationPolicyResponseDTO.class,
					payload);

			arrowheadHttpService.consumeService(
					Constants.SERVICE_DEF_AUTHORIZATION,
					Constants.SERVICE_OP_GRANT,
					AuthorizationPolicyResponseDTO.class,
					payload2);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}

		try {
			mqttClient.initialize();
			mqttClient.subscribe("tq/triple/make-triple");
			mqttClient.subscribe("tq/quadruple/make-quadruple");
		} catch (final MqttException ex) {
			logger.error(ex.getMessage());
			logger.debug(ex);
			throw new ConfigurationException("Can't access the MQTT broker: " + ex.getMessage());
		}
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	protected void customDestroy() {
		try {
			mqttClient.unsubscribe("tq/triple/make-triple");
			mqttClient.unsubscribe("tq/quadruple/make-quadruple");
			mqttClient.destroy();
		} catch (final MqttException ex) {
			logger.error(ex.getMessage());
			logger.debug(ex);
		}
	}

	//-------------------------------------------------------------------------------------------------
	private AuthorizationGrantRequestDTO getPayload(final String serviceDef) {
		final AuthorizationGrantRequestDTO payload = new AuthorizationGrantRequestDTO(
				"LOCAL",
				"SERVICE_DEF",
				serviceDef,
				"can be used by every system in the local cloud",
				new AuthorizationPolicyRequestDTO("ALL", null, null),
				null);
		return payload;
	}
}