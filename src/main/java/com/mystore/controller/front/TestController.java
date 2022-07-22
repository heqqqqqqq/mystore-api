package com.mystore.controller.front;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController         //要么用RestController，要么用@Controller，下面的方法加@ResponseBody
@RequestMapping("/test/")
public class TestController {

    @GetMapping("aaa")
    public String fun(){
        return "abcde";
    }

}
