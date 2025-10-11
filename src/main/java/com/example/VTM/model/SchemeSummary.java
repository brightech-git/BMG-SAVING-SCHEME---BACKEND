package com.example.VTM.model;

public class SchemeSummary {

	private String schemeId;
	private String schemeName;
	private String schemeSName;
	private String instalment;
	private String amount;
	private SchemaSummaryTransBalance schemaSummaryTransBalance;
	private String fixedIns;
	private String weightLedger;

	// ✅ New fields for weight
	private String totalWeight; // Sum of weights for this scheme
	private String lastWeight;  // Weight of last installment or last record

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
		return weightLedger;
	}

	public void setWeightLedger(String weightLedger) {
		this.weightLedger = weightLedger;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getTotalWeight() {
		return totalWeight;
	}

	public void setTotalWeight(String totalWeight) {
		this.totalWeight = totalWeight;
	}

	public String getLastWeight() {
		return lastWeight;
	}

	public void setLastWeight(String lastWeight) {
		this.lastWeight = lastWeight;
	}
}
