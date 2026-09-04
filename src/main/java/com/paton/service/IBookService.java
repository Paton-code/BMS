package com.paton.service;

import com.paton.domain.Vo.BookVo;
import com.paton.utils.page.Page;

import java.util.List;
import java.util.Map;

public interface IBookService {

    /**
     * 按分类ID查询书籍
     * @param categoryId 分类ID
     * @return 书籍列表
     */
    List<BookVo> findBooksByCategoryId(Integer categoryId);

    /**
     * 按分类ID分页查询书籍
     * @param categoryId 分类ID
     * @param pageNum 页码
     * @return 分页结果
     */
    Page<BookVo> findBooksByCategoryIdWithPagination(int categoryId, int pageNum);

    /**
     * 按书名查询书籍
     * @param bookName 书名
     * @return 书籍列表
     */
    List<BookVo> findBooksByBookName(String bookName);

    /**
     * 查询所有书籍
     * @return 所有书籍列表
     */
    List<BookVo> findAllBooks();

    /**
     * 根据图书ID获取图书详情（包含封面和描述）
     * @param bookId 图书ID
     * @return 图书详情
     */
    BookVo getBookDetailById(Integer bookId);

    /**
     * 检查图书是否可借
     * @param bookId 图书ID
     * @return 是否可借
     */
    boolean isBookAvailable(Integer bookId);

    /**
     * 根据图书ID删除图书
     * @param bookId 图书ID
     * @return 删除结果（影响行数）
     */
    int deleteBookById(Integer bookId);

    /**
     * 按分类统计图书数量
     * @return 分类统计结果列表（包含分类ID、分类名称、图书数量）
     */
    List<Map<String, Object>> countBooksByCategory();

    /**
     * 查询所有书籍的库存信息（包含总库存、已借出、可用数量）
     * @return 书籍库存信息列表
     */
    List<Map<String, Object>> getBooksStockInfo();

    /**
     * 更新书籍库存
     * @param bookId 图书ID
     * @param stock 新库存数量
     * @return 是否更新成功
     */
    boolean updateBookStock(Integer bookId, Integer stock);
}