package com.code.community.controller;

import com.code.community.dto.PaginationDTO;
import com.code.community.mapper.QuestionMapper;
import com.code.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndexController {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionService questionService;

    @GetMapping("/")
    public String hello(Model model,
                        @RequestParam(value = "page", defaultValue = "1") Integer page,
                        @RequestParam(value = "size", defaultValue = "5") Integer size
                        ){

        PaginationDTO pagination = questionService.list(page, size);
        model.addAttribute("pagination", pagination);

        return "index";
    }
}
