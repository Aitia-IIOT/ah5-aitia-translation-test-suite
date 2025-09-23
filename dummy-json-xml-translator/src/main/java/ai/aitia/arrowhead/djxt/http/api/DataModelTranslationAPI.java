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
package ai.aitia.arrowhead.djxt.http.api;

import java.security.InvalidParameterException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ai.aitia.arrowhead.Constants;
import ai.aitia.arrowhead.djxt.DummyJsonXmlTranslatorConstants;
import ai.aitia.arrowhead.djxt.service.TranslationTask;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.exception.DataNotFoundException;
import eu.arrowhead.common.exception.InternalServerError;
import eu.arrowhead.dto.DataModelTranslationInitRequestDTO;
import eu.arrowhead.dto.DataModelTranslationResultResponseDTO;
import eu.arrowhead.dto.ErrorMessageDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.annotation.Resource;

@RestController
@RequestMapping(DummyJsonXmlTranslatorConstants.HTTP_API_DM_TRANSLATION_PATH)
public class DataModelTranslationAPI {

	//=================================================================================================
	// members

	private static final String QUERY_PARAM_TASK_ID = "taskId";

	@Resource(name = DummyJsonXmlTranslatorConstants.TASK_QUEUE)
	private BlockingQueue<UUID> taskQueue;

	@Resource(name = DummyJsonXmlTranslatorConstants.TASK_STORE)
	private Map<UUID, TranslationTask> taskStore;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Operation(summary = "Initialized a translation task")
	@ApiResponses(value = {
			@ApiResponse(responseCode = Constants.HTTP_STATUS_OK, description = Constants.SWAGGER_HTTP_200_MESSAGE),
			@ApiResponse(responseCode = Constants.HTTP_STATUS_INTERNAL_SERVER_ERROR, description = Constants.SWAGGER_HTTP_500_MESSAGE, content = {
					@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorMessageDTO.class)) })
	})
	@PostMapping(path = DummyJsonXmlTranslatorConstants.HTTP_API_OP_INIT_PATH, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	public @ResponseBody String initTranslation(@RequestBody final DataModelTranslationInitRequestDTO dto) {

		final UUID taskId = UUID.randomUUID();
		taskStore.put(taskId, new TranslationTask(dto));
		taskQueue.add(taskId);

		return taskId.toString();
	}

	//-------------------------------------------------------------------------------------------------
	@Operation(summary = "Return a translation task's status/result")
	@ApiResponses(value = {
			@ApiResponse(responseCode = Constants.HTTP_STATUS_OK, description = Constants.SWAGGER_HTTP_200_MESSAGE),
			@ApiResponse(responseCode = Constants.HTTP_STATUS_BAD_REQUEST, description = Constants.SWAGGER_HTTP_400_MESSAGE, content = {
					@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorMessageDTO.class)) }),
			@ApiResponse(responseCode = Constants.HTTP_STATUS_NOT_FOUND, description = Constants.SWAGGER_HTTP_404_MESSAGE, content = {
					@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorMessageDTO.class)) }),
			@ApiResponse(responseCode = Constants.HTTP_STATUS_INTERNAL_SERVER_ERROR, description = Constants.SWAGGER_HTTP_500_MESSAGE, content = {
					@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorMessageDTO.class)) })
	})
	@GetMapping(path = DummyJsonXmlTranslatorConstants.HTTP_API_OP_GET_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody DataModelTranslationResultResponseDTO getTranslationResult(@RequestParam(required = true, name = QUERY_PARAM_TASK_ID) final String taskId) {
		if (Utilities.isEmpty(taskId) || !Utilities.isUUID(taskId.trim())) {
			throw new InvalidParameterException("taskId is missing/invalid");
		}

		final UUID taskUuid = UUID.fromString(taskId.trim());
		final TranslationTask task = taskStore.get(taskUuid);

		if (task == null) {
			throw new DataNotFoundException("Task not found");
		}

		switch (task.getStatus()) {
		case PENDING:
		case IN_PROGRESS:
			return new DataModelTranslationResultResponseDTO(task.getStatus(), null, null);
		case ERROR:
		case DONE:
			taskStore.remove(taskUuid);
			return new DataModelTranslationResultResponseDTO(task.getStatus(), task.getResult(), task.getMimeType());
		default:
			throw new InternalServerError("Unknown status: " + task.getStatus().name());
		}
	}

	//-------------------------------------------------------------------------------------------------
	@Operation(summary = "Aborts a translation task")
	@ApiResponses(value = {
			@ApiResponse(responseCode = Constants.HTTP_STATUS_OK, description = Constants.SWAGGER_HTTP_200_MESSAGE),
			@ApiResponse(responseCode = Constants.HTTP_STATUS_BAD_REQUEST, description = Constants.SWAGGER_HTTP_400_MESSAGE, content = {
					@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorMessageDTO.class)) }),
			@ApiResponse(responseCode = Constants.HTTP_STATUS_INTERNAL_SERVER_ERROR, description = Constants.SWAGGER_HTTP_500_MESSAGE, content = {
					@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorMessageDTO.class)) })
	})
	@DeleteMapping(path = DummyJsonXmlTranslatorConstants.HTTP_API_OP_ABORT_PATH)
	public void abortTranslation(@RequestParam(required = true, name = QUERY_PARAM_TASK_ID) final String taskId) {
		if (Utilities.isEmpty(taskId) || !Utilities.isUUID(taskId.trim())) {
			throw new InvalidParameterException("taskId is missing/invalid");
		}

		final UUID taskUuid = UUID.fromString(taskId.trim());
		taskStore.remove(taskUuid);
	}
}