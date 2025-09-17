package ai.aitia.arrowhead.djxt.service;

import eu.arrowhead.dto.DataModelTranslationInitRequestDTO;
import eu.arrowhead.dto.enums.DataModelTranslationTaskStatus;

public class TranslationTask {

	//=================================================================================================
	// members
	
	private final DataModelTranslationInitRequestDTO request;
	private DataModelTranslationTaskStatus status;
	private String result;
	
	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	public TranslationTask(final DataModelTranslationInitRequestDTO request) {
		this.request = request;
		this.status = DataModelTranslationTaskStatus.PENDING;
	}

	//-------------------------------------------------------------------------------------------------
	public DataModelTranslationTaskStatus getStatus() {
		return status;
	}

	//-------------------------------------------------------------------------------------------------
	public void setStatus(final DataModelTranslationTaskStatus status) {
		this.status = status;
	}

	//-------------------------------------------------------------------------------------------------
	public String getResult() {
		return result;
	}

	//-------------------------------------------------------------------------------------------------
	public void setResult(final String result) {
		this.result = result;
	}

	//-------------------------------------------------------------------------------------------------
	public DataModelTranslationInitRequestDTO getRequest() {
		return request;
	}
}