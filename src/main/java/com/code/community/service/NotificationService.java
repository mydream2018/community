package com.code.community.service;

import com.code.community.dto.NotificationDTO;
import com.code.community.dto.PaginationDTO;
import com.code.community.enums.NotificationStatusEnum;
import com.code.community.enums.NotificationTypeEnum;
import com.code.community.exception.CustomizeErrorCode;
import com.code.community.exception.CustomizeException;
import com.code.community.mapper.NotificationMapper;
import com.code.community.mapper.UserMapper;
import com.code.community.model.Notification;
import com.code.community.model.NotificationExample;
import com.code.community.model.User;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private UserMapper userMapper;

    public PaginationDTO list(Long userId, Integer page, Integer size) {

        PaginationDTO<NotificationDTO> paginationDTO = new PaginationDTO<>();
        //获取个人发布问题的总数量
        NotificationExample questionExample = new NotificationExample();
        questionExample.createCriteria().andReceiverEqualTo(userId);
        Integer totalCount = (int)notificationMapper.countByExample(questionExample);
        Integer totalPage;
        if(totalCount % size == 0){
            totalPage = totalCount / size;
        }else{
            totalPage = (totalCount / size) + 1;
        }
        paginationDTO.setPagination(totalPage, page);

        Integer offset = (paginationDTO.getPage() - 1) * size;

        NotificationExample example = new NotificationExample();
        example.createCriteria().andReceiverEqualTo(userId);
        example.setOrderByClause("gmt_create desc");
        List<Notification> notificationList = notificationMapper.selectByExampleWithRowbounds(example ,new RowBounds(offset, size));

        if(notificationList.size() == 0){
            return paginationDTO;
        }

        List<NotificationDTO> notificationDTOS = new ArrayList<>();

        for (Notification notification : notificationList) {
            NotificationDTO notificationDTO = new NotificationDTO();
            BeanUtils.copyProperties(notification, notificationDTO);
            notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
            notificationDTOS.add(notificationDTO);
        }

        paginationDTO.setData(notificationDTOS);



        return paginationDTO;
    }

    public Long unreadCount(Long userId) {
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria()
                .andReceiverEqualTo(userId)
                .andStatusEqualTo(NotificationStatusEnum.UNREAD.getStatus());
        return  notificationMapper.countByExample(notificationExample);
    }

    public NotificationDTO read(Long id, User user) {

        Notification notification = notificationMapper.selectByPrimaryKey(id);
        if(notification == null){
            throw new CustomizeException(CustomizeErrorCode.NOTIFICATION_NOT_FOUND);
        }
        if(notification.getReceiver() != user.getId()){
            throw new CustomizeException(CustomizeErrorCode.READ_NOTIFICATION_FAIL);
        }

        notification.setStatus(NotificationStatusEnum.READ.getStatus());
        notificationMapper.updateByPrimaryKey(notification);

        NotificationDTO notificationDTO = new NotificationDTO();
        BeanUtils.copyProperties(notification, notificationDTO);
        notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
        return notificationDTO;
    }
}
