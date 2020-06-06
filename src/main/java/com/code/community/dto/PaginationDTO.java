package com.code.community.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PaginationDTO <T> {
    private List<T> data;
    private boolean showPrevious;
    private boolean showFirstPage;
    private boolean showNext;
    private boolean showEndPage;
    private Integer page;
    private List<Integer> pages = new ArrayList<>();
    private Integer totalPage;


    public void setPagination(Integer totalPage, Integer page) {
        this.page = page;
        this.totalPage = totalPage;
        if(this.page < 1){//小于第一页改成第一页
            this.page = 1;
        }
        if(this.page > totalPage){//大于最后一页改成最后一页
            this.page = totalPage;
        }
        if(!this.page.equals(0)){
            pages.add(this.page);
        }

        for(int i = 1; i <= 3; i++){
            if(this.page - i > 0 && !this.page.equals(0)){
                pages.add(0, this.page - i);
            }
            if(this.page + i <= totalPage && !this.page.equals(0)){
                pages.add(this.page + i);
            }
        }

        if(this.page.equals(1) ){//是否展示跳转到第一页
            this.showPrevious = false;
        }else{
            this.showPrevious = true;
        }

        if(this.page.equals(totalPage)) {//是否展示跳转到最后一页
            this.showNext = false;
        }else{
            this.showNext = true;
        }

        if(pages.contains(1)){//是否显示跳转到第一页
            this.showFirstPage = false;
        }else{
            this.showFirstPage = true;
        }

        if(pages.contains(totalPage)){//是否显示跳转到最后一页
            this.showEndPage = false;
        }else{
            this.showEndPage = true;
        }
        if(this.page.equals(0)){
            this.showPrevious = false;
            this.showNext = false;
            this.showFirstPage = false;
            this.showEndPage = false;
        }
    }
}
