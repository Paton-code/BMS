package com.paton.mapper;

import com.paton.domain.Book;
import com.paton.domain.BookExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface BookMapper {
    long countByExample(BookExample example);

    int deleteByExample(BookExample example);

    int deleteByPrimaryKey(Integer bookId);

    int insert(Book record);

    int insertSelective(Book record);

    List<Book> selectByExample(BookExample example);

    Book selectByPrimaryKey(Integer bookId);

    int updateByExampleSelective(@Param("record") Book record, @Param("example") BookExample example);

    int updateByExample(@Param("record") Book record, @Param("example") BookExample example);

    int updateByPrimaryKeySelective(Book record);

    int updateByPrimaryKey(Book record);

    List<Book> selectByCategoryId(Integer categoryId);

    List<Book> selectByCategoryIdWithPagination(@Param("categoryId") int categoryId, @Param("offset") int offset, @Param("limit") int limit);

    int selectBookCountByCategoryId(Integer categoryId);

    /**
     * 查询所有分类信息
     * @return 分类列表
     */
    List<Map<String, Object>> selectAllCategories();

    /**
     * 查询所有书籍的库存信息（包含已借出数量和可用数量）
     * @return 书籍库存信息列表
     */
    List<Map<String, Object>> selectAllBooksStockInfo();
}