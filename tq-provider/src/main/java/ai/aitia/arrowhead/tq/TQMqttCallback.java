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

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.aitia.arrowhead.tq.mqtt.api.QuadrupleServiceHandler;
import ai.aitia.arrowhead.tq.mqtt.api.TripleServiceHandler;

@Service
public class TQMqttCallback implements MqttCallback {

	//=================================================================================================
	// members

	@Autowired
	private TripleServiceHandler tripleHandler;

	@Autowired
	private QuadrupleServiceHandler quadrupleHandler;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Override
	public void messageArrived(final String topic, final MqttMessage message) throws Exception {
		if (topic.equals("tq/triple/make-triple")) {
			tripleHandler.handle(message);
		} else if (topic.equals("tq/quadruple/make-quadruple")) {
			quadrupleHandler.handle(message);
		}
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	public void deliveryComplete(final IMqttDeliveryToken token) {
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	public void connectionLost(final Throwable cause) {
	}
}
