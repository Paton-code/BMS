package com.paton.service.impl;

import com.paton.domain.*;
import com.paton.domain.Vo.BorrowingBooksVo;
import com.paton.domain.Vo.RegisterVo;
import com.paton.mapper.BookMapper;
import com.paton.mapper.BorrowingBooksMapper;
import com.paton.mapper.DepartmentMapper;
import com.paton.mapper.UserMapper;
import com.paton.service.IUserService;
import com.paton.utils.page.FileUploadUtil;
import com.paton.utils.page.Page;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class UserServiceImpl implements IUserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private DepartmentMapper departmentMapper;
    @Resource
    private BorrowingBooksMapper borrowingBooksMapper;
    @Resource
    private BookMapper bookMapper;

    @Value("${file.upload.base-dir}")
    private String uploadBaseDir;

    @Value("${file.default.avatar}")
    private String defaultAvatar;

    // ==================== IUserService接口方法实现 ====================

    @Override
    public List<User> findUserByUserName(String userName) {
        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();
        criteria.andUserNameEqualTo(userName);
        return userMapper.selectByExample(example);
    }

    @Override
    public List<Department> findAllDepts() {
        return departmentMapper.selectByExample(new DepartmentExample());
    }

    @Override
    public User userLogin(String userName, String password) {
        List<User> users = findUserByUserName(userName);
        if (users == null || users.isEmpty()) {
            return null;
        }
        String encryptedPwd = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
        for (User user : users) {
            if (user.getUserPwd().equals(encryptedPwd)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public boolean updateUser(User user, HttpServletRequest request) {
        User sessionUser = (User) request.getSession().getAttribute("user");
        if (sessionUser == null) return false;

        user.setUserId(sessionUser.getUserId());

        if (user.getUserPwd() != null && !user.getUserPwd().trim().isEmpty()) {
            String encryptedPwd = DigestUtils.md5DigestAsHex(user.getUserPwd().getBytes(StandardCharsets.UTF_8));
            user.setUserPwd(encryptedPwd);
        } else {
            user.setUserPwd(sessionUser.getUserPwd());
        }

        int n = userMapper.updateByPrimaryKeySelective(user);
        if (n > 0) {
            User newUser = userMapper.selectByPrimaryKey(user.getUserId());
            request.getSession().setAttribute("user", newUser);
            return true;
        }
        return false;
    }

    @Override
    public List<BorrowingBooksVo> findAllBorrowingBooks(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) return null;

        BorrowingBooksExample example = new BorrowingBooksExample();
        BorrowingBooksExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(user.getUserId());
        List<BorrowingBooks> list = borrowingBooksMapper.selectByExample(example);

        if (list == null) return null;

        List<BorrowingBooksVo> result = new ArrayList<>();
        for (BorrowingBooks bb : list) {
            Book book = bookMapper.selectByPrimaryKey(bb.getBookId());
            BorrowingBooksVo vo = new BorrowingBooksVo();
            vo.setBook(book);

            Date borrowDate = bb.getDate();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            vo.setDateOfBorrowing(sdf.format(borrowDate));

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(borrowDate);
            calendar.add(Calendar.MONTH, 2);
            vo.setDateOfReturn(sdf.format(calendar.getTime()));

            result.add(vo);
        }
        return result;
    }

    @Override
    public boolean userReturnBook(int bookId, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) return false;

        BorrowingBooksExample example = new BorrowingBooksExample();
        BorrowingBooksExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(user.getUserId());
        criteria.andBookIdEqualTo(bookId);

        int n = borrowingBooksMapper.deleteByExample(example);
        return n > 0;
    }

    @Override
    public Map<String, Object> userBorrowingBook(int bookId, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }

        // 查询这本书被借出了多少本
        BorrowingBooksExample example = new BorrowingBooksExample();
        BorrowingBooksExample.Criteria criteria = example.createCriteria();
        criteria.andBookIdEqualTo(bookId);
        List<BorrowingBooks> list = borrowingBooksMapper.selectByExample(example);
        int borrowedCount = list.size();

        // 查询总库存
        Book book = bookMapper.selectByPrimaryKey(bookId);
        if (book == null) {
            result.put("success", false);
            result.put("message", "图书不存在");
            return result;
        }
        int stockCount = (book.getStock() != null) ? book.getStock() : 1;

        // 如果已借出数量 >= 库存，不可借
        if (borrowedCount >= stockCount) {
            result.put("success", false);
            result.put("message", "库存不足，无法借阅");
            result.put("remainCount", 0);
            return result;
        }

        BorrowingBooks bb = new BorrowingBooks();
        bb.setUserId(user.getUserId());
        bb.setBookId(bookId);
        bb.setDate(new Date());

        int n = borrowingBooksMapper.insert(bb);
        int remainCount = stockCount - borrowedCount - 1;
        result.put("success", n > 0);
        result.put("remainCount", remainCount > 0 ? remainCount : 0);
        return result;
    }

    @Override
    public User findUserById(int id) {
        return userMapper.selectByPrimaryKey(id);
    }

    @Override
    public Page<User> findUserByPage(int pageNum) {
        int pageSize = 10;
        List<User> users = userMapper.selectByPageNum((pageNum - 1) * pageSize, pageSize);
        Page<User> page = new Page<>();
        page.setList(users);
        page.setPageNum(pageNum);
        page.setPageSize(pageSize);

        int totalCount = userMapper.selectUserCount();
        int pageCount = (totalCount + pageSize - 1) / pageSize;
        page.setPageCount(pageCount);
        return page;
    }

    @Override
    public int insertUser(User user) {
        // 检查用户名是否已存在
        if (checkUsernameExists(user.getUserName())) {
            return -2; // 用户名已存在
        }

        // 检查邮箱是否已存在
        if (checkEmailExists(user.getUserEmail())) {
            return -3; // 邮箱已存在
        }

        // 对密码进行MD5加密
        if (user.getUserPwd() != null && !user.getUserPwd().trim().isEmpty()) {
            String encryptedPwd = DigestUtils.md5DigestAsHex(user.getUserPwd().getBytes(StandardCharsets.UTF_8));
            user.setUserPwd(encryptedPwd);
        }

        // 设置默认头像
        if (user.getAvatar() == null) {
            user.setAvatar(defaultAvatar);
        }

        return userMapper.insert(user);
    }

    @Override
    public int deleteUserById(int userId) {
        return userMapper.deleteByPrimaryKey(userId);
    }

    @Override
    public boolean registerUser(RegisterVo registerVo) {
        if (registerVo == null ||
                registerVo.getUserName() == null || registerVo.getUserName().trim().isEmpty() ||
                registerVo.getUserPwd() == null || registerVo.getUserPwd().trim().isEmpty() ||
                registerVo.getUserEmail() == null || registerVo.getUserEmail().trim().isEmpty()) {
            return false;
        }

        if (!registerVo.getUserPwd().equals(registerVo.getConfirmPwd())) return false;

        if (checkUsernameExists(registerVo.getUserName())) return false;
        if (checkEmailExists(registerVo.getUserEmail())) return false;

        User user = new User();
        user.setUserName(registerVo.getUserName().trim());
        String encryptedPwd = DigestUtils.md5DigestAsHex(registerVo.getUserPwd().getBytes(StandardCharsets.UTF_8));
        user.setUserPwd(encryptedPwd);
        user.setUserEmail(registerVo.getUserEmail().trim());
        user.setAvatar(registerVo.getAvatar() != null ? registerVo.getAvatar() : defaultAvatar);

        return userMapper.insertSelective(user) > 0;
    }

    @Override
    public String uploadAvatar(MultipartFile file, Integer userId) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        if (!FileUploadUtil.checkFileSize(file, 5)) {
            throw new IllegalArgumentException("文件大小不能超过5MB");
        }

        String relativePath = FileUploadUtil.uploadFile(file, uploadBaseDir, "avatars");
        if (relativePath != null && userId != null) {
            User user = userMapper.selectByPrimaryKey(userId);
            if (user != null) {
                if (user.getAvatar() != null && !user.getAvatar().equals(defaultAvatar)) {
                    FileUploadUtil.deleteFile(user.getAvatar(), uploadBaseDir);
                }
                user.setAvatar(relativePath);
                userMapper.updateByPrimaryKeySelective(user);
            }
        }
        return relativePath;
    }

    @Override
    public boolean checkUsernameExists(String username) {
        UserExample example = new UserExample();
        example.createCriteria().andUserNameEqualTo(username.trim());
        return userMapper.countByExample(example) > 0;
    }

    @Override
    public boolean checkEmailExists(String email) {
        UserExample example = new UserExample();
        example.createCriteria().andUserEmailEqualTo(email.trim());
        return userMapper.countByExample(example) > 0;
    }

    @Override
    public boolean updateUserAvatar(Integer userId, String avatarPath) {
        if (userId == null || avatarPath == null) return false;
        User user = new User();
        user.setUserId(userId);
        user.setAvatar(avatarPath);
        return userMapper.updateByPrimaryKeySelective(user) > 0;
    }

}