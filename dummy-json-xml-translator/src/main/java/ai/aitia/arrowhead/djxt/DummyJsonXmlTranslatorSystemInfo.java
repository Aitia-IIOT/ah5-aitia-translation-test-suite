package ai.aitia.arrowhead.djxt;

import java.util.List;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import ai.aitia.arrowhead.Constants;
import ai.aitia.arrowhead.SharedConstants;
import eu.arrowhead.common.SystemInfo;
import eu.arrowhead.common.http.filter.authentication.AuthenticationPolicy;
import eu.arrowhead.common.http.model.HttpInterfaceModel;
import eu.arrowhead.common.http.model.HttpOperationModel;
import eu.arrowhead.common.model.InterfaceModel;
import eu.arrowhead.common.model.ServiceModel;
import eu.arrowhead.common.model.SystemModel;

@Component
public class DummyJsonXmlTranslatorSystemInfo extends SystemInfo {

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
		final ServiceModel doubleService = new ServiceModel.Builder()
				.serviceDefinition(Constants.SERVICE_DEF_DATA_MODEL_TRANSLATION)
				.version("1.0.0")
				.metadata(Constants.METADATA_KEY_DATA_MODEL_IDS, List.of(
						List.of(SharedConstants.TEST_JSON_MODEL_ID, SharedConstants.TEST_XML_MODEL_ID),
						List.of(SharedConstants.TEST_XML_MODEL_ID, SharedConstants.TEST_JSON_MODEL_ID)))
				.serviceInterface(getHTTPInterfaceForDataModelTranslation())
				.build();

		return List.of(doubleService);
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private InterfaceModel getHTTPInterfaceForDataModelTranslation() {
		final String templateName = getSslProperties().isSslEnabled() ? Constants.GENERIC_HTTPS_INTERFACE_TEMPLATE_NAME : Constants.GENERIC_HTTP_INTERFACE_TEMPLATE_NAME;

		final HttpOperationModel init = new HttpOperationModel.Builder()
				.method(HttpMethod.POST.name())
				.path(DummyJsonXmlTranslatorConstants.HTTP_API_OP_INIT_PATH)
				.build();
		
		final HttpOperationModel get = new HttpOperationModel.Builder()
				.method(HttpMethod.GET.name())
				.path(DummyJsonXmlTranslatorConstants.HTTP_API_OP_GET_PATH)
				.build();

		final HttpOperationModel abort = new HttpOperationModel.Builder()
				.method(HttpMethod.DELETE.name())
				.path(DummyJsonXmlTranslatorConstants.HTTP_API_OP_ABORT_PATH)
				.build();

		return new HttpInterfaceModel.Builder(templateName, getDomainAddress(), getServerPort())
				.basePath(DummyJsonXmlTranslatorConstants.HTTP_API_DM_TRANSLATION_PATH)
				.operation(Constants.SERVICE_OP_DATA_MODEL_TRANSLATOR_INIT_TRANSLATION, init)
				.operation(Constants.SERVICE_OP_DATA_MODEL_TRANSLATOR_GET_TRANSLATION_RESULT, get)
				.operation(Constants.SERVICE_OP_DATA_MODEL_TRANSLATOR_ABORT_TRANSLATION, abort)
				.build();
	}
}