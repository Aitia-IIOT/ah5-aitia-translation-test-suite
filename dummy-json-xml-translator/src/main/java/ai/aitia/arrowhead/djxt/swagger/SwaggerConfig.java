package ai.aitia.arrowhead.djxt.swagger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import ai.aitia.arrowhead.djxt.DummyJsonXmlTranslatorSystemInfo;
import eu.arrowhead.common.swagger.DefaultSwaggerConfig;
import io.swagger.v3.oas.models.info.License;
import jakarta.annotation.PostConstruct;

@Configuration
public class SwaggerConfig extends DefaultSwaggerConfig {

	//=================================================================================================
	// methods

	@Autowired
	private DummyJsonXmlTranslatorSystemInfo sysInfo;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public SwaggerConfig() {
		super(null, "1.0.0");
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	@Override
	protected License apiLicence() {
		// TODO: add real licence here
		return super.apiLicence();
	}

	//-------------------------------------------------------------------------------------------------
	@PostConstruct
	private void init() {
		setSystemName(sysInfo.getSystemName());
	}
}