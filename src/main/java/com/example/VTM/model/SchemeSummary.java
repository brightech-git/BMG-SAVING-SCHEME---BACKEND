package com.example.VTM.model;

public class SchemeSummary {
	
	private String schemeId;
	private String schemeName;
	private String schemeSName;
	private String instalment;
	private String Amount;
	private SchemaSummaryTransBalance schemaSummaryTransBalance;
	private String fixedIns;
	private String WeightLedger;
	
	
	public String getSchemeId() {
		return schemeId;
	}
	public void setSchemeId(String schemeId) {
		this.schemeId = schemeId;
	}
	public String getSchemeName() {
		return schemeName;
	}
	public void setSchemeName(String schemeName) {
		this.schemeName = schemeName;
	}
	public String getSchemeSName() {
		return schemeSName;
	}
	public void setSchemeSName(String schemeSName) {
		this.schemeSName = schemeSName;
	}
	public String getInstalment() {
		return instalment;
	}
	public void setInstalment(String instalment) {
		this.instalment = instalment;
	}
	public SchemaSummaryTransBalance getSchemaSummaryTransBalance() {
		return schemaSummaryTransBalance;
	}
	public void setSchemaSummaryTransBalance(SchemaSummaryTransBalance schemaSummaryTransBalance) {
		this.schemaSummaryTransBalance = schemaSummaryTransBalance;
	}
	public String getFixedIns() {
		return fixedIns;
	}
	public void setFixedIns(String fixedIns) {
		this.fixedIns = fixedIns;
	}
	public String getWeightLedger() {
		return WeightLedger;
	}
	public void setWeightLedger(String weightLedger) {
		WeightLedger = weightLedger;
	}

	public String getAmount() {
		return Amount;
	}

	public void setAmount(String amount) {
		Amount = amount;
	}
}
