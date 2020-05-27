package com.code.community.advice;

import com.alibaba.fastjson.JSON;
import com.code.community.dto.ResultDTO;
import com.code.community.exception.CustomizeErrorCode;
import com.code.community.exception.CustomizeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@ControllerAdvice
public class CustomizeExceptionHandler {
    @ExceptionHandler(Exception.class)
    Object handle(HttpServletRequest request, Throwable ex, Model model, HttpServletResponse response) {
        String contentType = request.getContentType();
        ResultDTO resultDTO;
        if("application/json".equals(contentType)){
            //返回json
            if(ex instanceof CustomizeException){
                ex.printStackTrace();
                resultDTO = ResultDTO.errorOf((CustomizeException)ex);
            }else {
                ex.printStackTrace();
                resultDTO = ResultDTO.errorOf(CustomizeErrorCode.SYSTEM_ERROR);
            }
            try {
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json");
                response.setStatus(200);
                PrintWriter writer = response.getWriter();
                writer.write(JSON.toJSONString(resultDTO));
                writer.close();
            } catch (IOException ioe) {
//                ioe.printStackTrace();
            }
            return null;
        }else{
            //做错误页面跳转
            if(ex instanceof CustomizeException){
                ex.printStackTrace();
                model.addAttribute("messages", ex.getMessage());
            }else {
                ex.printStackTrace();
                model.addAttribute("messages", ResultDTO.errorOf(CustomizeErrorCode.SYSTEM_ERROR));
            }
            return new ModelAndView("error");
        }


    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statusCode);
    }
}
