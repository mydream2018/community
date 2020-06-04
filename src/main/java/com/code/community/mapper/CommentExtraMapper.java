package com.code.community.mapper;

import com.code.community.model.Comment;

public interface CommentExtraMapper {
    int incCommentCount(Comment comment);
}