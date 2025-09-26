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
package ai.aitia.arrowhead.dp.http.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ai.aitia.arrowhead.Constants;
import ai.aitia.arrowhead.dto.TestElement;
import ai.aitia.arrowhead.dto.TestValueList;
import eu.arrowhead.dto.ErrorMessageDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@SecurityRequirement(name = Constants.SECURITY_REQ_AUTHORIZATION)
public class DoubleServiceAPI {

	//=================================================================================================
	// members

	public static final String HTTP_API_DOUBLE_SERVICE_PATH = "/double";
	public static final String HTTP_API_OP_MAKE_DOUBLE_PATH = "/make-double";

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Operation(summary = "Doubles every number in the input")
	@ApiResponses(value = {
			@ApiResponse(responseCode = Constants.HTTP_STATUS_OK, description = Constants.SWAGGER_HTTP_200_MESSAGE),
			@ApiResponse(responseCode = Constants.HTTP_STATUS_INTERNAL_SERVER_ERROR, description = Constants.SWAGGER_HTTP_500_MESSAGE, content = {
					@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorMessageDTO.class)) })
	})
	@PostMapping(path = HTTP_API_DOUBLE_SERVICE_PATH + HTTP_API_OP_MAKE_DOUBLE_PATH, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody TestValueList makeDouble(final HttpServletRequest request, @RequestBody final TestValueList dto) {
		if (request.getHeader(HttpHeaders.AUTHORIZATION) != null) {
			System.out.println("Authorization: " + request.getHeader(HttpHeaders.AUTHORIZATION));
		}

		final List<TestElement> result = new ArrayList<>(dto.getTestElements().size());
		dto.getTestElements().forEach(e -> {
			try {
				final int value = Integer.parseInt(e.getValue());
				result.add(new TestElement(e.getKey(), String.valueOf(2 * value)));
			} catch (final NumberFormatException __) {
				result.add(e);
			}
		});

		return new TestValueList(result);
	}
}