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
 *  	AITIA - implementation
 *  	Arrowhead Consortia - conceptualization
 *
 *******************************************************************************/
package ai.aitia.arrowhead.tq.mqtt.api;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ai.aitia.arrowhead.Constants;
import ai.aitia.arrowhead.tq.GeneralMqttClient;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.common.exception.ExternalServerError;
import eu.arrowhead.common.exception.InternalServerError;
import eu.arrowhead.common.mqtt.MqttStatus;
import eu.arrowhead.dto.MqttRequestTemplate;
import eu.arrowhead.dto.MqttResponseTemplate;
import eu.arrowhead.dto.enums.ExceptionType;

@Component
public class MQTTUtils {

	//=================================================================================================
	// members

	@Autowired
	private GeneralMqttClient client;

	@Autowired
	private ObjectMapper mapper;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public void errorResponse(final Exception ex, final MqttRequestTemplate request) {

		if (request == null) {
			return;
		}

		if (Utilities.isEmpty(request.responseTopic())) {
			return;
		}

		final ExceptionType exType = calculateExceptionType(ex);
		final String payload = exType.getErrorCode() + " " + ex.getMessage();

		response(
				"",
				request.responseTopic(),
				request.traceId(),
				request.qosRequirement(),
				calculateStatusFromExceptionType(exType).value(),
				payload);
	}

	//-------------------------------------------------------------------------------------------------
	public void response(
			final String receiver,
			final String topic,
			final String traceId,
			final Integer qos,
			final int status,
			final Object payload) {
		Assert.isTrue(!Utilities.isEmpty(topic), "topic is empty");

		try {
			final MqttResponseTemplate template = new MqttResponseTemplate(status, traceId, receiver, payload == null ? "" : payload);
			final MqttMessage msg = new MqttMessage(mapper.writeValueAsBytes(template));
			msg.setQos(qos == null ? Constants.MQTT_DEFAULT_QOS : qos);
			client.publish(topic, msg);
		} catch (final JsonProcessingException ex) {
			throw new InternalServerError("MQTT service response message creation failed: " + ex.getMessage());
		} catch (final MqttException ex) {
			throw new ExternalServerError("MQTT service response failed: " + ex.getMessage());
		}
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private ExceptionType calculateExceptionType(final Exception ex) {
		if (!(ex instanceof ArrowheadException)) {
			return ExceptionType.INTERNAL_SERVER_ERROR;
		}

		return ((ArrowheadException) ex).getExceptionType();
	}

	//-------------------------------------------------------------------------------------------------
	private MqttStatus calculateStatusFromExceptionType(final ExceptionType exType) {

		switch (exType) {
		case AUTH:
			return MqttStatus.UNAUTHORIZED;
		case FORBIDDEN:
			return MqttStatus.FORBIDDEN;
		case INVALID_PARAMETER:
			return MqttStatus.BAD_REQUEST;
		case DATA_NOT_FOUND:
			return MqttStatus.NOT_FOUND;
		case EXTERNAL_SERVER_ERROR:
			return MqttStatus.EXTERNAL_SERVER_ERROR;
		case TIMEOUT:
			return MqttStatus.TIMEOUT;
		case LOCKED:
			return MqttStatus.LOCKED;
		default:
			return MqttStatus.INTERNAL_SERVER_ERROR;
		}
	}
}