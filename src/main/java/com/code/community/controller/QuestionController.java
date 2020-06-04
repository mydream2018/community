package com.code.community.controller;

import com.code.community.dto.CommentDTO;
import com.code.community.dto.QuestionDTO;
import com.code.community.enums.CommentTypeEnum;
import com.code.community.service.CommentService;
import com.code.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class QuestionController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private QuestionService questionService;

    @GetMapping("/question/{id}")
    public String question(@PathVariable(name="id") Long id, Model model){
        QuestionDTO questionDTO = questionService.findById(id);
        List<CommentDTO> commentDTOList = commentService.listByTargetId(id, CommentTypeEnum.QUESTION);
        //累加阅读数
        questionService.inView(id);
        model.addAttribute("question",questionDTO);
        model.addAttribute("comments", commentDTOList);
        return "question";
    }
}
