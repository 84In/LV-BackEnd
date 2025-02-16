package com.luanvan.authenticationservice.controller;

import com.luanvan.authenticationservice.dto.LoginRequest;
import com.luanvan.authenticationservice.service.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class authenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping
    public Map<String,Object> login(@RequestBody LoginRequest loginRequest, HttpServletResponse httpServletResponse){
        Map<String, Object> result =  authenticationService.login(loginRequest,httpServletResponse);
        Map<String,Object> map = new HashMap<>();
        map.put("code",0);
        map.put("data",result);
        return map;
    }
}
