package com.paton.service.impl;

import com.paton.domain.Favorite;
import com.paton.mapper.FavoriteMapper;
import com.paton.service.IFavoriteService;
import com.paton.utils.page.Page;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class FavoriteServiceImpl implements IFavoriteService {

    @Resource
    private FavoriteMapper favoriteMapper;

    @Override
    public boolean addFavorite(Integer userId, Integer bookId) {
        try {
            // 检查是否已收藏
            if (favoriteMapper.existsByUserAndBook(userId, bookId)) {
                return false; // 已收藏，无需重复添加
            }

            Favorite favorite = new Favorite();
            favorite.setUserId(userId);
            favorite.setBookId(bookId);
            favorite.setCreateTime(new Date());

            int result = favoriteMapper.insert(favorite);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean removeFavorite(Integer favoriteId, Integer userId) {
        try {
            // 验证用户权限
            Favorite favorite = favoriteMapper.selectByPrimaryKey(favoriteId);
            if (favorite == null || !favorite.getUserId().equals(userId)) {
                return false;
            }

            int result = favoriteMapper.deleteByPrimaryKey(favoriteId);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean adminRemoveFavorite(Integer favoriteId) {
        try {
            int result = favoriteMapper.deleteByPrimaryKey(favoriteId);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Favorite> getFavoritesByUserId(Integer userId) {
        try {
            return favoriteMapper.selectFavoritesWithBookInfo(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int getFavoriteCountByUserId(Integer userId) {
        try {
            return favoriteMapper.countByUserId(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public boolean isFavorite(Integer userId, Integer bookId) {
        try {
            return favoriteMapper.existsByUserAndBook(userId, bookId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Page<Favorite> getFavoritesByPage(Integer pageNum, Integer pageSize, String userName, String bookName) {
        Page<Favorite> page = new Page<>();
        page.setPageNum(pageNum);
        page.setPageSize(pageSize);

        try {
            int totalCount = favoriteMapper.countFavoritesByCondition(userName, bookName);
            page.setTotalCount(totalCount);

            if (totalCount > 0) {
                int offset = (pageNum - 1) * pageSize;
                List<Favorite> favorites = favoriteMapper.selectFavoritesByCondition(userName, bookName, offset, pageSize);
                page.setList(favorites);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return page;
    }

    @Override
    public boolean removeFavoriteByUserAndBook(Integer userId, Integer bookId) {
        try {
            int result = favoriteMapper.deleteByUserAndBook(userId, bookId);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}