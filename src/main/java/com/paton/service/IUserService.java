package com.paton.service;

import com.paton.domain.BorrowingBooks;
import com.paton.domain.Department;
import com.paton.domain.User;
import com.paton.domain.Vo.BorrowingBooksVo;
import com.paton.domain.Vo.RegisterVo;
import com.paton.utils.page.Page;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Map;

public interface IUserService {

    //验证用户是否存在
    List<User> findUserByUserName(String userName);

    //查询所有部门
    List<Department> findAllDepts();

    //用户登录
    User userLogin(String userName,String password);

    //更新用户信息
    boolean updateUser(User user, HttpServletRequest request);

    //查询用户借书记录
    List<BorrowingBooksVo> findAllBorrowingBooks(HttpServletRequest request);

    //用户还书
    boolean userReturnBook(int bookId,HttpServletRequest request);

    //用户借书
    Map<String, Object> userBorrowingBook(int bookId,HttpServletRequest request);

    //通过id查找用户
    User findUserById(int id);

    //分页查询用户
    Page<User> findUserByPage(int pageNum);

    //添加用户
    int insertUser(User user);

    //根据用户id删除用户
    int deleteUserById(int userId);

    // ==================== 新增功能：用户注册和头像上传 ====================

    /**
     * 用户注册
     * @param registerVo 注册信息
     * @return 是否注册成功
     */
    boolean registerUser(RegisterVo registerVo);

    /**
     * 上传用户头像
     * @param file 头像文件
     * @param userId 用户ID
     * @return 头像相对路径
     * @throws Exception 上传异常
     */
    String uploadAvatar(MultipartFile file, Integer userId) throws Exception;

    /**
     * 检查用户名是否已存在
     * @param username 用户名
     * @return 是否存在
     */
    boolean checkUsernameExists(String username);

    /**
     * 检查邮箱是否已存在
     * @param email 邮箱
     * @return 是否存在
     */
    boolean checkEmailExists(String email);

    /**
     * 更新用户头像信息
     * @param userId 用户ID
     * @param avatarPath 头像路径
     * @return 是否更新成功
     */
    boolean updateUserAvatar(Integer userId, String avatarPath);
}