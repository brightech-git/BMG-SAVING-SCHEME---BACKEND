package com.example.VTM.service;

import com.example.VTM.model.PPData;
import com.example.VTM.model.PaymentHistory;
import com.example.VTM.model.PersonalInfo;
import com.example.VTM.model.SchemaSummaryTransBalance;
import com.example.VTM.model.SchemeClosedSummary;
import com.example.VTM.model.SchemeSummary;
import com.example.VTM.model.PhoneSearch.PhoneSearchRegNoGroupCode;

import com.example.VTM.service.utils.CustomQueryUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;


import java.sql.Types;
import java.util.*;

@Service
public class AccountService {
	private final JdbcTemplate firstJdbcTemplate;
	private final JdbcTemplate secondJdbcTemplate;
	private final CustomQueryUtils customQueryUtils;

	@Autowired
	public AccountService(@Qualifier("firstJdbcTemplate") JdbcTemplate firstJdbcTemplate,
			@Qualifier("secondJdbcTemplate") JdbcTemplate secondJdbcTemplate,@Qualifier("customQueryUtils") CustomQueryUtils customQueryUtils) {
		this.firstJdbcTemplate = firstJdbcTemplate;
		this.secondJdbcTemplate = secondJdbcTemplate;
		this.customQueryUtils=customQueryUtils;
	}


	public PPData ppData(int regNo, String groupCode) {
		PPData ppData = new PPData();

		//PersonalInfo
		String sql1 = "SELECT " + "p.PERSONALID, " + "p.PNAME, " + "p.DOORNO, " + "p.ADDRESS1, " + "p.PINCODE, "
				+ "p.MOBILE " + "FROM " + "PERSONALINFO AS p " + "LEFT JOIN " + "SCHEMEMAST AS s "
				+ "ON s.sno = p.PERSONALID " + "WHERE " + "s.REGNO = ? " + "AND s.GROUPCODE = ?;";
		@SuppressWarnings("deprecation")
		PersonalInfo personalInfo = firstJdbcTemplate.queryForObject(sql1, new Object[] { regNo, groupCode },
				new BeanPropertyRowMapper<>(PersonalInfo.class));
	
		//PaymentHistoryList
		String sql2 = "SELECT RECEIPTNO,AMOUNT,INSTALLMENT,UPDATETIME FROM SCHEMETRAN  "
				+ "WHERE REGNO = ? AND GROUPCODE = ? " + "ORDER BY INSTALLMENT";
		@SuppressWarnings("deprecation")
		List<PaymentHistory> paymentHistoryList = secondJdbcTemplate.query(sql2, new Object[] { regNo, groupCode },
				new BeanPropertyRowMapper<>(PaymentHistory.class));

		//SchemeSummary
		String sql3_section1 = "SELECT s.SchemeId,s.schemeName ,s.SchemeSName ,s.Instalment,s.FixedIns,s.WeightLedger"
								+ " from SCHEMEMAST sm   "
								+ "left join Scheme s on s.SchemeId =sm.SCHEMEID  "
								+ "where sm.GROUPCODE = ? and  sm.REGNO = ? ";
		@SuppressWarnings("deprecation")
		SchemeSummary schemeSummary = firstJdbcTemplate.queryForObject(sql3_section1, new Object[] { groupCode,regNo },
				new BeanPropertyRowMapper<>(SchemeSummary.class));
		
		//SchemaSummaryTransBalance
		String sql3_section2 = "SELECT " + "	SUM(AMOUNT) AMTRECD , " + "	COUNT(INSTALLMENT) INSPaid  " + " FROM "
				+ "	SCHEMETRAN " + " WHERE " + "	REGNO = ?  " + "	and GROUPCODE = ?  " + " GROUP BY " + "	REGNO, "
				+ "	GROUPCODE ";
		@SuppressWarnings("deprecation")
		SchemaSummaryTransBalance schemaSummaryTransBalance = secondJdbcTemplate.queryForObject(sql3_section2,
				new Object[] { regNo, groupCode }, new BeanPropertyRowMapper<>(SchemaSummaryTransBalance.class));
		
		// SchemeClosedSummary
		String sql4="SELECT sm.DOCLOSE,sm.BILLNO,u.USERNAME,e.EMPNAME FROM SCHEMEMAST sm "
				+ "LEFT JOIN USERMASTER u on u.USERID =sm.USERID "
				+ "LEFT JOIN EMPMASTER e on e.EMPID =sm.IEMP "
				+ "WHERE GROUPCODE = ? AND REGNO = ?" ;
		@SuppressWarnings("deprecation")
		SchemeClosedSummary schemeClosedSummary  = firstJdbcTemplate.queryForObject(sql4,
				new Object[] {groupCode, regNo  }, new BeanPropertyRowMapper<>(SchemeClosedSummary.class));
		
		//Last Paid Date
		String sql5="SELECT MAX(UPDATETIME) FROM SCHEMETRAN "
				+ "WHERE REGNO = ? AND GROUPCODE = ? ";
		@SuppressWarnings("deprecation")
		String lastPaidDate = secondJdbcTemplate.queryForObject(sql5, new Object[]{regNo, groupCode}, String.class);		
		
		//Last Paid Date			
		if(schemeSummary.getFixedIns().equals("Y")) {			
			String getfixedAmount="SELECT TOP 1 Amount FROM SCHEMETRAN WHERE GROUPCODE= ? AND regno=? ";
			@SuppressWarnings("deprecation")
			Optional<String> fixedAmount = Optional.ofNullable(secondJdbcTemplate.queryForObject
								(getfixedAmount, new Object[]{groupCode ,regNo}, String.class));
			if(fixedAmount.equals(null))
				ppData.setAmount(null);				
			else
				ppData.setAmount(fixedAmount.get());
		}
		
		schemeSummary.setSchemaSummaryTransBalance(schemaSummaryTransBalance);
		ppData.setPersonalInfo(personalInfo);
		ppData.setSchemeClosedSummary(schemeClosedSummary);
		ppData.setPaymentHistoryList(paymentHistoryList);
		ppData.setSchemeSummary(schemeSummary);
		ppData.setLastPaidDate(lastPaidDate);				
		return ppData;
	}

	public List<PhoneSearchRegNoGroupCode> getRegNoGroupCodeByPhoneNo(String phoneNo) {
		String sql = "SELECT p.PNAME, sm.REGNO, sm.GROUPCODE, " +
				"DATEADD(MONTH, S.Instalment, SM.JOINDATE) AS MaturityDate, SM.JOINDATE " +
				"FROM PERSONALINFO p " +
				"LEFT JOIN SCHEMEMAST sm ON sm.SNO = p.PERSONALID " +
				"LEFT JOIN Scheme s ON s.SchemeId = sm.SCHEMEID " +
				"WHERE p.MOBILE = ? AND sm.DOCLOSE IS NULL";

		@SuppressWarnings("deprecation")
		List<PhoneSearchRegNoGroupCode> phoneSearchRegNoGroupCode = firstJdbcTemplate.query(
				sql,
				new Object[]{phoneNo}, // Correctly passing the parameter
				new BeanPropertyRowMapper<>(PhoneSearchRegNoGroupCode.class)
		);

		return phoneSearchRegNoGroupCode;
	}



//	public Map<String, Object> getRateOFGoldAndSliver() {
//		String sql = "  SELECT METALID, PURITY, PRATE \n" +
//				"                FROM RATEMAST \n" +
//				"                WHERE RATEGROUP = (SELECT MAX(RATEGROUP)\n" +
//				"                                   FROM RATEMAST) \n" +
//				"                AND ((METALID = 'G' AND PURITY = '91.60') OR \n" +
//				"                   (METALID = 'P' AND PURITY = '95.00') OR \n" +
//				"                    (METALID = 'S' AND PURITY = '91.60'))";
//
//		List<Map<String, Object>> results = firstJdbcTemplate.query(sql, (rs, rowNum) -> {
//			Map<String, Object> row = new HashMap<>();
//			row.put("METALID", rs.getString("METALID"));
//			row.put("PRATE", rs.getFloat("PRATE"));
//			return row;
//		});
//
//		Map<String, Object> finalResult = new HashMap<>();
//
//		for (Map<String, Object> row : results) {
//			String metalId = (String) row.get("METALID");
//			float rate = (float) row.get("PRATE");
//
//			if ("G".equals(metalId)) {
//				finalResult.put("GOLDRATE", rate);
//			} else if ("S".equals(metalId)) {
//				finalResult.put("SILVERRATE", rate);
//			}
//		}
//
//		return finalResult;
//	}

	public Map<String, Object> getRateOfGoldAndSliver() {
		String sql = "select * from [VJCsavings]..RateMast where rateid in (select max(rateid) from [VJCsavings]..RateMast) ";
		return firstJdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
			Map<String, Object> map = new HashMap<>();
			map.put("Rate", rs.getFloat("Rate"));
			map.put("SILVERRATE", rs.getFloat("SILVERRATE"));
			return map;
		});
	}

	public String insertEntry(String groupCode, String regNo, String rDate, String amount, String modePay,
							  String accCode, String updateTime, String installment ,String userID,boolean snoCreate){
		if(snoCreate){
			return insertEntry(groupCode, regNo,  rDate,  amount,  modePay,
					accCode,  updateTime,  installment , userID,
					executeGetSnoScheme("SCHEMECOLLECT", "VJCSH0708"));
		}
		else {
			return null;
		}
	}


	public String insertEntry(String groupCode, String regNo, String rDate, String amount, String modePay,
			String accCode, String updateTime, String installment ,String userID,String sno) {

		String receiptNoQuery = "SELECT StartReceiptNo FROM Company ";

		String refNoQuery = "SELECT ctlText FROM  SOFTCONTROL  WHERE CTLID = 'ENTREFNO' ";

		String insertQuerySchemeCollect = "INSERT INTO SCHEMECOLLECT (GROUPCODE, REGNO, RECEIPTNO, RDATE, AMOUNT, MODEPAY, ACCODE, "
				+ "ENTREFNO, CANCEL, SYSTEMID, UPDATETIME, USERID, BOOKNO, COSTID, APPVER, TRANMODE, SC_ID, SNO) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		String insertQuerySchemeTran = "INSERT INTO SCHEMETRAN " + "(GROUPCODE," + " REGNO," + " AMOUNT, " + "WEIGHT, "
				+ "SWEIGHT, " + "RATE, " + "SRATE, " + "RECEIPTNO, " + "RDATE, " + "CANCEL, " + "SYSTEMID, "
				+ "INSTALLMENT, " + "EMPID, " + "REMARKS, " + "ENTREFNO, " + "CPERSON, " + "USERID, " + "UPDATETIME, "
				+ "BOOKNO, " + "COSTID, " + "Msno, " + "BONUSAMount, " + "BonusWeight, " + "APPVER, " + "ST_ID, "
				+ "SNO ," + "ACTUALDATE, " + "TAX, " + "SGST, " + "CGST, " + "IGST, " + "INSAMOUNT " + ") "
				+ "VALUES (?, ?, ? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? "
				+ ",?  ,?  ,?  ,?  ,?  ,?  )";

		String wtCalculate ="SELECT s.WeightLedger FROM SCHEMEMAST sm "
				+ "LEFT JOIN Scheme s on s.SchemeId =sm.SCHEMEID "
				+ "WHERE sm.GROUPCODE = ? and  sm.REGNO = ? ";

		@SuppressWarnings("deprecation")
		String wtFlag =firstJdbcTemplate.queryForObject(wtCalculate,new Object[]{groupCode, regNo},String.class );

		Map<String, Object> result = firstJdbcTemplate.queryForObject("SELECT Rate, SILVERRATE FROM RateMast "
				+ "WHERE rateid = (SELECT MAX(rateid) FROM RateMast)", (rs, rowNum) -> {
		    Map<String, Object> map = new HashMap<>();
		    String rate = Float.toString(rs.getFloat("Rate"));
		    String sRate = Float.toString(rs.getFloat("SILVERRATE"));
		    map.put("Rate", rate);
		    map.put("SILVERRATE", sRate);
		    return map;
		});

		String receiptNo = firstJdbcTemplate.queryForObject(receiptNoQuery, String.class);
		String entRefNo = firstJdbcTemplate.queryForObject(refNoQuery, String.class);;
		String cancel = "";
		String systemId = "";
		String bookNo = "0";
		String costId = "";
		String appVer = "19.12.10.1";
		String tranMode = "D";
		String scId = entRefNo;
		String snoSchemeCollect = sno;
		String rate = (String) result.get("Rate");
		String sRate = (String) result.get("SILVERRATE");
		String empId = "1";
		String remark = "";
		String cPerson = "0";
		String msno = "0";
		String BONUSAMount = "0";
		String BonusWeight = "0";
		String ActualDate = " ";
		String tax = "0";
		String sgst = "0";
		String cgst = "0";
		String igst = "0";
		String insAmount = "0";
		String weight="0";
		String sWeight="0";

		if(wtFlag.equals("Y")) {
			float xrate = Float.parseFloat(result.get("Rate").toString());
		    float silverRate = Float.parseFloat(result.get("SILVERRATE").toString());
		    int parsedAmount = Integer.parseInt(amount);
		    weight = String.format("%.3f", (parsedAmount / xrate));
		    sWeight = String.format("%.3f", (parsedAmount /silverRate ));
		}

		try {
			secondJdbcTemplate.update(insertQuerySchemeCollect, groupCode, regNo, receiptNo, rDate, amount, modePay,
					accCode, entRefNo, cancel, systemId, updateTime, userID, bookNo, costId, appVer, tranMode, scId,
					snoSchemeCollect);

			secondJdbcTemplate.update(insertQuerySchemeTran, groupCode, regNo, amount, weight, sWeight, rate, sRate,
					receiptNo, rDate, cancel, systemId, installment, empId, remark, entRefNo, cPerson, userID,
					updateTime, bookNo, costId, msno, BONUSAMount, BonusWeight, appVer, scId, snoSchemeCollect,
					ActualDate, tax, sgst, cgst, igst, insAmount);

			firstJdbcTemplate.update(
			    "UPDATE SOFTCONTROL SET CTLTEXT = ? WHERE CTLID = 'ENTREFNO' AND CTLTEXT = ?",
			    Integer.parseInt(entRefNo) + 1,
			    Integer.parseInt(entRefNo)
			);

			firstJdbcTemplate.update(
			    "UPDATE Company SET StartReceiptNo = ? WHERE StartReceiptNo = ?",
			    Integer.parseInt(receiptNo) +1,
			    Integer.parseInt(receiptNo)
			);
			System.out.println("A new record was inserted successfully!");
			return "Success";
		} catch (Exception e) {
			System.err.println("An error occurred while inserting the record: " + e.getMessage());
			e.printStackTrace();
			return "Error";
		}

	}

	public String executeGetSnoScheme(String tableName, String dbName) {
		SimpleJdbcCall jdbcCall = new SimpleJdbcCall(secondJdbcTemplate)
				.withProcedureName("GET_SNO_SCHEME")
				.declareParameters(
						new SqlParameter("COSTID", Types.VARCHAR),
						new SqlParameter("CTLID", Types.VARCHAR),
						new SqlParameter("CHECKDB", Types.VARCHAR),
						new SqlParameter("CHECKTABLENAME", Types.VARCHAR),
						new SqlParameter("COMPANYID", Types.VARCHAR),
						new SqlOutParameter("RETVALUE", Types.VARCHAR)  // Declare output parameter
				);

		Map<String, Object> inParams = new HashMap<>();
		inParams.put("COSTID", ""); // @COSTID
		inParams.put("CTLID", "SCHEMETRANCODE"); // @CTLID
		inParams.put("CHECKDB", dbName); // @CHECKDB
		inParams.put("CHECKTABLENAME", tableName); // @CHECKTABLENAME
		inParams.put("COMPANYID", "1"); // @COMPANYID

		// Execute the stored procedure
		Map<String, Object> outParams = jdbcCall.execute(inParams);

		// Retrieve the output parameter value
		return (String) outParams.get("RETVALUE");
	}

	public List<Map<String, Object>> getTranType() {
		String sql = "SELECT NAME, CARDTYPE, ACCOUNT FROM CREDITCARD WHERE ACTIVE = 'Y'";

		// Fetch and return only active credit cards
		return firstJdbcTemplate.queryForList(sql);
	}


	@Transactional
	public boolean deleteMember(Long id) {
		try {
			// 1. Update SCHEMEMAST -> set active status to NULL
			firstJdbcTemplate.update("UPDATE SCHEMEMAST SET ACTIVE = INACTIVE WHERE SNO = ?", id);

			// 2. Update PERSONALINFO -> set active status to NULL
			firstJdbcTemplate.update("UPDATE PERSONALINFO SET ACTIVE = INACTIVE WHERE PERSONALID = ?", id);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}


}