package com.itheima.interceptor;

import com.itheima.domain.User;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

public class ResourcesInterceptor extends HandlerInterceptorAdapter {
    // 不需要拦截的地址
    private List<String> excludedUrls;

    public ResourcesInterceptor(List<String> excludedUrls) {
        if (excludedUrls == null) {
            this.excludedUrls = new ArrayList<>();
        } else {
            this.excludedUrls = excludedUrls;
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object obj) throws Exception {
        // 当前路径
        String currentPath = req.getRequestURI();

        // 登录相关放行
        if (currentPath.contains("login")) {
            return true;
        }

        // 获取用户信息
        User usr = (User) req.getSession().getAttribute("USER_SESSION");

        // 没登录
        if (usr == null) {
            req.setAttribute("msg", "请先登录");
            req.getRequestDispatcher("/admin/login.jsp").forward(req, resp);
            return false;
        }

        String role = usr.getRole();

        // 管理员直接过
        if (role != null && role.equals("ADMIN")) {
            return true;
        }

        // 检查是否在排除列表
        for (String exUrl : excludedUrls) {
            if (currentPath.indexOf(exUrl) >= 0) {
                return true;
            }
        }

        // 没权限
        req.setAttribute("msg", "权限不足");
        req.getRequestDispatcher("/admin/login.jsp").forward(req, resp);
        return false;
    }

    // 检查路径是否在排除列表中
    private boolean isExcluded(String path) {
        if (path == null || path.length() == 0) return false;
        for (String ex : excludedUrls) {
            if (path.contains(ex)) {
                return true;
            }
        }
        return false;
    }

    // 获取排除列表（没用但留着）
    public List<String> getExcludedUrls() {
        return excludedUrls;
    }
}