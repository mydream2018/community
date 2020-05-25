package com.code.community.service;

import com.code.community.dto.PaginationDTO;
import com.code.community.dto.QuestionDTO;
import com.code.community.mapper.QuestionMapper;
import com.code.community.mapper.UserMapper;
import com.code.community.model.Question;
import com.code.community.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    public PaginationDTO list(Integer page, Integer size){
        //(page - 1) * 5


//        Integer totalCount = questionMapper.count();
//        pagination.setPagination(totalCount, page, size);
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        PaginationDTO paginationDTO = new PaginationDTO();
        //获取论坛用户发布问题的总数量
        Integer totalCount = questionMapper.count();

        Integer totalPage;
        if(totalCount % size == 0){
            totalPage = totalCount / size;
        }else{
            totalPage = (totalCount / size) + 1;
        }
        paginationDTO.setPagination(totalPage, page);

        Integer offset = (paginationDTO.getPage() - 1) * size;
//        System.out.println(offset+"------------------------------");
        List<Question> questionList = questionMapper.list(offset, size);
        for(Question question : questionList){
            User user = userMapper.findById(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }

        paginationDTO.setQuestions(questionDTOList);

        return paginationDTO;
    }


    public PaginationDTO list(Integer userId, Integer page, Integer size) {
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        PaginationDTO paginationDTO = new PaginationDTO();
        //获取个人发布问题的总数量
        Integer totalCount = questionMapper.countByUserId(userId);
        Integer totalPage;
        if(totalCount % size == 0){
            totalPage = totalCount / size;
        }else{
            totalPage = (totalCount / size) + 1;
        }
        paginationDTO.setPagination(totalPage, page);

        Integer offset = (paginationDTO.getPage() - 1) * size;
//        System.out.println(offset+"------------------------------");
        List<Question> questionList = questionMapper.listByUserId(userId,offset, size);
        for(Question question : questionList){
            User user = userMapper.findById(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }

        paginationDTO.setQuestions(questionDTOList);

        return paginationDTO;

    }
}
