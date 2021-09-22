package com.imooc.ssoclient2.controller;

import com.imooc.ssoclient2.domain.Foo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ContentController {

    @GetMapping("/foos")
    public String getFoos(Model model) {
        model.addAttribute("foos", List.of(new Foo("Client-2", "This is second client")));
        return "content";
    }
}
