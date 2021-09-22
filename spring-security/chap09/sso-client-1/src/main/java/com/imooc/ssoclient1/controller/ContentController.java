package com.imooc.ssoclient1.controller;

import com.imooc.ssoclient1.domain.Foo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ContentController {

    @GetMapping("/content")
    public String getFoos(Model model) {
        model.addAttribute("foos", List.of(new Foo("Client-1", "This is first client")));
        return "content";
    }
}
