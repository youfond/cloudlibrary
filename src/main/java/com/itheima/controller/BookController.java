package com.itheima.controller;

import com.itheima.domain.Book;
import com.itheima.domain.User;
import com.itheima.service.BookService;
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
@RequestMapping("/book")
public class BookController {

    @Autowired
    private BookService bookService;

    // 查书详情
    @ResponseBody
    @RequestMapping("/findById")
    public Result<Book> getBookDetails(String bookId) {
        try {
            Book targetBook = bookService.findById(bookId);
            if (targetBook == null) {
                return new Result(false, "没找到书");
            }
            return new Result(true, "ok", targetBook);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "查询出错");
        }
    }

    // 分页查书
    @RequestMapping("/search")
    public ModelAndView queryBooks(Book queryCondition, Integer currentPage,
                                   Integer pageLimit, HttpServletRequest req) {
        if (currentPage == null) {
            currentPage = 1;
        }
        if (pageLimit == null) {
            pageLimit = 10;
        }

        PageResult pagedData = bookService.search(queryCondition, currentPage, pageLimit);
        ModelAndView viewContainer = new ModelAndView();
        viewContainer.setViewName("books");
        viewContainer.addObject("pageResult", pagedData);
        viewContainer.addObject("search", queryCondition);
        viewContainer.addObject("pageNum", currentPage);
        viewContainer.addObject("gourl", req.getRequestURI());
        return viewContainer;
    }

    // 新书列表
    @RequestMapping("/selectNewbooks")
    public ModelAndView fetchNewBooks() {
        int startPage = 1;
        int itemsPerPage = 5;
        PageResult resultPage = bookService.selectNewBooks(startPage, itemsPerPage);
        ModelAndView mv = new ModelAndView();
        mv.setViewName("books_new");
        mv.addObject("pageResult", resultPage);
        return mv;
    }

    // 借书
    @ResponseBody
    @RequestMapping("/borrowBook")
    public Result executeBorrow(Book borrowItem, HttpSession currentSession) {
        String currentUserName = ((User) currentSession.getAttribute("USER_SESSION")).getName();
        borrowItem.setBorrower(currentUserName);
        try {
            Integer operationResult = bookService.borrowBook(borrowItem);
            if (operationResult == 0) {
                return new Result(false, "借阅失败");
            }
            return new Result(true, "借阅成功，去拿书");
        } catch (Exception ex) {
            ex.printStackTrace();
            return new Result(false, "借书异常");
        }
    }

    // 还书
    @ResponseBody
    @RequestMapping("/returnBook")
    public Result requestReturn(String bookIdentifier, HttpSession session) {
        User currentUser = (User) session.getAttribute("USER_SESSION");
        try {
            boolean returnStatus = bookService.returnBook(bookIdentifier, currentUser);
            if (returnStatus == false) {
                return new Result(false, "还书失败");
            }
            return new Result(true, "还书流程已启动");
        } catch (Exception ex) {
            ex.printStackTrace();
            return new Result(false, "还书异常");
        }
    }

    // 确认还书完成
    @ResponseBody
    @RequestMapping("/returnConfirm")
    public Result confirmReturnCompletion(String bookId) {
        try {
            Integer confirmResult = bookService.returnConfirm(bookId);
            if (confirmResult == 0) {
                return new Result(false, "确认失败");
            }
            return new Result(true, "已确认还书");
        } catch (Exception ex) {
            ex.printStackTrace();
            return new Result(false, "确认异常");
        }
    }

    // 改书信息
    @ResponseBody
    @RequestMapping("/editBook")
    public Result updateBookInfo(Book modifiedBook) {
        try {
            Integer updateResult = bookService.editBook(modifiedBook);
            if (updateResult == 0) {
                return new Result(false, "更新失败");
            }
            return new Result(true, "更新成功");
        } catch (Exception ex) {
            ex.printStackTrace();
            return new Result(false, "更新异常");
        }
    }

    // 加书
    @ResponseBody
    @RequestMapping("/addBook")
    public Result addBook(Book book) {
        try {
            int insertCount = bookService.addBook(book);
            if (insertCount == 0) {
                return new Result(false, "新增失败");
            }
            return new Result(true, "新增完成");
        } catch (Exception ex) {
            ex.printStackTrace();
            return new Result(false, "新增异常");
        }
    }

    // 查已借未还
    @RequestMapping("/searchBorrowed")
    public ModelAndView queryBorrowedRecords(Book condition, Integer currentPage,
                                             Integer pageLimit, HttpServletRequest req) {
        if (currentPage == null) {
            currentPage = 1;
        }
        if (pageLimit == null) {
            pageLimit = 10;
        }

        User currentUser = (User) req.getSession().getAttribute("USER_SESSION");
        PageResult resultSet = bookService.searchBorrowed(condition, currentUser,
                currentPage, pageLimit);
        ModelAndView mvObj = new ModelAndView();
        mvObj.setViewName("book_borrowed");
        mvObj.addObject("pageResult", resultSet);
        mvObj.addObject("search", condition);
        mvObj.addObject("pageNum", currentPage);
        mvObj.addObject("gourl", req.getRequestURI());
        return mvObj;
    }
}
