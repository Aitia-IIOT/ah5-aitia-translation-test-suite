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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import ai.aitia.arrowhead.dto.TestElement;
import ai.aitia.arrowhead.dto.TestValueList;
import eu.arrowhead.dto.MqttRequestTemplate;

@Component
public class QuadrupleServiceHandler {

	//=================================================================================================
	// members

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private MappingJackson2XmlHttpMessageConverter xmlConverter;

	@Autowired
	private MQTTUtils utils;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@SuppressWarnings("checkstyle:MagicNumber")
	public void handle(final MqttMessage msg) {

		MqttRequestTemplate template = null;
		try {
			template = mapper.readValue(msg.getPayload(), MqttRequestTemplate.class);
			final TestValueList list = xmlConverter.getObjectMapper().readValue(mapper.writeValueAsBytes(template.payload()), TestValueList.class);
			final List<TestElement> result = new ArrayList<>(list.getTestElements().size());
			list.getTestElements().forEach(e -> {
				try {
					final int value = Integer.parseInt(e.getValue());
					result.add(new TestElement(e.getKey(), String.valueOf(4 * value)));
				} catch (final NumberFormatException __) {
					result.add(e);
				}
			});

			utils.response(
					"",
					template.responseTopic(),
					template.traceId(),
					template.qosRequirement(),
					200,
					new TestValueList(result));
		} catch (final IOException ex) {
			utils.errorResponse(ex, template);
		}
	}
}