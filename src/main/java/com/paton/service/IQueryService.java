package com.paton.service;

import com.paton.domain.Book;
import com.paton.domain.User;
import com.paton.domain.Vo.BorrowingBooksVo;
import com.paton.utils.page.Page;

public interface IQueryService {

    /**
     * 查询借阅记录
     * @param userId 用户ID（为null时查询所有记录）
     * @param role 角色（admin/user）
     * @param pageNum 页码
     * @return 分页结果
     */
    Page<BorrowingBooksVo> queryBorrowingRecords(Integer userId, String role, int pageNum);

    /**
     * 查询书籍
     * @param bookName 书名（可选）
     * @param categoryId 类别ID（可选）
     * @param role 角色（admin/user）
     * @param pageNum 页码
     * @return 分页结果
     */
    Page<Book> queryBooks(String bookName, Integer categoryId, String role, int pageNum);

    /**
     * 查询用户信息
     * @param userName 用户名（可选）
     * @param role 角色（admin/user）
     * @param pageNum 页码
     * @return 分页结果
     */
    Page<User> queryUsers(String userName, String role, int pageNum);
}