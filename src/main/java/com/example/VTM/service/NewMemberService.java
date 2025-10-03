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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NewMemberService{

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
			String sno= accountService.executeGetSnoScheme("SCHEMECOLLECT", "VJCSH0708");
			System.out.println("Sno number - "+sno);
			return createNewMember(nmData,sno);
//			return "Ntg";
		}

		public String createNewMember(NMData nmData,String sno) {

			NewMember newMember = nmData.getNewMember();
			CreateSchemeSummary newScheme=nmData.getCreateSchemeSummary();
			SchemeCollectInsert schemeCollectInsert=nmData.getSchemeCollectInsert();

			String PersonalId = customQueryUtils.customeQueryBuilderforString("SELECT CTLTEXT FROM SOFTCONTROL WHERE " +
					" CTLID = 'MASTERCODE'",firstJdbcTemplate);

			String title = newMember.getTitle();
			String initial = newMember.getInitial();
			String pName = newMember.getpName();
			String sName = newMember.getsName()==null ? "" : newMember.getsName() ;
			String doorNo = newMember.getDoorNo();
			String address1 = newMember.getAddress1();
			String address2 = newMember.getAddress2();
			String area = newMember.getArea();
			String city = newMember.getCity();
			String state = newMember.getState();
			String country = newMember.getCountry();
			String pinCode = newMember.getPinCode();
			String mobile = newMember.getMobile();
			String idProof = newMember.getIdProof();
			String idProofNo = newMember.getIdProofNo();
			String upDateTime1 = newMember.getUpDateTime();
			String userId1 = newMember.getUserId();
			String appVer1 = "WEB";
			String dob = newMember.getDob();
			String email = newMember.getEmail();
			String needsms1 = "N";
			String needsms2 = "N";
			String needemail = "N";
			String smsSend = "N";
			String phoneRes = "";
			String phoneRes2 = "";
			String mobile2 = "";
			String fax = "";
			String stdCode1 = "";
			String stdCode2 = "";
			String nomeni = "";
			String previlegeId1= "";
			String costId1= "";

			String sNo = PersonalId;
			String companyId=  "1";
			String schemeId = newScheme.getSchemeId();
			String groupCode = newScheme.getGroupCode();
			String regNo =newScheme.getRegNo();
			String joinDate=newScheme.getJoinDate();
			String upDateTime2=newScheme.getUpdateTime();
			String openingDate = newScheme.getOpeningDate();
			String iEMP = "1";
			String intro="0";
			String iGroupCode = "";
			String iRegNo="0";
			String homeCollect="N";
			String remark = "";
			String signaturePath="";
			String userId2= newScheme.getUserId();
			String costId2="";
			String totalIns=newScheme.getTotalIns();
			String totalQty="1";
			String appVer2="WEB";
			String previlegeId2="";

			String amount=schemeCollectInsert.getAmount();
			String modePay = schemeCollectInsert.getModePay();
			String accCode = schemeCollectInsert.getAccCode();

			try {
				String queryForNewMemberEntry = " INSERT INTO PERSONALINFO ( " +
						"  PERSONALID, TITLE, INITIAL, PNAME, SNAME, DOORNO, " +
						"  ADDRESS1, ADDRESS2, AREA, CITY, STATE, COUNTRY, PINCODE, PHONERES, PHONERES2, " +
						"  STDCODE1, STDCODE2, MOBILE, MOBILE2, NEEDSMS1, NEEDSMS2, NEEDEMAIL, EMAIL, FAX, " +
						"  UPDATETIME, USERID, PREVILEGEID, COSTID, SMS_SEND, APPVER, NOMENI , DOB ,IDPROOF , IDPROOFNO" +
						"  ) " +
						"  VALUES ( " +
						"  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
						"  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,? ,? ,? ,? " +
						"  ) ";
				firstJdbcTemplate.update(queryForNewMemberEntry, PersonalId, title, initial, pName, sName, doorNo, address1, address2,
						area, city, state, country, pinCode, phoneRes, phoneRes2, stdCode1, stdCode2, mobile, mobile2, needsms1, needsms2,
						needemail, email, fax, upDateTime1, userId1, previlegeId1, costId1, smsSend, appVer1, nomeni, dob, idProof, idProofNo);

				String queryForAddSchemeToMember = "INSERT INTO SCHEMEMAST ( " +
						"COMPANYID, SCHEMEID, GROUPCODE, REGNO, JOINDATE, IEMP, IGROUPCODE, IREGNO, " +
						"HOMECOLLECT, REMARK, SIGNATUREPATH, UPDATETIME, USERID, OPENINGDATE, SNO, " +
						"COSTID, TOTALINS, INTRO, TOTALQTY, APPVER, PREVILEGEID, INSAMOUNT " +
						") VALUES ( " +
						"?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? " +
						")";

				firstJdbcTemplate.update(queryForAddSchemeToMember,
						companyId,
						schemeId,
						groupCode,
						regNo,
						joinDate,
						iEMP,
						iGroupCode,
						iRegNo,
						homeCollect,
						remark,
						signaturePath,
						upDateTime2,   // UPDATETIME here
						userId2,       // USERID here
						openingDate,
						sNo,
						costId2,
						totalIns,
						intro,
						totalQty,
						appVer2,
						previlegeId2,
						new BigDecimal(amount)   // INSAMOUNT here
				);




				accountService.insertEntry(groupCode, regNo, upDateTime1, amount, modePay, accCode, upDateTime1, "1", userId2, sno);

				firstJdbcTemplate.update("UPDATE SOFTCONTROL SET CTLTEXT = ? WHERE CTLID = 'MASTERCODE' AND CTLTEXT = ? ",
						Integer.parseInt(PersonalId) + 1, PersonalId);

				firstJdbcTemplate.update("UPDATE INSAMOUNT SET CURRENTREGNO = ? WHERE SCHEMEID = ? and ACTIVE ='Y'  " +
						"and CURRENTREGNO = ? and GROUPCODE = ?  ", Integer.parseInt(regNo) + 1, schemeId, regNo, groupCode);
				Map<String, Object> response = new HashMap<>();
				response.put("status", "Success");
				response.put("personalId", PersonalId);
				response.put("schemeId", schemeId);
				response.put("groupCode", groupCode);
				response.put("regNo", regNo);
				response.put("joinDate", joinDate);
				response.put("openingDate", openingDate);
				response.put("upDateTime", upDateTime2 != null ? upDateTime2 : upDateTime1);
				response.put("userId", userId2);
				response.put("totalIns", totalIns);
				response.put("amount", amount);
				response.put("modePay", modePay);
				response.put("accCode", accCode);
				response.put("sno", sno);

// Convert to JSON if your framework requires it (e.g. Jackson)
// return new ObjectMapper().writeValueAsString(response);
				return response.toString();

			}
			catch (Exception e) {
				firstJdbcTemplate.update("DELETE FROM SCHEMEMAST WHERE GROUPCODE= ? AND REGNO= ? ",groupCode,regNo);
				try{
					firstJdbcTemplate.update("DELETE FROM PERSONALINFO WHERE PERSONALID= ? ",PersonalId);
				}
				catch (Exception e1){}
				//firstJdbcTemplate.update("UPDATE SOFTCONTROL SET CTLTEXT = ? WHERE CTLID = 'MASTERCODE' AND CTLTEXT = ? ", Integer.parseInt(PersonalId) , PersonalId);
				//firstJdbcTemplate.update("UPDATE INSAMOUNT SET CURRENTREGNO = ? WHERE SCHEMEID = ? and ACTIVE ='Y' and CURRENTREGNO = ? and GROUPCODE = ?  ", Integer.parseInt(regNo), schemeId, regNo, groupCode);
				return "Error - No Record inserted due to Duplicate record";
			}
		}



}
