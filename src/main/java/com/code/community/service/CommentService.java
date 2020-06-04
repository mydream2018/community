package com.code.community.service;

import com.code.community.dto.CommentDTO;
import com.code.community.enums.CommentTypeEnum;
import com.code.community.exception.CustomizeErrorCode;
import com.code.community.exception.CustomizeException;
import com.code.community.mapper.CommentMapper;
import com.code.community.mapper.QuestionExtraMapper;
import com.code.community.mapper.QuestionMapper;
import com.code.community.mapper.UserMapper;
import com.code.community.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionExtraMapper questionExtraMapper;

    @Autowired
    private UserMapper userMapper;

    @Transactional
    public void insert(Comment comment) {
        //评论的parentId不存在
        if(comment.getParentId() == null || comment.getParentId() == 0){
            throw new CustomizeException(CustomizeErrorCode.TARGET_NOT_FOUND);
        }
        //评论的类型错误
        if(comment.getType() == null || !CommentTypeEnum.isExist(comment.getType())){
            System.out.println(comment.getType()== null);
            System.out.println(CommentTypeEnum.isExist(comment.getType()));
            throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);
        }

        if(comment.getType() == CommentTypeEnum.COMMENT.getType()){//回复评论
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if(dbComment == null){
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }
            commentMapper.insert(comment);
        }else {//回复问题
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if(question == null){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            commentMapper.insert(comment);
            question.setCommentCount(1);
            questionExtraMapper.incCommentCount(question);
        }
    }

    public List<CommentDTO> listByTargetId(Long id, CommentTypeEnum type) {
        CommentExample commentExample = new CommentExample();
        commentExample.createCriteria()
                .andTypeEqualTo(type.getType())
                .andParentIdEqualTo(id);
        commentExample.setOrderByClause("gmt_create desc");
        List<Comment> comments = commentMapper.selectByExample(commentExample);
        if(comments.size() == 0){
            return new ArrayList<>();
        }
        //获取去重的评论人的id
        Set<Long> commentators = comments.stream().map(comment->comment.getCommentator()).collect(Collectors.toSet());
        List<Long> userIds = new ArrayList<>();
        userIds.addAll(commentators);

        //获取去重的评论人的信息并转换为map
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andIdIn(userIds);
//        System.out.println(userIds);
        List<User> users = userMapper.selectByExample(userExample);
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));

        //转换comment为commentDTO
        List<CommentDTO> commentDTOS = comments.stream().map(comment->{

            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment, commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentator()));
            return commentDTO;
        }).collect(Collectors.toList());
        return commentDTOS;
    }
}
