package com.ssafy.Split.bowling.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("bowling")
public class BowlingController {

    @GetMapping
    public String test() {
        return "Bowling test";
    }
}
