package com.madebawojo.nysc.ppa.clearance.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/")
    public String controller(){
        return "hello world";
    }
}
