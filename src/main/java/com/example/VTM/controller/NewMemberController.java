package com.example.VTM.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.VTM.model.NewMember.NMData;
import com.example.VTM.service.NewMemberService;

@RestController
@RequestMapping("/v1/api/member")
public class NewMemberController {
	
	@Autowired
	public NewMemberService newMemberService;
	
	@GetMapping("/scheme")
	public List<Map<String,Object>> getScheme(){
			return newMemberService.getScheme();
	}
	
	@GetMapping("/schemeid")
	public List<Map<String,Object>> getSchemeAmount(@RequestParam("schemeId")  String schemeId){	
		return newMemberService.getSchemeAmount(schemeId);
	}

	@PostMapping("/create")
	public String createNewMember(@RequestBody NMData nmData) {
		return newMemberService.createNewMember(nmData,true);
	}

}
