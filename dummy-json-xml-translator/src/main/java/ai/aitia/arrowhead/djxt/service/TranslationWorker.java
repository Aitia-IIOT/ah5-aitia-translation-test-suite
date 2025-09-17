package ai.aitia.arrowhead.djxt.service;

import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

import org.springframework.stereotype.Component;

import ai.aitia.arrowhead.SharedConstants;
import ai.aitia.arrowhead.djxt.DummyJsonXmlTranslatorConstants;
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
			task.setStatus(DataModelTranslationTaskStatus.PENDING);
			
			try {
				byte[] result = null;
				if (task.getRequest().inputModelId().equals(SharedConstants.TEST_JSON_MODEL_ID)
						&& task.getRequest().outputModelId().equals(SharedConstants.TEST_XML_MODEL_ID)) {
					result = translateJsonToXml(task.getRequest().payload());
				} else if (task.getRequest().inputModelId().endsWith(SharedConstants.TEST_XML_MODEL_ID)
						&& task.getRequest().outputModelId().equals(SharedConstants.TEST_JSON_MODEL_ID)) {
					result = translateXmlToJson(task.getRequest().payload());
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
	private byte[] translateXmlToJson(String payload) {
		// TODO Auto-generated method stub
		return null;
	}

	//-------------------------------------------------------------------------------------------------
	private byte[] translateJsonToXml(String payload) {
		// TODO Auto-generated method stub
		return null;
	}
}