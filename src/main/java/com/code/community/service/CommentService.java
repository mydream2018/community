package com.code.community.service;

import com.code.community.enums.CommentTypeEnum;
import com.code.community.exception.CustomizeErrorCode;
import com.code.community.exception.CustomizeException;
import com.code.community.mapper.CommentMapper;
import com.code.community.mapper.QuestionExtraMapper;
import com.code.community.mapper.QuestionMapper;
import com.code.community.model.Comment;
import com.code.community.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionExtraMapper questionExtraMapper;

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
}
