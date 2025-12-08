package com.itheima.service;
import com.itheima.domain.Book;
import com.itheima.domain.User;
import entity.PageResult;
//图书接口

public interface BookService {
//查询最新上架的图书
PageResult selectNewBooks(Integer pageNum, Integer pageSize);
//根据id查询图书信息
Book findById(String id);
//借阅图书
Integer borrowBook(Book book);
//分页查询图书
PageResult search(Book book, Integer pageNum, Integer pageSize);
//新增
Integer addBook(Book book);
//编辑
Integer editBook(Book book);
//查询当前借阅
PageResult searchBorrowed(Book book, User user, Integer pageNum, Integer pageSize);
//归还
boolean returnBook(String  id,User user);
//归还确认
Integer returnConfirm(String id);

}
