package ai.aitia.arrowhead.djxt.init;

import javax.naming.ConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import ai.aitia.arrowhead.Constants;
import ai.aitia.arrowhead.djxt.service.TranslationWorker;
import eu.arrowhead.common.init.ApplicationInitListener;
import eu.arrowhead.dto.AuthorizationGrantRequestDTO;
import eu.arrowhead.dto.AuthorizationPolicyRequestDTO;
import eu.arrowhead.dto.AuthorizationPolicyResponseDTO;

@Component
public class DummyJsonXmlTranslatorApplicationInitListener extends ApplicationInitListener {
	
	//=================================================================================================
	// members
	
	@Autowired
	private TranslationWorker worker;

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	@Override
	protected void customInit(final ContextRefreshedEvent event) throws InterruptedException, ConfigurationException {
		final AuthorizationGrantRequestDTO payload = new AuthorizationGrantRequestDTO(
				"LOCAL",
				"SERVICE_DEF",
				Constants.SERVICE_DEF_DATA_MODEL_TRANSLATION,
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
		
		worker.start();
	}
	
	//-------------------------------------------------------------------------------------------------
	@Override
	protected void customDestroy() {
		worker.interrupt();
	}
}