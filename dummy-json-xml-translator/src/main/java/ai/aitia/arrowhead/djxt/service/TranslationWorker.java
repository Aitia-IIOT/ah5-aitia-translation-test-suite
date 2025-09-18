package ai.aitia.arrowhead.djxt.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import ai.aitia.arrowhead.SharedConstants;
import ai.aitia.arrowhead.djxt.DummyJsonXmlTranslatorConstants;
import ai.aitia.arrowhead.dto.JsonTestElement;
import ai.aitia.arrowhead.dto.JsonTestValueList;
import ai.aitia.arrowhead.dto.TestElement;
import ai.aitia.arrowhead.dto.TestValueList;
import eu.arrowhead.dto.enums.DataModelTranslationTaskStatus;
import jakarta.annotation.Resource;

@Component
public class TranslationWorker extends Thread {

	//=================================================================================================
	// members

	@Resource(name = DummyJsonXmlTranslatorConstants.TASK_QUEUE)
	private BlockingQueue<UUID> taskQueue;

	@Resource(name = DummyJsonXmlTranslatorConstants.TASK_STORE)
	private Map<UUID, TranslationTask> taskStore;

	@Autowired
	private ObjectMapper jsonMapper;

	@Autowired
	private MappingJackson2XmlHttpMessageConverter xmlConverter;

	private boolean doWork = true;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Override
	public void run() {
		while (doWork) {
			try {
				final UUID taskId = taskQueue.take();
				handleTask(taskId);
			} catch (final InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	public void interrupt() {
		doWork = false;
		super.interrupt();
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private void handleTask(final UUID taskId) {
		final TranslationTask task = taskStore.get(taskId);
		if (task != null) {
			task.setStatus(DataModelTranslationTaskStatus.IN_PROGRESS);

			try {
				final byte[] input = Base64.getDecoder().decode(task.getRequest().payload().getBytes(StandardCharsets.UTF_8));
				byte[] result = null;
				if (task.getRequest().inputModelId().equals(SharedConstants.TEST_JSON_MODEL_ID)
						&& task.getRequest().outputModelId().equals(SharedConstants.TEST_XML_MODEL_ID)) {
					result = translateJsonToXml(input);
				} else if (task.getRequest().inputModelId().endsWith(SharedConstants.TEST_XML_MODEL_ID)
						&& task.getRequest().outputModelId().equals(SharedConstants.TEST_JSON_MODEL_ID)) {
					result = translateXmlToJson(input);
				} else {
					throw new InvalidParameterException("Unknown model ids");
				}

				String resultBase64 = new String(Base64.getEncoder().encode(result), StandardCharsets.UTF_8);
				task.setResult(resultBase64);
				task.setStatus(DataModelTranslationTaskStatus.DONE);
			} catch (final Exception ex) {
				task.setResult(ex.getMessage());
				task.setStatus(DataModelTranslationTaskStatus.ERROR);
			}
		}
	}

	//-------------------------------------------------------------------------------------------------
	private byte[] translateJsonToXml(final byte[] input) {
		try {
			final JsonTestValueList inputList = jsonMapper.readValue(input, JsonTestValueList.class);
			final List<TestElement> resultList = new ArrayList<>(inputList.elements().size());
			inputList.elements().forEach(e -> {
				resultList.add(new TestElement(e.id(), e.value()));
			});
			final TestValueList result = new TestValueList(resultList);

			return xmlConverter.getObjectMapper().writeValueAsBytes(result);
		} catch (final IOException ex) {
			ex.printStackTrace();
			throw new InvalidParameterException("Input is not using data model " + SharedConstants.TEST_JSON_MODEL_ID);
		}
	}

	//-------------------------------------------------------------------------------------------------
	private byte[] translateXmlToJson(final byte[] input) {
		try {
			final TestValueList inputList = xmlConverter.getObjectMapper().readValue(input, TestValueList.class);
			final List<JsonTestElement> resultList = new ArrayList<>(inputList.getTestElements().size());
			inputList.getTestElements().forEach(e -> {
				resultList.add(new JsonTestElement(e.getKey(), e.getValue()));
			});
			final JsonTestValueList result = new JsonTestValueList(resultList);

			return jsonMapper.writeValueAsBytes(result);
		} catch (final IOException ex) {
			ex.printStackTrace();
			throw new InvalidParameterException("Input is not using data model " + SharedConstants.TEST_XML_MODEL_ID);
		}
	}
}