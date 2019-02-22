package com.blueskykong.demo.rest;

import com.blueskykong.demo.annotation.PreAuth;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by keets on 2017/12/6.
 */
@RestController
public class DemoController {

    @RequestMapping(value = "/test", method = RequestMethod.GET)
//    @PreAuthorize(value = "hasAuthority('CREATE_COMPANY')") hasRole('Admin')
    @PreAuth("hasAuthority('CREATE_COMPANY')")
    public String test() {
        return "ok";
    }


    @GetMapping(value = "/hello")
    public String hello() {
        return "hello, this is web demo!";
    }
}
