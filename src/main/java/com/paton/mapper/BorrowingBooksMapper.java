package com.paton.mapper;

import com.paton.domain.BorrowingBooks;
import com.paton.domain.BorrowingBooksExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface BorrowingBooksMapper {

    long countByExample(BorrowingBooksExample example);

    int deleteByExample(BorrowingBooksExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(BorrowingBooks record);

    int insertSelective(BorrowingBooks record);

    List<BorrowingBooks> selectByExample(BorrowingBooksExample example);

    BorrowingBooks selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") BorrowingBooks record,
                                 @Param("example") BorrowingBooksExample example);

    int updateByExample(@Param("record") BorrowingBooks record,
                        @Param("example") BorrowingBooksExample example);

    int updateByPrimaryKeySelective(BorrowingBooks record);

    int updateByPrimaryKey(BorrowingBooks record);

    // 分页查询指定用户借书记录
    List<BorrowingBooks> selectByPageNumAndPageSize(@Param("userId") int userId,
                                                    @Param("currIndex") int currIndex,
                                                    @Param("pageSize") int pageSize);

    // 查询指定用户借书总数
    int selectAllRecordCount(@Param("userId") int userId);

    // 分页查询所有借书记录
    List<BorrowingBooks> selectAllByPage(@Param("currIndex") int currIndex,
                                         @Param("pageSize") int pageSize);

    // 查询所有借书记录总数
    int selectAll();

}

