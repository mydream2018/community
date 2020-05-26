package com.code.community.service;

import com.code.community.dto.PaginationDTO;
import com.code.community.dto.QuestionDTO;
import com.code.community.exception.CustomizeErrorCode;
import com.code.community.exception.CustomizeException;
import com.code.community.mapper.QuestionMapper;
import com.code.community.mapper.UserMapper;
import com.code.community.model.Question;
import com.code.community.model.QuestionExample;
import com.code.community.model.User;
import org.apache.ibatis.session.RowBounds;
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

//        Integer totalCount = questionMapper.count();
//        pagination.setPagination(totalCount, page, size);
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        PaginationDTO paginationDTO = new PaginationDTO();
        //获取论坛用户发布问题的总数量
        Integer totalCount = (int)questionMapper.countByExample(new QuestionExample());

        Integer totalPage;
        if(totalCount % size == 0){
            totalPage = totalCount / size;
        }else{
            totalPage = (totalCount / size) + 1;
        }
        paginationDTO.setPagination(totalPage, page);

        Integer offset = (paginationDTO.getPage() - 1) * size;
//        List<Question> questionList1 = questionMapper.list(offset, size);
        List<Question> questionList = questionMapper.selectByExampleWithRowbounds(
                new QuestionExample(), new RowBounds(offset, size));


        for(Question question : questionList){
            User user = userMapper.selectByPrimaryKey(question.getCreator());
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
        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria().andCreatorEqualTo(userId);
        Integer totalCount = (int)questionMapper.countByExample(questionExample);
        Integer totalPage;
        if(totalCount % size == 0){
            totalPage = totalCount / size;
        }else{
            totalPage = (totalCount / size) + 1;
        }
        paginationDTO.setPagination(totalPage, page);

        Integer offset = (paginationDTO.getPage() - 1) * size;

        QuestionExample questionExampleWithId = new QuestionExample();
        questionExampleWithId.createCriteria().andCreatorEqualTo(userId);
        List<Question> questionList = questionMapper.selectByExampleWithRowbounds(
                questionExampleWithId, new RowBounds(offset, size));

//        List<Question> questionList = questionMapper.listByUserId(userId,offset, size);
        for(Question question : questionList){
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }

        paginationDTO.setQuestions(questionDTOList);

        return paginationDTO;

    }

    public QuestionDTO findById(Integer id) {

        Question question = questionMapper.selectByPrimaryKey(id);
        if(question == null){
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question, questionDTO);
        User user = userMapper.selectByPrimaryKey(question.getCreator());
        questionDTO.setUser(user);
        return questionDTO;
    }

    public void createOrUpdate(Question question) {
        if(question.getId() == null){//该问题第一发布，需要插入
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            questionMapper.insert(question);
        }else{//该问题经过提问者修改，需要重新更新一下
            Question questionModified = new Question();
            questionModified.setGmtModified(System.currentTimeMillis());
            questionModified.setTitle(question.getTitle());
            questionModified.setDescription(question.getDescription());
            questionModified.setTag(question.getTag());
            QuestionExample questionExample = new QuestionExample();
            questionExample.createCriteria().andIdEqualTo(question.getId());
            int count = questionMapper.updateByExampleSelective(questionModified, questionExample);
            if(count != 1){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }

        }
    }
}
