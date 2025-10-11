package com.example.VTM.model;

import java.time.LocalDateTime;
import java.util.List;

public class PPData {

	private Integer regNo;
	private String groupCode;
	private String pName; // Add pName from PhoneSearch
	private LocalDateTime maturityDate;
	private LocalDateTime joinDate;

	private PersonalInfo personalInfo;
	private SchemeSummary schemeSummary;
	private SchemeClosedSummary schemeClosedSummary;
	private List<PaymentHistory> paymentHistoryList;
	private String lastPaidDate;
	private String amount;

	// Getters & Setters
	public Integer getRegNo() { return regNo; }
	public void setRegNo(Integer regNo) { this.regNo = regNo; }

	public String getGroupCode() { return groupCode; }
	public void setGroupCode(String groupCode) { this.groupCode = groupCode; }

	public String getPName() { return pName; }
	public void setPName(String pName) { this.pName = pName; }

	public LocalDateTime getMaturityDate() { return maturityDate; }
	public void setMaturityDate(LocalDateTime maturityDate) { this.maturityDate = maturityDate; }

	public LocalDateTime getJoinDate() { return joinDate; }
	public void setJoinDate(LocalDateTime joinDate) { this.joinDate = joinDate; }

	public PersonalInfo getPersonalInfo() { return personalInfo; }
	public void setPersonalInfo(PersonalInfo personalInfo) { this.personalInfo = personalInfo; }

	public SchemeSummary getSchemeSummary() { return schemeSummary; }
	public void setSchemeSummary(SchemeSummary schemeSummary) { this.schemeSummary = schemeSummary; }

	public SchemeClosedSummary getSchemeClosedSummary() { return schemeClosedSummary; }
	public void setSchemeClosedSummary(SchemeClosedSummary schemeClosedSummary) { this.schemeClosedSummary = schemeClosedSummary; }

	public List<PaymentHistory> getPaymentHistoryList() { return paymentHistoryList; }
	public void setPaymentHistoryList(List<PaymentHistory> paymentHistoryList) { this.paymentHistoryList = paymentHistoryList; }

	public String getLastPaidDate() { return lastPaidDate; }
	public void setLastPaidDate(String lastPaidDate) { this.lastPaidDate = lastPaidDate; }

	public String getAmount() { return amount; }
	public void setAmount(String amount) { this.amount = amount; }
}
