package com.example.VTM.controller;

import com.example.VTM.model.PPData;
import com.example.VTM.model.PhoneSearch.PhoneSearchRegNoGroupCode;
import com.example.VTM.model.SchemeCollectInsert;
import com.example.VTM.service.AccountService;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/account")
public class AccountController {

    @Autowired
    public AccountService accountService;

    @GetMapping
    public PPData getppData(@RequestParam("regno") int regno, @RequestParam("groupcode") String groupcode) {  
      return accountService.ppData(regno,groupcode);
    }

//    @GetMapping("/todayrate")
//    public Map<String,Object> getRateOFGoldAndSliver(){
//    	return accountService.getRateOFGoldAndSliver();
//    }
    
    @GetMapping("/phonesearch")
    public List<PhoneSearchRegNoGroupCode> getRegNoGroupCodeByPhoneNo(@RequestParam("phoneNo")  String phoneNo){
    	return accountService.getRegNoGroupCodeByPhoneNo(phoneNo);
    }

    @PostMapping("/insert")
    public String insertEntry(@RequestBody SchemeCollectInsert data ) {
    	return accountService.insertEntry(data.getGroupCode(), data.getRegNo(), data.getRDate(), data.getAmount(), 
    			data.getModePay(), data.getAccCode(), data.getUpdateTime(),data.getInstallment()
    			,data.getUserID(),true);
    }
    
    @GetMapping("/getTranType")
    public List<Map<String,Object>> getTranType(){
    	return accountService.getTranType();    	
    }

    @GetMapping("/todayrate")
    public Map<String,Object> getRateOfGoldAndSliver(){

        return accountService.getRateOfGoldAndSliver();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMember(@PathVariable("id") Long id) {
        boolean deleted = accountService.deleteMember(id);
        if (deleted) {
            return ResponseEntity.ok("Member deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Member not found or could not be deleted.");
        }
    }
    
}
