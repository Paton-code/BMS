package com.paton.service;

import com.paton.domain.Favorite;
import com.paton.utils.page.Page;
import java.util.List;

public interface IFavoriteService {

    /**
     * 添加收藏
     */
    boolean addFavorite(Integer userId, Integer bookId);

    /**
     * 取消收藏
     */
    boolean removeFavorite(Integer favoriteId, Integer userId);

    /**
     * 管理员删除收藏记录
     */
    boolean adminRemoveFavorite(Integer favoriteId);

    /**
     * 获取用户收藏列表
     */
    List<Favorite> getFavoritesByUserId(Integer userId);

    /**
     * 获取用户收藏数量
     */
    int getFavoriteCountByUserId(Integer userId);

    /**
     * 检查是否已收藏
     */
    boolean isFavorite(Integer userId, Integer bookId);

    /**
     * 分页查询所有用户收藏记录
     */
    Page<Favorite> getFavoritesByPage(Integer pageNum, Integer pageSize, String userName, String bookName);

    /**
     * 根据用户和图书ID取消收藏
     */
    boolean removeFavoriteByUserAndBook(Integer userId, Integer bookId);
}