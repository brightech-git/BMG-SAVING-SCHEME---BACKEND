package com.example.VTM.model;

import java.util.List;

public class PPData {
	
    private PersonalInfo personalInfo;
    private SchemeSummary schemeSummary;
	private SchemeClosedSummary schemeClosedSummary;
    private List<PaymentHistory> paymentHistoryList;
    private String lastPaidDate;
    private String amount;
    
    public PersonalInfo getPersonalInfo() {
        return personalInfo;
    }

    public void setPersonalInfo(PersonalInfo personalInfo) {
        this.personalInfo = personalInfo;
    }
    public SchemeSummary getSchemeSummary() {
		return schemeSummary;
	}

	public void setSchemeSummary(SchemeSummary schemeSummary) {
		this.schemeSummary = schemeSummary;
	}

	
    public List<PaymentHistory> getPaymentHistoryList() {
        return paymentHistoryList;
    }

    public void setPaymentHistoryList(List<PaymentHistory> paymentHistoryList) {
        this.paymentHistoryList = paymentHistoryList;
    }
    public SchemeClosedSummary getSchemeClosedSummary() {
  		return schemeClosedSummary;
  	}

  	public void setSchemeClosedSummary(SchemeClosedSummary schemeClosedSummary) {
  		this.schemeClosedSummary = schemeClosedSummary;
  	}

	public String getLastPaidDate() {
		return lastPaidDate;
	}

	public void setLastPaidDate(String lastPaidDate) {
		this.lastPaidDate = lastPaidDate;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}
  	
}
