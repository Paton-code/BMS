package com.paton.mapper;

import com.paton.domain.Favorite;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface FavoriteMapper {
    int insert(Favorite favorite);
    int deleteByPrimaryKey(Integer favoriteId);
    int deleteByUserAndBook(Integer userId, Integer bookId);
    Favorite selectByPrimaryKey(Integer favoriteId);
    List<Favorite> selectByUserId(Integer userId);
    int countByUserId(Integer userId);
    boolean existsByUserAndBook(Integer userId, Integer bookId);
    List<Favorite> selectFavoritesWithBookInfo(Integer userId);
    List<Favorite> selectAllFavoritesWithUserAndBookInfo();
    List<Favorite> selectFavoritesByCondition(@Param("userName") String userName, @Param("bookName") String bookName, @Param("offset") Integer offset, @Param("limit") Integer limit);
    int countFavoritesByCondition(@Param("userName") String userName, @Param("bookName") String bookName);
}