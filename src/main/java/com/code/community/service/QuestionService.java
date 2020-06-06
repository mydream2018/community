package com.code.community.service;

import com.code.community.dto.PaginationDTO;
import com.code.community.dto.QuestionDTO;
import com.code.community.exception.CustomizeErrorCode;
import com.code.community.exception.CustomizeException;
import com.code.community.mapper.QuestionExtraMapper;
import com.code.community.mapper.QuestionMapper;
import com.code.community.mapper.UserMapper;
import com.code.community.model.Question;
import com.code.community.model.QuestionExample;
import com.code.community.model.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionExtraMapper questionExtraMapper;

    public PaginationDTO list(Integer page, Integer size){

        List<QuestionDTO> questionDTOList = new ArrayList<>();
        PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO<>();
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
//        List<Question> questionList = questionExtraMapper.list(offset, size);
        QuestionExample questionExample = new QuestionExample();
        questionExample.setOrderByClause("gmt_create desc");
        List<Question> questionList = questionMapper.selectByExampleWithRowbounds(questionExample,new RowBounds(offset, size));

//        Collections.sort(questionList, (o1, o2) -> {
//            if(o1.getGmtCreate() > o2.getGmtCreate()){
//                return -1;
//            }else if(o1.getGmtCreate() == o2.getGmtCreate()) {
//                return 0;
//            }else{
//                return 1;
//            }
//        });

        for(Question question : questionList){
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }

        paginationDTO.setData(questionDTOList);

        return paginationDTO;
    }


    public PaginationDTO list(Long userId, Integer page, Integer size) {
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO<>();
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

        List<Question> questionList = questionExtraMapper.listByUserId(userId,offset, size);
        for(Question question : questionList){
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }

        paginationDTO.setData(questionDTOList);

        return paginationDTO;

    }

    public QuestionDTO findById(Long id) {

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
            question.setLikeCount(0);
            question.setViewCount(0);
            question.setCommentCount(0);
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

    public void inView(Long id) {
        Question updateQuestion = new Question();
        updateQuestion.setViewCount(1);
        updateQuestion.setId(id);
        questionExtraMapper.incView(updateQuestion);
    }

    public List<QuestionDTO> selectRelated(QuestionDTO queryDTO) {
        if(StringUtils.isBlank(queryDTO.getTag())){
            return new ArrayList<>();
        }
        String[] tags = StringUtils.split(queryDTO.getTag(),",");
        String regexpTag = Arrays.stream(tags).collect(Collectors.joining("|"));
        Question question = new Question();
        question.setId(queryDTO.getId());
        question.setTag(regexpTag);
        List<Question> questionList = questionExtraMapper.selectRelated(question);
        List<QuestionDTO> questionDTOs = questionList.stream().map(q -> {
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(q, questionDTO);
            return questionDTO;
        }).collect(Collectors.toList());
        return questionDTOs;
    }
}
