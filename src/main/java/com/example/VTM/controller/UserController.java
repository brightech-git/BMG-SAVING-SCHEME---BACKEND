package com.example.VTM.controller;


import com.example.VTM.model.RequestResponse.RequestResponse;
import com.example.VTM.model.User;
import com.example.VTM.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/api")
public class UserController {

    @Autowired
    public UserService userService;

    @GetMapping("/user")
    public List<User> getUser(){
        return userService.getUser();
    }

    @GetMapping("/serverstatus")
    public RequestResponse checkHealthy(){
        return userService.checkHealthy();
    }

    @GetMapping("/company")
    public RequestResponse getCompanyName(){
        return userService.getCompanyName();
    }


    @GetMapping("/getAmountWeight")
    public List<Map<String, String>> getAmountWeight(@RequestParam Integer REGNO, @RequestParam String GROUPCODE) {
        System.out.println(REGNO);
        System.out.println(GROUPCODE);
        try {
            return userService.getAmountWeight(REGNO, GROUPCODE);
        } catch (Exception e) {
            throw new RuntimeException(e);

        }

    }




}
