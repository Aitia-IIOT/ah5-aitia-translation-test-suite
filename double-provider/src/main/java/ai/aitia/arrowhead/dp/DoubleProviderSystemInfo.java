package ai.aitia.arrowhead.dp;

import java.util.List;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import ai.aitia.arrowhead.Constants;
import ai.aitia.arrowhead.SharedConstants;
import ai.aitia.arrowhead.dp.http.api.DoubleServiceAPI;
import eu.arrowhead.common.SystemInfo;
import eu.arrowhead.common.http.filter.authentication.AuthenticationPolicy;
import eu.arrowhead.common.http.model.HttpDataModelsOperationModel;
import eu.arrowhead.common.http.model.HttpInterfaceModel;
import eu.arrowhead.common.http.model.HttpOperationModel;
import eu.arrowhead.common.model.InterfaceModel;
import eu.arrowhead.common.model.ServiceModel;
import eu.arrowhead.common.model.SystemModel;
import eu.arrowhead.dto.enums.ServiceInterfacePolicy;

@Component
public class DoubleProviderSystemInfo extends SystemInfo {

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
				.serviceDefinition("doubleService")
				.version("1.0.0")
				.serviceInterface(getHTTPInterfaceForDoubleService())
				.build();

		return List.of(doubleService);
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private InterfaceModel getHTTPInterfaceForDoubleService() {
		final String templateName = getSslProperties().isSslEnabled() ? Constants.GENERIC_HTTPS_INTERFACE_TEMPLATE_NAME : Constants.GENERIC_HTTP_INTERFACE_TEMPLATE_NAME;

		final HttpOperationModel makeDouble = new HttpOperationModel.Builder()
				.method(HttpMethod.POST.name())
				.path(DoubleServiceAPI.HTTP_API_OP_MAKE_DOUBLE_PATH)
				.build();

		final HttpDataModelsOperationModel dataModel = new HttpDataModelsOperationModel.Builder()
				.input(SharedConstants.TEST_XML_MODEL_ID)
				.output(SharedConstants.TEST_XML_MODEL_ID)
				.build();

		return new HttpInterfaceModel.Builder(templateName, getDomainAddress(), getServerPort())
				.basePath(DoubleServiceAPI.HTTP_API_DOUBLE_SERVICE_PATH)
				.policy(ServiceInterfacePolicy.USAGE_LIMITED_TOKEN_AUTH)
				.operation("make-double", makeDouble)
				.dataModel("make-double", dataModel)
				.build();
	}
}