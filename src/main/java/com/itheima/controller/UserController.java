package com.itheima.controller;

import com.itheima.domain.User;
import com.itheima.service.UserService;
import entity.PageResult;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    // 退出登录
    @RequestMapping("/logout")
    public String exit(HttpServletRequest req) {
        try {
            HttpSession sess = req.getSession();
            sess.invalidate();
            return "forward:/admin/login.jsp";
        } catch (Exception ex) {
            ex.printStackTrace();
            req.setAttribute("msg", "系统异常");
            return "forward:/admin/login.jsp";
        }
    }

    // 登录
    @RequestMapping("/login")
    public String signIn(User u, HttpServletRequest req) {
        try {
            User usr = userService.login(u);
            if (usr == null) {
                req.setAttribute("msg", "用户名或密码错误");
                return "forward:/admin/login.jsp";
            }
            req.getSession().setAttribute("USER_SESSION", usr);
            String roleType = usr.getRole();
            if (roleType == null) return "redirect:/admin/index.jsp";
            if (roleType.equals("ADMIN")) {
                return "redirect:/admin/main.jsp";
            }
            return "redirect:/admin/index.jsp";
        } catch (Exception ex) {
            ex.printStackTrace();
            req.setAttribute("msg", "系统异常");
            return "forward:/admin/login.jsp";
        }
    }

    // 添加用户
    @ResponseBody
    @RequestMapping("/addUser")
    public Result insert(User u) {
        try {
            userService.addUser(u);
            return new Result(true, "添加成功");
        } catch (Exception ex) {
            ex.printStackTrace();
            return new Result(false, "添加失败");
        }
    }

    // 删除用户（离职）
    @ResponseBody
    @RequestMapping("/delUser")
    public Result remove(Integer id) {
        try {
            userService.delUser(id);
            return new Result(true, "删除成功");
        } catch (Exception ex) {
            ex.printStackTrace();
            return new Result(false, "删除失败");
        }
    }

    // 查询单个用户
    @ResponseBody
    @RequestMapping("/findById")
    public User querySingle(Integer id) {
        return userService.findById(id);
    }

    // 更新用户
    @ResponseBody
    @RequestMapping("/editUser")
    public Result update(User u) {
        try {
            userService.editUser(u);
            return new Result(true, "更新成功");
        } catch (Exception ex) {
            ex.printStackTrace();
            return new Result(false, "更新失败");
        }
    }

    // 检查名称重复
    @ResponseBody
    @RequestMapping("/checkName")
    public Result validateName(String name) {
        Integer cnt = userService.checkName(name);
        if (cnt == null) return new Result(false, "检查异常");
        if (cnt > 0) {
            return new Result(false, "名称重复");
        } else {
            return new Result(true, "名称可用");
        }
    }

    // 检查邮箱重复
    @ResponseBody
    @RequestMapping("/checkEmail")
    public Result validateEmail(String email) {
        Integer cnt = userService.checkEmail(email);
        if (cnt == null) return new Result(false, "检查异常");
        if (cnt > 0) {
            return new Result(false, "邮箱重复");
        } else {
            return new Result(true, "邮箱可用");
        }
    }

    // 搜索用户（分页）
    @RequestMapping("/search")
    public ModelAndView find(User condition, Integer page, Integer size) {
        if (page == null) page = 1;
        if (size == null) size = 10;
        PageResult pr = userService.searchUsers(condition, page, size);
        ModelAndView mv = new ModelAndView();
        mv.setViewName("user");
        mv.addObject("pageResult", pr);
        mv.addObject("search", condition);
        mv.addObject("pageNum", page);
        mv.addObject("gourl", "/user/search");
        return mv;
    }
}