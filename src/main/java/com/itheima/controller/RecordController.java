package com.itheima.controller;

import com.itheima.domain.Record;
import com.itheima.domain.User;
import com.itheima.service.RecordService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/record")
public class RecordController {

    @Autowired
    private RecordService recordService;

    /*
     * 根据条件查找借阅日志
     * recordParam: 检索条件
     * requestParam: HTTP请求
     * currentPage: 当前页码
     * sizePerPage: 每页条数
     */
    @RequestMapping("/searchRecords")
    public ModelAndView executeSearch(Record recordParam,
                                      HttpServletRequest requestParam,
                                      Integer currentPage,
                                      Integer sizePerPage) {

        // 设置页码默认值
        if (currentPage == null) {
            currentPage = 1;
        }
        // 设置每页数量默认值
        if (sizePerPage == null) {
            sizePerPage = 10;
        }

        // 从会话中提取当前登录用户
        User currentUser = (User) requestParam.getSession().getAttribute("USER_SESSION");

        // 调用服务层进行查询
        PageResult resultPage = recordService.searchRecords(recordParam, currentUser, currentPage, sizePerPage);

        // 创建视图对象
        ModelAndView mv = new ModelAndView();
        mv.setViewName("record");

        // 添加结果数据
        mv.addObject("pageResult", resultPage);

        // 回显查询条件
        mv.addObject("search", recordParam);

        // 传递当前页码
        mv.addObject("pageNum", currentPage);

        // 传递当前请求路径，用于分页链接
        mv.addObject("gourl", requestParam.getRequestURI());

        return mv;
    }
}

