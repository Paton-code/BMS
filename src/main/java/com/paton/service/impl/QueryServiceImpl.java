package com.paton.service.impl;

import com.paton.domain.Book;
import com.paton.domain.BorrowingBooks;
import com.paton.domain.User;
import com.paton.domain.Vo.BorrowingBooksVo;
import com.paton.mapper.BookMapper;
import com.paton.mapper.BorrowingBooksMapper;
import com.paton.mapper.UserMapper;
import com.paton.service.IQueryService;
import com.paton.utils.page.Page;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class QueryServiceImpl implements IQueryService {

    @Resource
    private BorrowingBooksMapper borrowingBooksMapper;

    @Resource
    private BookMapper bookMapper;

    @Resource
    private UserMapper userMapper;

    @Override
    public Page<BorrowingBooksVo> queryBorrowingRecords(Integer userId, String role, int pageNum) {
        if ("admin".equals(role)) {
            // 管理员查询所有借阅记录
            List<BorrowingBooks> borrowingBooksList = borrowingBooksMapper.selectAllByPage((pageNum - 1) * 10, 10);
            return convertToBorrowingBooksVoPage(borrowingBooksList, pageNum, true);
        } else {
            // 用户查询个人借阅记录
            if (userId == null) {
                throw new IllegalArgumentException("用户ID不能为空");
            }
            List<BorrowingBooks> borrowingBooksList = borrowingBooksMapper.selectByPageNumAndPageSize(userId, (pageNum - 1) * 10, 10);
            return convertToBorrowingBooksVoPage(borrowingBooksList, pageNum, false);
        }
    }

    @Override
    public Page<Book> queryBooks(String bookName, Integer categoryId, String role, int pageNum) {
        // 实现书籍查询逻辑
        List<Book> books = new ArrayList<>();
        Page<Book> page = new Page<>();
        page.setList(books);
        page.setPageNum(pageNum);
        page.setPageSize(10);
        page.setPageCount(1);
        return page;
    }

    @Override
    public Page<User> queryUsers(String userName, String role, int pageNum) {
        // 实现用户查询逻辑
        List<User> users = new ArrayList<>();
        Page<User> page = new Page<>();
        page.setList(users);
        page.setPageNum(pageNum);
        page.setPageSize(10);
        page.setPageCount(1);
        return page;
    }

    /**
     * 将BorrowingBooks列表转换为BorrowingBooksVo分页对象
     */
    private Page<BorrowingBooksVo> convertToBorrowingBooksVoPage(List<BorrowingBooks> borrowingBooksList, int pageNum, boolean includeUserInfo) {
        Page<BorrowingBooksVo> page = new Page<>();

        if (borrowingBooksList == null || borrowingBooksList.isEmpty()) {
            page.setList(new ArrayList<>());
            page.setPageNum(pageNum);
            page.setPageCount(0);
            return page;
        }

        List<BorrowingBooksVo> borrowingBooksVos = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (BorrowingBooks borrowingBook : borrowingBooksList) {
            BorrowingBooksVo vo = new BorrowingBooksVo();

            // 设置书籍信息
            Book book = bookMapper.selectByPrimaryKey(borrowingBook.getBookId());
            vo.setBook(book);

            // 如果是管理员查询，设置用户信息
            if (includeUserInfo) {
                User user = userMapper.selectByPrimaryKey(borrowingBook.getUserId());
                vo.setUser(user);
            }

            // 设置日期信息
            if (borrowingBook.getDate() != null) {
                vo.setDateOfBorrowing(sdf.format(borrowingBook.getDate()));
                // 计算还书日期（借书日期+2个月）
                java.util.Calendar calendar = java.util.Calendar.getInstance();
                calendar.setTime(borrowingBook.getDate());
                calendar.add(java.util.Calendar.MONTH, 2);
                vo.setDateOfReturn(sdf.format(calendar.getTime()));
            }

            borrowingBooksVos.add(vo);
        }

        page.setList(borrowingBooksVos);
        page.setPageNum(pageNum);
        page.setPageSize(10);

        // 计算总页数
        int totalCount;
        if (includeUserInfo) {
            totalCount = borrowingBooksMapper.selectAll();
        } else {
            // 修复错误：使用borrowingBook的userId，而不是不存在的user变量
            BorrowingBooks borrowingBook = borrowingBooksList.get(0);
            totalCount = borrowingBooksMapper.selectAllRecordCount(borrowingBook.getUserId());
        }

        int pageCount = (totalCount + 10 - 1) / 10;
        page.setPageCount(pageCount);

        return page;
    }
}