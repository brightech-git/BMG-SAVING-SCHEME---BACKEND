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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;


import java.sql.Timestamp;
import java.sql.Types;
import java.util.*;

@Service
public class AccountService {
	private final JdbcTemplate firstJdbcTemplate;
	private final JdbcTemplate secondJdbcTemplate;
	private final JdbcTemplate fourthJdbcTemplate;
	private final CustomQueryUtils customQueryUtils;

	@Autowired
	public AccountService(@Qualifier("firstJdbcTemplate") JdbcTemplate firstJdbcTemplate,
                          @Qualifier("secondJdbcTemplate") JdbcTemplate secondJdbcTemplate, @Qualifier("fourthJdbcTemplate")JdbcTemplate fourthJdbcTemplate, @Qualifier("customQueryUtils") CustomQueryUtils customQueryUtils) {
		this.firstJdbcTemplate = firstJdbcTemplate;
		this.secondJdbcTemplate = secondJdbcTemplate;
        this.fourthJdbcTemplate = fourthJdbcTemplate;
        this.customQueryUtils=customQueryUtils;
	}


//	public PPData ppData(int regNo, String groupCode) {
//		PPData ppData = new PPData();
//
//		//PersonalInfo
//		String sql1 = "SELECT " + "p.PERSONALID, " + "p.PNAME, " + "p.DOORNO, " + "p.ADDRESS1, " + "p.PINCODE, "
//				+ "p.MOBILE " + "FROM " + "PERSONALINFO AS p " + "LEFT JOIN " + "SCHEMEMAST AS s "
//				+ "ON s.sno = p.PERSONALID " + "WHERE " + "s.REGNO = ? " + "AND s.GROUPCODE = ?;";
//		@SuppressWarnings("deprecation")
//		PersonalInfo personalInfo = firstJdbcTemplate.queryForObject(sql1, new Object[] { regNo, groupCode },
//				new BeanPropertyRowMapper<>(PersonalInfo.class));
//
//		//PaymentHistoryList
//		String sql2 = "SELECT RECEIPTNO,AMOUNT,INSTALLMENT,UPDATETIME FROM SCHEMETRAN  "
//				+ "WHERE REGNO = ? AND GROUPCODE = ? " + "ORDER BY INSTALLMENT";
//		@SuppressWarnings("deprecation")
//		List<PaymentHistory> paymentHistoryList = secondJdbcTemplate.query(sql2, new Object[] { regNo, groupCode },
//				new BeanPropertyRowMapper<>(PaymentHistory.class));
//
//		//SchemeSummary
//		String sql3_section1 = "SELECT s.SchemeId,s.schemeName ,s.SchemeSName ,s.Instalment,s.FixedIns,s.WeightLedger, s.weight, s.sum(weight) as totalweight"
//								+ " from SCHEMEMAST sm   "
//								+ "left join Scheme s on s.SchemeId =sm.SCHEMEID  "
//								+ "where sm.GROUPCODE = ? and  sm.REGNO = ? ";
//		@SuppressWarnings("deprecation")
//		SchemeSummary schemeSummary = firstJdbcTemplate.queryForObject(sql3_section1, new Object[] { groupCode,regNo },
//				new BeanPropertyRowMapper<>(SchemeSummary.class));
//
//		//SchemaSummaryTransBalance
//		String sql3_section2 = "SELECT " + "	SUM(AMOUNT) AMTRECD , " + "	COUNT(INSTALLMENT) INSPaid  " + " FROM "
//				+ "	SCHEMETRAN " + " WHERE " + "	REGNO = ?  " + "	and GROUPCODE = ?  " + " GROUP BY " + "	REGNO, "
//				+ "	GROUPCODE ";
//		@SuppressWarnings("deprecation")
//		SchemaSummaryTransBalance schemaSummaryTransBalance = secondJdbcTemplate.queryForObject(sql3_section2,
//				new Object[] { regNo, groupCode }, new BeanPropertyRowMapper<>(SchemaSummaryTransBalance.class));
//
//		// SchemeClosedSummary
//		String sql4="SELECT sm.DOCLOSE,sm.BILLNO,u.USERNAME,e.EMPNAME FROM SCHEMEMAST sm "
//				+ "LEFT JOIN USERMASTER u on u.USERID =sm.USERID "
//				+ "LEFT JOIN EMPMASTER e on e.EMPID =sm.IEMP "
//				+ "WHERE GROUPCODE = ? AND REGNO = ?" ;
//		@SuppressWarnings("deprecation")
//		SchemeClosedSummary schemeClosedSummary  = firstJdbcTemplate.queryForObject(sql4,
//				new Object[] {groupCode, regNo  }, new BeanPropertyRowMapper<>(SchemeClosedSummary.class));
//
//		//Last Paid Date
//		String sql5="SELECT MAX(UPDATETIME) FROM SCHEMETRAN "
//				+ "WHERE REGNO = ? AND GROUPCODE = ? ";
//		@SuppressWarnings("deprecation")
//		String lastPaidDate = secondJdbcTemplate.queryForObject(sql5, new Object[]{regNo, groupCode}, String.class);
//
//		//Last Paid Date
//		if(schemeSummary.getFixedIns().equals("Y")) {
//			String getfixedAmount="SELECT TOP 1 Amount FROM SCHEMETRAN WHERE GROUPCODE= ? AND regno=? ";
//			@SuppressWarnings("deprecation")
//			Optional<String> fixedAmount = Optional.ofNullable(secondJdbcTemplate.queryForObject
//								(getfixedAmount, new Object[]{groupCode ,regNo}, String.class));
//			if(fixedAmount.equals(null))
//				ppData.setAmount(null);
//			else
//				ppData.setAmount(fixedAmount.get());
//		}
//
//		schemeSummary.setSchemaSummaryTransBalance(schemaSummaryTransBalance);
//		ppData.setPersonalInfo(personalInfo);
//		ppData.setSchemeClosedSummary(schemeClosedSummary);
//		ppData.setPaymentHistoryList(paymentHistoryList);
//		ppData.setSchemeSummary(schemeSummary);
//		ppData.setLastPaidDate(lastPaidDate);
//		return ppData;
//	}


	public PPData ppData(int regNo, String groupCode) {
		PPData ppData = new PPData();

		// ------------------- PersonalInfo -------------------
		String sqlPersonal = """
        SELECT p.PERSONALID, p.PNAME, p.DOORNO, p.ADDRESS1, p.PINCODE, p.MOBILE
        FROM PERSONALINFO AS p
        LEFT JOIN SCHEMEMAST AS s ON s.SNO = p.PERSONALID
        WHERE s.REGNO = ? AND s.GROUPCODE = ?
    """;
		PersonalInfo personalInfo = firstJdbcTemplate.queryForObject(
				sqlPersonal,
				new Object[]{regNo, groupCode},
				new BeanPropertyRowMapper<>(PersonalInfo.class)
		);
		ppData.setPersonalInfo(personalInfo);

		// ------------------- PaymentHistoryList -------------------
		String sqlPayments = """
        SELECT RECEIPTNO, AMOUNT, INSTALLMENT, UPDATETIME, WEIGHT
        FROM SCHEMETRAN
        WHERE REGNO = ? AND GROUPCODE = ?
        ORDER BY INSTALLMENT
    """;
		List<PaymentHistory> paymentHistoryList = secondJdbcTemplate.query(
				sqlPayments,
				new Object[]{regNo, groupCode},
				new BeanPropertyRowMapper<>(PaymentHistory.class)
		);
		ppData.setPaymentHistoryList(paymentHistoryList);

		// ------------------- SchemeSummary -------------------
		String sqlScheme = """
        SELECT s.SchemeId, s.SchemeName, s.SchemeSName, s.Instalment, s.FixedIns, s.WeightLedger
        FROM SCHEMEMAST sm
        LEFT JOIN Scheme s ON s.SchemeId = sm.SCHEMEID
        WHERE sm.GROUPCODE = ? AND sm.REGNO = ?
    """;
		SchemeSummary schemeSummary = firstJdbcTemplate.queryForObject(
				sqlScheme,
				new Object[]{groupCode, regNo},
				new BeanPropertyRowMapper<>(SchemeSummary.class)
		);

		// ------------------- Total Weight and Last Weight -------------------
		String sqlTotalWeight = "SELECT COALESCE(SUM(WEIGHT),0) FROM SCHEMETRAN WHERE REGNO = ? AND GROUPCODE = ?";
		String totalWeight = secondJdbcTemplate.queryForObject(sqlTotalWeight, new Object[]{regNo, groupCode}, String.class);
		schemeSummary.setTotalWeight(totalWeight);

		String sqlLastWeight = "SELECT TOP 1 WEIGHT FROM SCHEMETRAN WHERE REGNO = ? AND GROUPCODE = ? ORDER BY UPDATETIME DESC";
		String lastWeight = null;
		try {
			lastWeight = secondJdbcTemplate.queryForObject(sqlLastWeight, new Object[]{regNo, groupCode}, String.class);
		} catch (EmptyResultDataAccessException e) {
			lastWeight = null;
		}
		schemeSummary.setLastWeight(lastWeight);

		// ------------------- SchemaSummaryTransBalance -------------------
		String sqlTransBalance = """
        SELECT SUM(AMOUNT) AS amtrecd, COUNT(INSTALLMENT) AS insPaid
        FROM SCHEMETRAN
        WHERE REGNO = ? AND GROUPCODE = ?
        GROUP BY REGNO, GROUPCODE
    """;
		SchemaSummaryTransBalance schemaSummaryTransBalance = null;
		try {
			schemaSummaryTransBalance = secondJdbcTemplate.queryForObject(
					sqlTransBalance,
					new Object[]{regNo, groupCode},
					new BeanPropertyRowMapper<>(SchemaSummaryTransBalance.class)
			);
		} catch (EmptyResultDataAccessException e) {
			schemaSummaryTransBalance = new SchemaSummaryTransBalance(); // default empty
		}
		schemeSummary.setSchemaSummaryTransBalance(schemaSummaryTransBalance);

		// ------------------- SchemeClosedSummary -------------------
		String sqlClosed = """
        SELECT sm.DOCLOSE AS doClose, sm.BILLNO AS billNo, u.USERNAME AS userName, e.EMPNAME AS empName
        FROM SCHEMEMAST sm
        LEFT JOIN USERMASTER u ON u.USERID = sm.USERID
        LEFT JOIN EMPMASTER e ON e.EMPID = sm.IEMP
        WHERE GROUPCODE = ? AND REGNO = ?
    """;
		SchemeClosedSummary schemeClosedSummary = firstJdbcTemplate.queryForObject(
				sqlClosed,
				new Object[]{groupCode, regNo},
				new BeanPropertyRowMapper<>(SchemeClosedSummary.class)
		);

		// ------------------- Last Paid Date -------------------
		String sqlLastPaid = "SELECT MAX(UPDATETIME) FROM SCHEMETRAN WHERE REGNO = ? AND GROUPCODE = ?";
		String lastPaidDate = null;
		try {
			lastPaidDate = secondJdbcTemplate.queryForObject(sqlLastPaid, new Object[]{regNo, groupCode}, String.class);
		} catch (EmptyResultDataAccessException e) {
			lastPaidDate = null;
		}

		// ------------------- Fixed Installment Amount -------------------
		String amount = null;
		if ("Y".equalsIgnoreCase(schemeSummary.getFixedIns())) {
			String sqlFixedAmount = "SELECT TOP 1 AMOUNT FROM SCHEMETRAN WHERE REGNO = ? AND GROUPCODE = ? ORDER BY UPDATETIME DESC";
			try {
				amount = secondJdbcTemplate.queryForObject(sqlFixedAmount, new Object[]{regNo, groupCode}, String.class);
			} catch (EmptyResultDataAccessException e) {
				amount = null;
			}
		}

		// ------------------- Set All PPData Fields -------------------
		ppData.setRegNo(regNo);
		ppData.setGroupCode(groupCode);
  // optional: fetch from SCHEMEMAST
		ppData.setPersonalInfo(personalInfo);
		ppData.setSchemeSummary(schemeSummary);
		ppData.setSchemeClosedSummary(schemeClosedSummary);
		ppData.setPaymentHistoryList(paymentHistoryList);
		ppData.setLastPaidDate(lastPaidDate);
		ppData.setAmount(amount);

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


	public List<PPData> getFullDetailsByPhoneNo(String phoneNo) {

		String sql = """
        SELECT 
            p.PNAME AS pName, 
            sm.REGNO AS regNo, 
            sm.GROUPCODE AS groupCode, 
            DATEADD(MONTH, s.Instalment, sm.JOINDATE) AS maturityDate, 
            sm.JOINDATE AS joinDate
        FROM PERSONALINFO p
        LEFT JOIN SCHEMEMAST sm ON sm.SNO = p.PERSONALID
        LEFT JOIN SCHEME s ON s.SchemeId = sm.SCHEMEID
        WHERE p.MOBILE = ? 
          AND (sm.DOCLOSE IS NULL OR sm.DOCLOSE = '')
    """;

		List<PhoneSearchRegNoGroupCode> phoneSearchList = firstJdbcTemplate.query(
				sql,
				new BeanPropertyRowMapper<>(PhoneSearchRegNoGroupCode.class),
				phoneNo
		);

		List<PPData> resultList = new ArrayList<>();

		for (PhoneSearchRegNoGroupCode item : phoneSearchList) {
			if (item.getRegNo() != null && item.getGroupCode() != null) {
				PPData data;
				try {
					data = ppData(item.getRegNo(), item.getGroupCode());
				} catch (EmptyResultDataAccessException e) {
					continue; // Skip if no data found
				}

				// Set phone search info directly
				data.setRegNo(item.getRegNo());
				data.setGroupCode(item.getGroupCode());
				data.setPName(item.getPName());
				data.setMaturityDate(item.getMaturityDate());
				data.setJoinDate(item.getJoinDate());

				resultList.add(data);
			}
		}

		return resultList;
	}







	public Map<String, Object> getRateOFGoldAndSliver() {
		String sql = "  SELECT METALID, PURITY, PRATE \n" +
				"                FROM RATEMAST \n" +
				"                WHERE RATEGROUP = (SELECT MAX(RATEGROUP)\n" +
				"                                   FROM RATEMAST) \n" +
				"                AND ((METALID = 'G' AND PURITY = '91.60') OR \n" +
				"                   (METALID = 'P' AND PURITY = '95.00') OR \n" +
				"                    (METALID = 'S' AND PURITY = '91.60'))";

		List<Map<String, Object>> results = firstJdbcTemplate.query(sql, (rs, rowNum) -> {
			Map<String, Object> row = new HashMap<>();
			row.put("METALID", rs.getString("METALID"));
			row.put("PRATE", rs.getFloat("PRATE"));
			return row;
		});

		Map<String, Object> finalResult = new HashMap<>();

		for (Map<String, Object> row : results) {
			String metalId = (String) row.get("METALID");
			float rate = (float) row.get("PRATE");

			if ("G".equals(metalId)) {
				finalResult.put("GOLDRATE", rate);
			} else if ("S".equals(metalId)) {
				finalResult.put("SILVERRATE", rate);
			}
		}

		return finalResult;
	}

//	public Map<String, Object> getRateOfGoldAndSliver() {
//		String sql = "select * from [BMGsavings]..RateMast where rateid in (select max(rateid) from [BMGsavings]..RateMast) ";
//		return firstJdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
//			Map<String, Object> map = new HashMap<>();
//			map.put("Rate", rs.getFloat("Rate"));
//			map.put("SILVERRATE", rs.getFloat("SILVERRATE"));
//			return map;
//		});
//	}

//	public String insertEntry(String groupCode, String regNo, String rDate, String amount, String modePay,
//							  String accCode, String updateTime, String installment, String userID, boolean snoCreate) {
//
//		System.out.println("=== insertEntry START ===");
//		System.out.println("groupCode=" + groupCode + ", regNo=" + regNo + ", amount=" + amount);
//
//		if (!snoCreate) {
//			System.out.println("SNO creation disabled, returning null");
//			return null;
//		}
//
//		// Step 1: Generate SNO
//		String sno = executeGetSnoScheme("SCHEMECOLLECT", "BMGSH0708");
//		System.out.println("Generated SNO: " + sno);
//
//		if (sno == null || sno.isEmpty()) {
//			System.out.println("Error: SNO generation failed");
//			return "Error: SNO generation failed";
//		}
//
//		// Step 2: Insert into DB safely
//		try {
//			// Example SQL, replace with your actual insert or select query
//			String checkSql = "SELECT COUNT(*) FROM SCHEMECOLLECT WHERE SNO = ?";
//			Integer count = secondJdbcTemplate.queryForObject(checkSql, new Object[]{sno}, Integer.class);
//
//			if (count == null || count == 0) {
//				System.out.println("No existing record found for SNO: " + sno + ", proceeding to insert.");
//				// Your insert statement
//				String insertSql = "INSERT INTO SCHEMECOLLECT (SNO, GROUPCODE, REGNO, RDATE, AMOUNT, MODEPAY, ACCCODE, UPDATETIME, INSTALLMENT, USERID) " +
//						"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//				int rows = secondJdbcTemplate.update(insertSql, sno, groupCode, regNo, rDate, amount, modePay, accCode, updateTime, installment, userID);
//				System.out.println("Rows inserted: " + rows);
//			} else {
//				System.out.println("Record already exists for SNO: " + sno);
//				return "Record already exists for SNO: " + sno;
//			}
//
//			return sno; // return the generated SNO after successful insert
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			return "Error inserting record: " + e.getMessage();
//		}
//	}



	public String insertEntry(String groupCode, String regNo, String rDate, String amount, String modePay,
							  String accCode, String updateTime, String installment, String userID, boolean snoCreate) {

		System.out.println("=== insertEntry START ===");
		System.out.println("Parameters received -> groupCode: " + groupCode + ", regNo: " + regNo
				+ ", rDate: " + rDate + ", amount: " + amount + ", modePay: " + modePay
				+ ", accCode: " + accCode + ", updateTime: " + updateTime
				+ ", installment: " + installment + ", userID: " + userID
				+ ", snoCreate: " + snoCreate);

		if (!snoCreate) {
			System.out.println("SNO creation is disabled. Exiting method.");
			return null;
		}

		// Step 1: Generate SNO
		String sno = executeGetSnoScheme("SCHEMECOLLECT", "BMGSH0708");
		System.out.println("Generated SNO: " + sno);

		if (sno == null || sno.isEmpty()) {
			System.out.println("Error: SNO generation failed.");
			return "Error: SNO generation failed";
		}

		try {
			// Step 2: Check if SNO already exists
			String checkSql = "SELECT COUNT(*) FROM SCHEMECOLLECT WHERE SNO = ?";
			Integer count = secondJdbcTemplate.queryForObject(checkSql, new Object[]{sno}, Integer.class);
			System.out.println("Existing record count for SNO " + sno + ": " + count);

			if (count != null && count > 0) {
				System.out.println("Record already exists for SNO: " + sno);
				return "Record already exists for SNO: " + sno;
			}

			// Step 3: Insert new record
			String insertSql = "INSERT INTO SCHEMECOLLECT " +
					"(SNO, GROUPCODE, REGNO, RDATE, AMOUNT, MODEPAY, ACCODE, UPDATETIME, INSTALLMENT, USERID) " +
					"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			int rows = secondJdbcTemplate.update(insertSql, sno, groupCode, regNo, rDate, amount, modePay, accCode, updateTime, installment, userID);

			System.out.println("Insert executed. Rows inserted: " + rows);

			return sno; // return the generated SNO after successful insert

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error inserting record: " + e.getMessage());
			return "Error inserting record: " + e.getMessage();
		}
	}


	public String insertEntry(String groupCode, String regNo, String rDate, String amount, String modePay,
							  String accCode, String updateTime, String installment, String userID, String sno) {

		System.out.println("=== insertEntry() START ===");
		System.out.printf("Params -> groupCode=%s, regNo=%s, rDate=%s, amount=%s, modePay=%s, accCode=%s, userID=%s, sno=%s%n",
				groupCode, regNo, rDate, amount, modePay, accCode, userID, sno);

		String receiptNoQuery = "SELECT StartReceiptNo FROM Company";
		String refNoQuery = "SELECT CTLTEXT FROM SOFTCONTROL WHERE CTLID = 'ENTREFNO'";
		String wtCalculate = "SELECT s.WeightLedger FROM SCHEMEMAST sm " +
				"LEFT JOIN Scheme s ON s.SchemeId = sm.SCHEMEID " +
				"WHERE sm.GROUPCODE = ? AND sm.REGNO = ?";

		String insertQuerySchemeCollect = "INSERT INTO SCHEMECOLLECT " +
				"(GROUPCODE, REGNO, RECEIPTNO, RDATE, AMOUNT, MODEPAY, ACCODE, ENTREFNO, CANCEL, SYSTEMID, " +
				"UPDATETIME, USERID, BOOKNO, COSTID, APPVER, TRANMODE, SC_ID, SNO) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		String insertQuerySchemeTran = "INSERT INTO SCHEMETRAN " +
				"(GROUPCODE, REGNO, AMOUNT, WEIGHT, SWEIGHT, RATE, SRATE, RECEIPTNO, RDATE, CANCEL, " +
				"SYSTEMID, INSTALLMENT, EMPID, REMARKS, ENTREFNO, CPERSON, USERID, UPDATETIME, BOOKNO, COSTID, " +
				"Msno, BONUSAMount, BonusWeight, APPVER, ST_ID, SNO, ACTUALDATE, TAX, SGST, CGST, IGST, INSAMOUNT) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";


		try {
			// Step 1: Fetch Receipt No
			String receiptNo = firstJdbcTemplate.queryForObject(receiptNoQuery, String.class);

			// Step 2: Fetch ENTREFNO
			String entRefNo = firstJdbcTemplate.queryForObject(refNoQuery, String.class);

			if (receiptNo == null || entRefNo == null) {
				return "Error: Missing receipt or ENTREFNO";
			}

			// Step 3: Weight Ledger
			String wtFlag = "N";
			List<String> wtList = firstJdbcTemplate.queryForList(wtCalculate, new Object[]{groupCode, regNo}, String.class);
			if (!wtList.isEmpty()) wtFlag = wtList.get(0);

			// Step 4: Fetch gold & silver rates
			String sql = "SELECT METALID, PURITY, PRATE FROM RATEMAST " +
					"WHERE RATEGROUP = (SELECT MAX(RATEGROUP) FROM RATEMAST) " +
					"AND ((METALID = 'G' AND PURITY = '91.60') OR " +
					"     (METALID = 'P' AND PURITY = '95.00') OR " +
					"     (METALID = 'S' AND PURITY = '91.60'))";

			Map<String, Float> rateMap = new HashMap<>();
			List<Map<String, Object>> results = fourthJdbcTemplate.query(sql, (rs, rowNum) -> {
				Map<String, Object> row = new HashMap<>();
				row.put("METALID", rs.getString("METALID"));
				row.put("PRATE", rs.getFloat("PRATE"));
				return row;
			});

			for (Map<String, Object> row : results) {
				String metalId = (String) row.get("METALID");
				float rate = (float) row.get("PRATE");
				if ("G".equals(metalId)) rateMap.put("GOLDRATE", rate);
				else if ("S".equals(metalId)) rateMap.put("SILVERRATE", rate);
			}

			float xRate = rateMap.getOrDefault("GOLDRATE", 0f);
			float silverRate = rateMap.getOrDefault("SILVERRATE", 0f);

			String weight = "0", sWeight = "0";
			if ("Y".equalsIgnoreCase(wtFlag)) {
				int amt = Integer.parseInt(amount);
				weight = String.format("%.3f", amt / xRate);
				sWeight = String.format("%.3f", amt / silverRate);
			}

			System.out.println("Rates -> GoldRate=" + xRate + ", SilverRate=" + silverRate + ", WeightFlag=" + wtFlag);
			System.out.println("Calculated weights -> G=" + weight + ", S=" + sWeight);

			// === Constants ===
			String cancel = "", systemId = "", bookNo = "0", costId = "";
			String appVer = "19.12.10.1", tranMode = "D", scId = entRefNo, remark = "";
			int empIdInt = 1;
			int cPersonInt = 0;
			int msnoInt = 0;
			double bonusAmount = 0, bonusWeight = 0, tax = 0, sgst = 0, cgst = 0, igst = 0, insAmount = 0;
			String actualDate = " ";

			// Step 5: Insert SCHEMECOLLECT
			int rows1 = secondJdbcTemplate.update(insertQuerySchemeCollect,
					groupCode, regNo, receiptNo, rDate, amount, modePay, accCode, entRefNo,
					cancel, systemId, updateTime, Integer.parseInt(userID), bookNo, costId, appVer, tranMode, scId, sno);
			System.out.println("✅ SCHEMECOLLECT rows inserted: " + rows1);

			// Step 6: Insert SCHEMETRAN
			int stId = 0; // default ST_ID if you don't have a value
			int rows2 = secondJdbcTemplate.update(insertQuerySchemeTran,
					groupCode, regNo, amount, weight, sWeight, xRate, silverRate,
					receiptNo, rDate, cancel, systemId, Integer.parseInt(installment), empIdInt, remark,
					entRefNo, cPersonInt, Integer.parseInt(userID), updateTime, bookNo, costId, msnoInt,
					bonusAmount, bonusWeight, appVer, stId, sno, actualDate,
					tax, sgst, cgst, igst, insAmount
			);
			System.out.println("✅ SCHEMETRAN rows inserted: " + rows2);

			// Step 7: Update counters
			firstJdbcTemplate.update("UPDATE SOFTCONTROL SET CTLTEXT = ? WHERE CTLID = 'ENTREFNO'", Integer.parseInt(entRefNo) + 1);
			firstJdbcTemplate.update("UPDATE Company SET StartReceiptNo = ? WHERE StartReceiptNo = ?", Integer.parseInt(receiptNo) + 1, Integer.parseInt(receiptNo));

			System.out.println("=== insertEntry() COMPLETED SUCCESSFULLY ===");
			return "Success";

		} catch (Exception e) {
			System.err.println("❌ Error inserting record: " + e.getMessage());
			e.printStackTrace();
			return "Error: " + e.getMessage();
		}
	}





//	public String insertEntry(String groupCode, String regNo, String rDate, String amount, String modePay,
//							  String accCode, String updateTime, String installment, String userID, String sno) {
//
//		System.out.println("=== insertEntry() START ===");
//		System.out.printf("Params -> groupCode=%s, regNo=%s, rDate=%s, amount=%s, modePay=%s, accCode=%s, userID=%s, sno=%s%n",
//				groupCode, regNo, rDate, amount, modePay, accCode, userID, sno);
//
//		String receiptNoQuery = "SELECT StartReceiptNo FROM Company";
//		String refNoQuery = "SELECT CTLTEXT FROM SOFTCONTROL WHERE CTLID = 'ENTREFNO'";
//		String wtCalculate = "SELECT s.WeightLedger FROM SCHEMEMAST sm " +
//				"LEFT JOIN Scheme s ON s.SchemeId = sm.SCHEMEID " +
//				"WHERE sm.GROUPCODE = ? AND sm.REGNO = ?";
//
//		String insertQuerySchemeCollect = "INSERT INTO SCHEMECOLLECT " +
//				"(GROUPCODE, REGNO, RECEIPTNO, RDATE, AMOUNT, MODEPAY, ACCODE, ENTREFNO, CANCEL, SYSTEMID, " +
//				"UPDATETIME, USERID, BOOKNO, COSTID, APPVER, TRANMODE, SC_ID, SNO) " +
//				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//
//		String insertQuerySchemeTran = "INSERT INTO SCHEMETRAN " +
//				"(GROUPCODE, REGNO, AMOUNT, WEIGHT, SWEIGHT, RATE, SRATE, RECEIPTNO, RDATE, CANCEL, " +
//				"SYSTEMID, INSTALLMENT, EMPID, REMARKS, ENTREFNO, CPERSON, USERID, UPDATETIME, BOOKNO, COSTID, " +
//				"Msno, BONUSAMount, BonusWeight, APPVER, ST_ID, SNO, ACTUALDATE, TAX, SGST, CGST, IGST, INSAMOUNT) " +
//				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//
//		try {
//			// === Step 1: Fetch Receipt No ===
//			String receiptNo = firstJdbcTemplate.queryForObject(receiptNoQuery, String.class);
//
//			// === Step 2: Fetch ENTREFNO ===
//			String entRefNo = firstJdbcTemplate.queryForObject(refNoQuery, String.class);
//
//			if (receiptNo == null || entRefNo == null) {
//				return "Error: Missing receipt or ENTREFNO";
//			}
//
//			// === Step 3: Weight Ledger ===
//			String wtFlag = "N";
//			try {
//				List<String> wtList = firstJdbcTemplate.queryForList(wtCalculate, new Object[]{groupCode, regNo}, String.class);
//				if (!wtList.isEmpty()) wtFlag = wtList.get(0);
//			} catch (Exception e) {
//				System.err.println("⚠️ Could not fetch weight ledger: " + e.getMessage());
//			}
//
//			// === Step 4: Fetch gold & silver rates ===
//			String sql = "SELECT METALID, PURITY, PRATE " +
//					"FROM RATEMAST " +
//					"WHERE RATEGROUP = (SELECT MAX(RATEGROUP) FROM RATEMAST) " +
//					"AND ((METALID = 'G' AND PURITY = '91.60') OR " +
//					"     (METALID = 'P' AND PURITY = '95.00') OR " +
//					"     (METALID = 'S' AND PURITY = '91.60'))";
//
//			Map<String, Object> rateMap = new HashMap<>();
//
//			List<Map<String, Object>> results = fourthJdbcTemplate.query(sql, (rs, rowNum) -> {
//				Map<String, Object> row = new HashMap<>();
//				row.put("METALID", rs.getString("METALID"));
//				row.put("PRATE", rs.getFloat("PRATE"));
//				return row;
//			});
//
//			for (Map<String, Object> row : results) {
//				String metalId = (String) row.get("METALID");
//				float rate = (float) row.get("PRATE");
//
//				if ("G".equals(metalId)) {
//					rateMap.put("Rate", rate); // Gold
//				} else if ("S".equals(metalId)) {
//					rateMap.put("SILVERRATE", rate); // Silver
//				}
//			}
//
//			float xRate = ((Number) rateMap.getOrDefault("Rate", 0)).floatValue();
//			float silverRate = ((Number) rateMap.getOrDefault("SILVERRATE", 0)).floatValue();
//
//			String weight = "0", sWeight = "0";
//			if ("Y".equalsIgnoreCase(wtFlag)) {
//				int amt = Integer.parseInt(amount);
//				weight = String.format("%.3f", amt / xRate);
//				sWeight = String.format("%.3f", amt / silverRate);
//			}
//
//			System.out.println("Rates -> GoldRate=" + xRate + ", SilverRate=" + silverRate + ", WeightFlag=" + wtFlag);
//			System.out.println("Calculated weights -> G=" + weight + ", S=" + sWeight);
//
//			// === Step 5: Insert SCHEMECOLLECT ===
//			System.out.println("🟢 Inserting into SCHEMECOLLECT (SNO=" + sno + ")");
//			int rows1 = secondJdbcTemplate.update(insertQuerySchemeCollect,
//					groupCode, regNo, receiptNo, rDate, amount, modePay, accCode, entRefNo,
//					"", "", updateTime, userID, "0", "", "19.12.10.1", "D", entRefNo, sno);
//			System.out.println("✅ SCHEMECOLLECT rows inserted: " + rows1);
//
//			// === Step 6: Insert SCHEMETRAN ===
//			System.out.println("🟢 Inserting into SCHEMETRAN (SNO=" + sno + ")");
//			Object[] params = {
//					groupCode, regNo, amount, weight, sWeight, xRate, silverRate, receiptNo, rDate, "", "", installment,
//					"1", "", entRefNo, "0", userID, updateTime, "0", "", "0", "0", "0", "19.12.10.1",
//					entRefNo, sno, " ", 0, 0, 0, 0, 0, null // ✅ added null for parameter 33
//			};
//
//			System.out.println("🔍 SCHEMETRAN Params Count: " + params.length);
//			int rows2 = secondJdbcTemplate.update(insertQuerySchemeTran, params);
//			System.out.println("✅ SCHEMETRAN rows inserted: " + rows2);
//
//			// === Step 7: Update reference numbers ===
//			System.out.println("🔄 Updating reference numbers...");
//			firstJdbcTemplate.update("UPDATE SOFTCONTROL SET CTLTEXT = ? WHERE CTLID = 'ENTREFNO'", Integer.parseInt(entRefNo) + 1);
//			firstJdbcTemplate.update("UPDATE Company SET StartReceiptNo = ? WHERE StartReceiptNo = ?", Integer.parseInt(receiptNo) + 1, Integer.parseInt(receiptNo));
//
//			System.out.println("=== insertEntry() COMPLETED SUCCESSFULLY ===");
//			return "Success";
//
//		} catch (Exception e) {
//			System.err.println("❌ Error inserting record: " + e.getMessage());
//			e.printStackTrace();
//			return "Error: " + e.getMessage();
//		}
//	}





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
		inParams.put("COMPANYID", "BMG"); // @COMPANYID

		// Execute the stored procedure
		Map<String, Object> outParams = jdbcCall.execute(inParams);

		// Retrieve the output parameter value
		return (String) outParams.get("RETVALUE");
	}

	public List<Map<String, Object>> getTranType() {
		String sql = "SELECT NAME, CARDTYPE, ACCOUNT FROM CREDITCARD where Active = 'Y'";
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


	public Map<String, Object> getRateOfGoldAndSliver() {
		{
			String sql = "  SELECT METALID, PURITY, PRATE \n" +
					"                FROM RATEMAST \n" +
					"                WHERE RATEGROUP = (SELECT MAX(RATEGROUP)\n" +
					"                                   FROM RATEMAST) \n" +
					"                AND ((METALID = 'G' AND PURITY = '91.60') OR \n" +
					"                   (METALID = 'P' AND PURITY = '95.00') OR \n" +
					"                    (METALID = 'S' AND PURITY = '91.60'))";

			List<Map<String, Object>> results = fourthJdbcTemplate.query(sql, (rs, rowNum) -> {
				Map<String, Object> row = new HashMap<>();
				row.put("METALID", rs.getString("METALID"));
				row.put("PRATE", rs.getFloat("PRATE"));
				return row;
			});

			Map<String, Object> finalResult = new HashMap<>();

			for (Map<String, Object> row : results) {
				String metalId = (String) row.get("METALID");
				float rate = (float) row.get("PRATE");

				if ("G".equals(metalId)) {
					finalResult.put("GOLDRATE", rate);
				} else if ("S".equals(metalId)) {
					finalResult.put("SILVERRATE", rate);
				}
			}

			return finalResult;
		}

	}

	}