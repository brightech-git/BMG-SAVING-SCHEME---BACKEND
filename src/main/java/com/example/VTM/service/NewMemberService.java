package com.example.VTM.service;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.VTM.model.NewMember.CreateSchemeSummary;
import com.example.VTM.model.NewMember.NMData;
import com.example.VTM.model.NewMember.NewMember;
import com.example.VTM.model.SchemeCollectInsert;
import com.example.VTM.service.utils.CustomQueryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NewMemberService{

	private static final Logger logger = LoggerFactory.getLogger(NewMemberService.class);

	private  final JdbcTemplate firstJdbcTemplate;
	private  final JdbcTemplate secondJdbcTemplate;
	private final AccountService accountService;
	private final CustomQueryUtils customQueryUtils;

	@Autowired
	public NewMemberService(@Qualifier("firstJdbcTemplate") JdbcTemplate firstJdbcTemplate,
							@Qualifier("secondJdbcTemplate") JdbcTemplate secondJdbcTemplate,
							@Qualifier("accountService") AccountService accountService,
							@Qualifier("customQueryUtils")CustomQueryUtils customQueryUtils) {
		this.firstJdbcTemplate=firstJdbcTemplate;
		this.secondJdbcTemplate=secondJdbcTemplate;
		this.accountService=accountService;
		this.customQueryUtils=customQueryUtils;
	}


	public List<Map<String,Object>> getScheme(){
		return 	customQueryUtils.customQueryBuilderForListOfObject("SELECT SchemeId,schemeName, SchemeSName FROM " +
				" Scheme WHERE ACTIVE ='y' ORDER BY SchemeId",firstJdbcTemplate);
	}

	public List<Map<String,Object>> getSchemeAmount(String schemeId){
		return customQueryUtils.customQueryBuilderForListOfObject("SELECT AMOUNT,GROUPCODE,CURRENTREGNO FROM INSAMOUNT WHERE " +
				" SCHEMEID = ? AND ACTIVE ='Y' ",firstJdbcTemplate, new String[]{schemeId});
	}

	public  String createNewMember(NMData nmData,boolean flag){
		String sno= accountService.executeGetSnoScheme("SCHEMECOLLECT", "BMGSH0708");
		System.out.println("Sno number - "+sno);
		return createNewMember(nmData,sno);
//			return "Ntg";
	}

	@Transactional
	public String createNewMember(NMData nmData, String sno) {
		Logger logger = LoggerFactory.getLogger(this.getClass());

		NewMember newMember = nmData.getNewMember();
		CreateSchemeSummary newScheme = nmData.getCreateSchemeSummary();
		SchemeCollectInsert schemeCollectInsert = nmData.getSchemeCollectInsert();

		String groupCode = newScheme.getGroupCode();
		String regNo = newScheme.getRegNo();
		String joinDate = newScheme.getJoinDate();
		String openingDate = newScheme.getOpeningDate();
		String userId2 = newScheme.getUserId();
		if (userId2 == null || userId2.isEmpty()) userId2 = "1"; // default user ID

		String totalIns = newScheme.getTotalIns();
		int nextRegNo = Integer.parseInt(regNo) + 1;

		String amount = schemeCollectInsert.getAmount();
		String modePay = schemeCollectInsert.getModePay();
		String accCode = schemeCollectInsert.getAccCode();

		String personalId = null;
		int nextPersonalId = 0;

		try {
			// --- Step 1: Generate unique PERSONALID ---
			personalId = firstJdbcTemplate.queryForObject(
					"SELECT CTLTEXT FROM SOFTCONTROL WITH (UPDLOCK, ROWLOCK) WHERE CTLID='MASTERCODE'",
					String.class
			);
			nextPersonalId = Integer.parseInt(personalId) + 1;

			logger.info("Generated PERSONALID={}", personalId);

			// --- Step 2: Insert into PERSONALINFO ---
			String queryPersonalInfo = "INSERT INTO PERSONALINFO (" +
					"PERSONALID, TITLE, INITIAL, PNAME, SNAME, DOORNO, ADDRESS1, ADDRESS2, AREA, CITY, STATE, COUNTRY, PINCODE, " +
					"PHONERES, PHONERES2, STDCODE1, STDCODE2, MOBILE, MOBILE2, NEEDSMS1, NEEDSMS2, NEEDEMAIL, EMAIL, FAX, " +
					"UPDATETIME, USERID, PREVILEGEID, COSTID, SMS_SEND, APPVER, NOMENI, DOB, IDPROOF, IDPROOFNO" +
					") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

			Object[] personalParams = {
					personalId,
					newMember.getTitle(),
					newMember.getInitial(),
					newMember.getpName(),
					newMember.getsName() != null ? newMember.getsName() : "",
					newMember.getDoorNo(),
					newMember.getAddress1(),
					newMember.getAddress2(),
					newMember.getArea(),
					newMember.getCity(),
					newMember.getState(),
					newMember.getCountry(),
					newMember.getPinCode(),
					"", "", "", "",
					newMember.getMobile(),
					"",
					"N", "N", "N",
					newMember.getEmail(),
					"",
					newMember.getUpDateTime(),
					userId2, // safe
					"", "", "N", "WEB", "",
					newMember.getDob(),
					newMember.getIdProof(),
					newMember.getIdProofNo()
			};

			firstJdbcTemplate.update(queryPersonalInfo, personalParams);
			logger.info("Inserted PERSONALINFO for PERSONALID={}", personalId);

			// --- Step 3: Insert into SCHEMEMAST ---
			String querySchemeMast = "INSERT INTO SCHEMEMAST (" +
					"COMPANYID, SCHEMEID, GROUPCODE, REGNO, JOINDATE, IEMP, IGROUPCODE, IREGNO, " +
					"HOMECOLLECT, REMARK, SIGNATUREPATH, UPDATETIME, USERID, OPENINGDATE, SNO, " +
					"COSTID, TOTALINS, INTRO, TOTALQTY, APPVER, PREVILEGEID, INSAMOUNT" +
					") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

			firstJdbcTemplate.update(querySchemeMast,
					"BMG",
					newScheme.getSchemeId(),
					groupCode,
					regNo,
					joinDate,
					"1",
					"",
					"0",
					"N",
					"",
					"",
					newMember.getUpDateTime(),
					userId2, // safe
					openingDate,
					personalId,
					"",
					totalIns,
					"0",
					"1",
					"WEB",
					"",
					new BigDecimal(amount)
			);
			logger.info("Inserted SCHEMEMAST for GROUPCODE={} REGNO={}", groupCode, regNo);

			// --- Step 4: Insert into SCHEMECOLLECT ---
			accountService.insertEntry(
					groupCode,
					regNo,
					newMember.getUpDateTime(),
					amount,
					modePay,
					accCode,
					newMember.getUpDateTime(),
					"1",
					userId2, // safe
					sno
			);
			logger.info("Inserted SCHEMECOLLECT for GROUPCODE={} REGNO={}", groupCode, regNo);

			// --- Step 5: Update counters ---
			firstJdbcTemplate.update(
					"UPDATE SOFTCONTROL SET CTLTEXT=? WHERE CTLID='MASTERCODE'",
					nextPersonalId
			);
			firstJdbcTemplate.update(
					"UPDATE INSAMOUNT SET CURRENTREGNO=? WHERE SCHEMEID=? AND ACTIVE='Y' AND GROUPCODE=?",
					nextRegNo, newScheme.getSchemeId(), groupCode
			);

			// --- Step 6: Prepare response ---
			Map<String, Object> response = new HashMap<>();
			response.put("status", "Success");
			response.put("personalId", personalId);
			response.put("schemeId", newScheme.getSchemeId());
			response.put("groupCode", groupCode);
			response.put("regNo", regNo);
			response.put("joinDate", joinDate);
			response.put("openingDate", openingDate);
			response.put("upDateTime", newScheme.getUpdateTime() != null ? newScheme.getUpdateTime() : newMember.getUpDateTime());
			response.put("userId", userId2);
			response.put("totalIns", totalIns);
			response.put("amount", amount);
			response.put("modePay", modePay);
			response.put("accCode", accCode);
			response.put("sno", sno);

			return response.toString();

		} catch (DataAccessException dae) {
			logger.error("Database error inserting new member: {}", dae.getMessage(), dae);
			throw dae; // Will trigger rollback
		} catch (Exception e) {
			logger.error("Unexpected error inserting new member: {}", e.getMessage(), e);
			throw new RuntimeException("Failed to insert new member", e); // rollback
		}
	}



}
