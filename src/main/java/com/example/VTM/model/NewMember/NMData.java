package com.example.VTM.model.NewMember;

import com.example.VTM.model.SchemeCollectInsert;

public class NMData {

	private NewMember newMember;
	private CreateSchemeSummary createSchemeSummary;
	private SchemeCollectInsert schemeCollectInsert;

	public NewMember getNewMember() {
		return newMember;
	}

	public void setNewMember(NewMember newMember) {
		this.newMember = newMember;
	}

	public CreateSchemeSummary getCreateSchemeSummary() {
		return createSchemeSummary;
	}

	public void setCreateSchemeSummary(CreateSchemeSummary createSchemeSummary) {
		this.createSchemeSummary = createSchemeSummary;
	}

	public SchemeCollectInsert getSchemeCollectInsert() {
		return schemeCollectInsert;
	}

	public void setSchemeCollectInsert(SchemeCollectInsert schemeCollectInsert) {
		this.schemeCollectInsert = schemeCollectInsert;
	}
}
