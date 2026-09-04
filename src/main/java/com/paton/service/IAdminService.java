package com.paton.service;

import com.paton.domain.Admin;
import com.paton.domain.Book;
import com.paton.domain.BookCategory;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IAdminService {

    //验证用户是否存在
    public boolean adminIsExist(String name);

    //管理员登陆
    public boolean adminLogin(String name,String password);

    //录入新书
    public boolean addBook(Book book);

    //获取所有图书类别
    public List<BookCategory> getBookCategorys();

    //增加图书类别
    public boolean addBookCategory(BookCategory bookCategory);

    // ==================== 新增功能：管理员头像和图书封面 ====================

    /**
     * 上传管理员头像
     * @param file 头像文件
     * @param adminId 管理员ID
     * @return 头像相对路径
     * @throws Exception 上传异常
     */
    String uploadAdminAvatar(MultipartFile file, Integer adminId) throws Exception;

    /**
     * 获取管理员信息（包括头像）
     * @param adminId 管理员ID
     * @return 管理员信息
     */
    Admin getAdminWithAvatar(Integer adminId);

    /**
     * 上传图书封面
     * @param file 封面文件
     * @param bookId 图书ID
     * @return 封面相对路径
     * @throws Exception 上传异常
     */
    String uploadBookCover(MultipartFile file, Integer bookId) throws Exception;

    /**
     * 添加图书（带封面）
     * @param book 图书信息
     * @param coverFile 封面文件
     * @return 是否添加成功
     * @throws Exception 上传异常
     */
    boolean addBookWithCover(Book book, MultipartFile coverFile) throws Exception;

    /**
     * 更新管理员头像信息
     * @param adminId 管理员ID
     * @param avatarPath 头像路径
     * @return 是否更新成功
     */
    boolean updateAdminAvatar(Integer adminId, String avatarPath);
}