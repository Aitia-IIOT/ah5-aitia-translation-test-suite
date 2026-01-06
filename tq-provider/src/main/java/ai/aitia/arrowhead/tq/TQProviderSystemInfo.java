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
package ai.aitia.arrowhead.tq;

import java.util.List;

import org.springframework.stereotype.Component;

import ai.aitia.arrowhead.Constants;
import ai.aitia.arrowhead.SharedConstants;
import eu.arrowhead.common.SystemInfo;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.exception.InvalidParameterException;
import eu.arrowhead.common.http.filter.authentication.AuthenticationPolicy;
import eu.arrowhead.common.http.model.DataModelsOperationModel;
import eu.arrowhead.common.model.InterfaceModel;
import eu.arrowhead.common.model.ServiceModel;
import eu.arrowhead.common.model.SystemModel;
import eu.arrowhead.common.mqtt.model.MqttInterfaceModel;
import eu.arrowhead.dto.enums.ServiceInterfacePolicy;

@Component
public class TQProviderSystemInfo extends SystemInfo {

	//=================================================================================================
	// members

	private SystemModel systemModel;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Override
	public SystemModel getSystemModel() {
		if (systemModel == null) {
			SystemModel.Builder builder = new SystemModel.Builder()
					.address(getAddress())
					.version("1.0.0");

			if (AuthenticationPolicy.CERTIFICATE == this.getAuthenticationPolicy()) {
				builder = builder.metadata(Constants.METADATA_KEY_X509_PUBLIC_KEY, getPublicKey());
			}

			systemModel = builder.build();
		}

		return systemModel;
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	public List<ServiceModel> getServices() {
		final ServiceModel tripleService = new ServiceModel.Builder()
				.serviceDefinition("tripleService")
				.version("1.0.0")
				.serviceInterface(getMQTTInterfaceForTripleService())
				.build();

		final ServiceModel quadrupleService = new ServiceModel.Builder()
				.serviceDefinition("quadrupleService")
				.version("1.0.0")
				.serviceInterface(getMQTTInterfaceForQuadrupleService())
				.build();

		return List.of(tripleService, quadrupleService);
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	@Override
	protected void customInit() {
		if (Utilities.isEmpty(getMqttBrokerAddress())) {
			throw new InvalidParameterException("MQTT Broker address is not defined");
		}

		if (getMqttBrokerPort() == null) {
			throw new InvalidParameterException("MQTT Broker port is not defined");
		}
	}

	//-------------------------------------------------------------------------------------------------
	private InterfaceModel getMQTTInterfaceForTripleService() {
		final String templateName = isSslEnabled() ? Constants.GENERIC_MQTTS_INTERFACE_TEMPLATE_NAME : Constants.GENERIC_MQTT_INTERFACE_TEMPLATE_NAME;

		final DataModelsOperationModel dataModel = new DataModelsOperationModel.Builder()
				.input(SharedConstants.TEST_JSON_MODEL_ID)
				.output(SharedConstants.TEST_JSON_MODEL_ID)
				.build();

		return new MqttInterfaceModel.Builder(templateName, getMqttBrokerAddress(), getMqttBrokerPort())
				.baseTopic("tq/triple/")
				.policy(ServiceInterfacePolicy.USAGE_LIMITED_TOKEN_AUTH)
				.operation("make-triple")
				.dataModel("make-triple", dataModel)
				.build();
	}

	//-------------------------------------------------------------------------------------------------
	private InterfaceModel getMQTTInterfaceForQuadrupleService() {
		final String templateName = isSslEnabled() ? Constants.GENERIC_MQTTS_INTERFACE_TEMPLATE_NAME : Constants.GENERIC_MQTT_INTERFACE_TEMPLATE_NAME;

		final DataModelsOperationModel dataModel = new DataModelsOperationModel.Builder()
				.input(SharedConstants.TEST_XML_MODEL_ID)
				.output(SharedConstants.TEST_XML_MODEL_ID)
				.build();

		return new MqttInterfaceModel.Builder(templateName, getMqttBrokerAddress(), getMqttBrokerPort())
				.baseTopic("tq/quadruple/")
				.policy(ServiceInterfacePolicy.USAGE_LIMITED_TOKEN_AUTH)
				.operation("make-quadruple")
				.dataModel("make-quadruple", dataModel)
				.build();
	}
}