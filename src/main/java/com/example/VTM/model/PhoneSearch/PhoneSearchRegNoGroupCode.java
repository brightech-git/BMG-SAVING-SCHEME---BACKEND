package com.example.VTM.model.PhoneSearch;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Date;

public class PhoneSearchRegNoGroupCode {
	
	private String PNAME;
	private Integer REGNO;
	private String GROUPCODE;
	private LocalDateTime MaturityDate;
	private LocalDateTime JOINDATE;

	public String getPNAME() {
		return PNAME;
	}

	public void setPNAME(String PNAME) {
		this.PNAME = PNAME;
	}

	public Integer getREGNO() {
		return REGNO;
	}

	public void setREGNO(Integer REGNO) {
		this.REGNO = REGNO;
	}

	public String getGROUPCODE() {
		return GROUPCODE;
	}

	public void setGROUPCODE(String GROUPCODE) {
		this.GROUPCODE = GROUPCODE;
	}

	public LocalDateTime getMaturityDate() {
		return MaturityDate;
	}

	public void setMaturityDate(LocalDateTime maturityDate) {
		MaturityDate = maturityDate;
	}

	public LocalDateTime getJOINDATE() {
		return JOINDATE;
	}

	public void setJOINDATE(LocalDateTime JOINDATE) {
		this.JOINDATE = JOINDATE;
	}
}
