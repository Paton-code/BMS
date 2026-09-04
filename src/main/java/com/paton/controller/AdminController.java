package com.paton.controller;

import com.paton.domain.Book;
import com.paton.domain.BookCategory;
import com.paton.domain.User;
import com.paton.domain.Vo.BookVo;
import com.paton.domain.Vo.BorrowingBooksVo;
import com.paton.service.*;
import com.paton.utils.page.Page;
import com.paton.utils.page.OperationLogUtil;
import com.paton.domain.Favorite;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AdminController {

    @Resource
    private IAdminService adminService;
    @Resource
    private IBookCategoryService bookCategoryService;
    @Resource
    private IUserService userService;
    @Resource
    private IFavoriteService favoriteService;
    @Resource
    private IBorrowingBooksRecordService borrowingBooksRecordService;

    /**
     * 判断admin是否存在
     * @param adminName
     * @return
     */
    @RequestMapping("/isAdminExist")
    @ResponseBody
    public String adminIsExist(@Param("adminName") String adminName){
        boolean b=adminService.adminIsExist(adminName);
        if(b){
            return "true";
        }else{
            return "false";
        }
    }

    /**
     * 管理员登录页面
     * @return
     */
    @RequestMapping(value = "/adminLogin", method = RequestMethod.GET)
    public String adminLoginPage() {
        return "adminLogin";
    }

    /**
     * 管理员登陆
     * @param adminName
     * @param password
     * @return
     */
    @RequestMapping(value = "/adminLogin", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> adminLogin(@RequestParam("adminName") String adminName,
                                          @RequestParam("password") String password,
                                          HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        boolean res = adminService.adminLogin(adminName, password);
        if (res == false) {
            result.put("success", false);
            result.put("message", "管理员账号或密码错误");
            return result;
        }
        request.getSession().setAttribute("admin", "admin");

        // 记录登录日志
        OperationLogUtil.recordLog(1, adminName, "登录", "系统管理",
                "管理员登录系统", request);

        result.put("success", true);
        result.put("redirectUrl", "/adminIndex");
        return result;
    }

    /**
     * 返回添加书籍页面
     * @return
     */
    @RequestMapping("/addBookPage")
    public String addBookPage(){
        return "admin/addBook";
    }

    /**
     * 返回添加类别页面
     * @return
     */
    @RequestMapping("/addCategoryPage")
    public String addCategoryPage(@RequestParam("pageNum") int pageNum, Model model){
        Page<BookCategory> page=bookCategoryService.selectBookCategoryByPageNum(pageNum);
        model.addAttribute("page",page);
        return "admin/addCategory";
    }

    /**
     * 返回查询状态页面
     * @return
     */
    @RequestMapping("/showStausPage")
    public String showStausPage(){
        return "admin/showStaus";
    }

    /**
     * 返回管理员首页
     * @return
     */
    @RequestMapping("/adminIndex")
    public String returnAdminIndexPage(){
        return "admin/index";
    }

    /**
     * 返回查询用户页面
     * @return
     */
    @RequestMapping("/showUsersPage")
    public String showUsersPage(Model model,@RequestParam("pageNum") int pageNum){
        Page<User> page=userService.findUserByPage(pageNum);
        model.addAttribute("page",page);
        return "admin/showUsers";
    }

    /**
     * 返回查询书籍页面
     * @return
     */
    @RequestMapping("/showBooksPage")
    public String showBooksPage(Model model){
        Page<BookVo> page=new Page<BookVo>();
        page.setPageCount(1);
        page.setPageNum(1);
        model.addAttribute("page",page);
        return "admin/showBooks";
    }

    /**
     * 管理员退出登陆
     * @param request
     * @return
     */
    @RequestMapping("/adminLogOut")
    public String userLogOut(HttpServletRequest request){
        // 记录退出登录日志
        OperationLogUtil.recordLog(1, "admin", "退出", "系统管理",
                "管理员退出系统", request);

        request.getSession().invalidate();
        return "adminLogin";
    }

    /**
     * 返回新增用户页面
     * @return
     */
    @RequestMapping("/addUserPage")
    public String addUserPage(){
        return "admin/addUser";
    }

    /**
     * 添加用户
     * @param user
     * @return
     */
    @RequestMapping("/addUser")
    @ResponseBody
    public String addUser(User user, HttpServletRequest request){
        int res = userService.insertUser(user);

        // 记录添加用户日志
        if (res > 0) {
            OperationLogUtil.recordLogWithTarget(1, "admin", "新增", "用户管理",
                    "添加新用户", user.getUserName(), request);
            return "true";
        } else if (res == -2) {
            return "duplicate_name"; // 用户名已存在
        } else if (res == -3) {
            return "duplicate_email"; // 邮箱已存在
        } else {
            OperationLogUtil.recordLogWithTarget(1, "admin", "新增", "用户管理",
                    "添加新用户失败", user.getUserName(), request);
            return "false";
        }
    }

    /**
     * 根据用户id删除用户
     * @param userId
     * @return
     */
    @RequestMapping("/deleteUser")
    @ResponseBody
    public String deleteUserByUserId(@RequestParam("userId") int userId, HttpServletRequest request){
        int res=userService.deleteUserById(userId);
        if(res>0){
            // 记录删除用户日志
            OperationLogUtil.recordLog(1, "admin", "删除", "用户管理",
                    "删除用户，用户ID：" + userId, request);
            return "true";
        }
        return "false";
    }

    /**
     * 录入新书
     * @param book
     * @return
     */
    @RequestMapping("/addBook")
    @ResponseBody
    public String addBook(Book book, HttpServletRequest request){
        boolean b=adminService.addBook(book);
        if(b){
            // 记录添加图书日志
            OperationLogUtil.recordLogWithTarget(1, "admin", "新增", "图书管理",
                    "录入新书", book.getBookName(), request);
            return "true";
        }
        return "false";
    }

    /**
     * 新建书籍种类
     * @param bookCategory
     * @return
     */
    @RequestMapping("/addBookCategory")
    @ResponseBody
    public String addBookCategory(BookCategory bookCategory, HttpServletRequest request){
        try {
            boolean b=adminService.addBookCategory(bookCategory);
            if(b){
                // 记录添加分类日志
                OperationLogUtil.recordLogWithTarget(1, "admin", "新增", "分类管理",
                        "新建书籍类别", bookCategory.getCategoryName(), request);
                return "true";
            }
            return "false";
        } catch (Exception e) {
            // 区分主键重复(编号)和唯一键重复(名称)
            String msg = e.getMessage();
            if (msg != null && msg.contains("PRIMARY")) {
                return "duplicate_id";
            }
            return "duplicate_name";
        }
    }

    /**
     * 根据书籍种类id删除种类
     * @param bookCategoryId
     * @return
     */
    @RequestMapping("/deleteCategory")
    @ResponseBody
    public String deleteBookCategoryById(@RequestParam("bookCategoryId") int bookCategoryId, HttpServletRequest request){
        try {
            int res=bookCategoryService.deleteBookCategoryById(bookCategoryId);
            if(res>0){
                // 记录删除分类日志
                OperationLogUtil.recordLog(1, "admin", "删除", "分类管理",
                        "删除书籍类别，类别ID：" + bookCategoryId, request);
                return "true";
            }
            return "false";
        } catch (Exception e) {
            // 外键约束等异常，返回false让前端显示友好提示
            return "false";
        }
    }

    // ==================== 新增功能：管理员头像上传 ====================

    /**
     * 上传管理员头像
     * @param file 头像文件
     * @param request HTTP请求
     * @return 上传结果
     */
    @RequestMapping("/uploadAdminAvatar")
    @ResponseBody
    public Map<String, Object> uploadAdminAvatar(@RequestParam("avatarFile") MultipartFile file,
                                                 HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 从session获取当前管理员
            Object adminObj = request.getSession().getAttribute("admin");
            if (adminObj == null) {
                result.put("success", false);
                result.put("message", "管理员未登录");
                return result;
            }

            // 这里需要根据实际的管理员ID获取，暂时使用固定值
            // 实际项目中应该从session中获取管理员ID
            Integer adminId = 1; // 默认管理员ID

            // 上传头像
            String avatarPath = adminService.uploadAdminAvatar(file, adminId);

            if (avatarPath != null) {
                // 记录头像上传日志
                OperationLogUtil.recordLog(1, "admin", "修改", "系统管理",
                        "上传管理员头像", request);

                result.put("success", true);
                result.put("message", "头像上传成功");
                result.put("avatarPath", avatarPath);
            } else {
                result.put("success", false);
                result.put("message", "头像上传失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "头像上传失败：" + e.getMessage());
        }

        return result;
    }

    /**
     * 获取管理员头像信息
     * @param request HTTP请求
     * @return 头像信息
     */
    @RequestMapping("/getAdminAvatar")
    @ResponseBody
    public Map<String, Object> getAdminAvatar(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();

        Object adminObj = request.getSession().getAttribute("admin");
        if (adminObj != null) {
            // 这里需要根据实际的管理员ID获取，暂时使用固定值
            Integer adminId = 1; // 默认管理员ID
            com.paton.domain.Admin admin = adminService.getAdminWithAvatar(adminId);

            if (admin != null) {
                result.put("success", true);
                result.put("avatar", admin.getAvatar());
            } else {
                result.put("success", false);
                result.put("message", "管理员信息不存在");
            }
        } else {
            result.put("success", false);
            result.put("message", "管理员未登录");
        }

        return result;
    }

    /**
     * 返回管理员头像上传页面
     * @return
     */
    @RequestMapping("/adminUploadAvatarPage")
    public String adminUploadAvatarPage() {
        return "admin/updateAvatar";
    }

    /**
     * 返回所有借阅记录页面
     * @return
     */
    @RequestMapping("/allBorrowingBooksRecordPage")
    public String allBorrowingBooksRecordPage(@RequestParam(defaultValue = "1") int pageNum, Model model) {
        Page<BorrowingBooksVo> page = borrowingBooksRecordService.selectAllByPage(pageNum);
        model.addAttribute("page", page);
        return "admin/allBorrowingBooksRecord";
    }

    // ==================== 新增功能：用户收藏管理 ====================

    /**
     * 返回用户收藏管理页面
     */
    @RequestMapping("/adminUserFavoritesPage")
    public String adminUserFavoritesPage(@RequestParam(defaultValue = "1") Integer pageNum,
                                         @RequestParam(required = false) String userName,
                                         @RequestParam(required = false) String bookName,
                                         Model model) {
        Page<Favorite> page = favoriteService.getFavoritesByPage(pageNum, 10, userName, bookName);
        model.addAttribute("page", page);
        return "admin/adminUserFavorites";
    }

    /**
     * 管理员删除收藏记录
     */
    @PostMapping("/admin/removeFavorite")
    @ResponseBody
    public String adminRemoveFavorite(@RequestParam Integer favoriteId,
                                      HttpServletRequest request) {
        try {
            boolean success = favoriteService.adminRemoveFavorite(favoriteId);
            if (success) {
                // 记录操作日志
                OperationLogUtil.recordLog(
                        1,
                        "admin",
                        "删除",
                        "用户管理",
                        "管理员删除用户收藏记录，收藏ID：" + favoriteId,
                        request
                );
                return "true";
            }
            return "false";
        } catch (Exception e) {
            return "false";
        }
    }

}