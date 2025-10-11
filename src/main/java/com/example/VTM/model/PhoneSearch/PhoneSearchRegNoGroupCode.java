package com.example.VTM.model.PhoneSearch;

import java.time.LocalDateTime;

public class PhoneSearchRegNoGroupCode {

	private String pName;
	private Integer regNo;
	private String groupCode;
	private LocalDateTime maturityDate;
	private LocalDateTime joinDate;

	// Getters and Setters
	public String getPName() { return pName; }
	public void setPName(String pName) { this.pName = pName; }

	public Integer getRegNo() { return regNo; }
	public void setRegNo(Integer regNo) { this.regNo = regNo; }

	public String getGroupCode() { return groupCode; }
	public void setGroupCode(String groupCode) { this.groupCode = groupCode; }

	public LocalDateTime getMaturityDate() { return maturityDate; }
	public void setMaturityDate(LocalDateTime maturityDate) { this.maturityDate = maturityDate; }

	public LocalDateTime getJoinDate() { return joinDate; }
	public void setJoinDate(LocalDateTime joinDate) { this.joinDate = joinDate; }
}
