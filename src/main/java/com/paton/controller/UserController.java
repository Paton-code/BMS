package com.paton.controller;

import com.paton.domain.Department;
import com.paton.domain.User;
import com.paton.domain.Vo.BookVo;
import com.paton.domain.Vo.BorrowingBooksVo;
import com.paton.domain.Vo.RegisterVo;
import com.paton.service.IBookService;
import com.paton.service.IBorrowingBooksRecordService;
import com.paton.service.IQueryService;
import com.paton.service.IUserService;
import com.paton.utils.page.Page;
import com.paton.service.IFavoriteService;
import javax.servlet.http.HttpSession;
import com.paton.domain.Favorite;
import com.paton.utils.page.OperationLogUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.context.annotation.RequestScope;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class UserController {

    @Resource
    private IUserService userService;

    @Resource
    private IBorrowingBooksRecordService borrowingBooksRecordService;

    @Resource
    private IBookService bookService;

    @Resource
    private IQueryService queryService;

    @Resource
    private IFavoriteService favoriteService;

    /**
     * 用户登录
     * @param userName
     * @return
     */
    @RequestMapping("/userLogin")
    @ResponseBody
    public Map<String, Object> userLogin(@Param("userName") String userName,
                                         @Param("password") String password, HttpServletRequest request){
        Map<String, Object> result = new HashMap<>();
        User user=userService.userLogin(userName,password);
        if(null!=user){
            request.getSession().setAttribute("user",user);
            result.put("success", true);
            result.put("redirectUrl", "/userIndex");
            return result;
        }
        result.put("success", false);
        result.put("message", "用户名或密码错误");
        return result;
    }

    /**
     * //验证用户是否存在
     * @param userName
     * @return
     */
    @RequestMapping("/isUserExist")
    @ResponseBody
    public String isUserExist(@Param("userName") String userName){
        List<User> users=userService.findUserByUserName(userName);
        if(null==users){
            return "false";
        }
        if(users.size()<1){
            return "false";
        }
        return "true";
    }

    /**
     * 查找所有部门
     * @return
     */
    @RequestMapping("/getDepts")
    @ResponseBody
    public List<Department> getDepts(){
        List<Department> depts=userService.findAllDepts();
        return depts;
    }

    /**
     * 返回用户借书记录页面
     * @param model
     * @param request
     * @return
     */
    @RequestMapping("/userBorrowingBooksPage")
    public String userBorrowingBooksPage(Model model, HttpServletRequest request, @RequestParam("pageNum") int pageNum){
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return "redirect:/index";
        }

        Page<BorrowingBooksVo> page = queryService.queryBorrowingRecords(user.getUserId(), "user", pageNum);
        model.addAttribute("page", page);
        return "user/borrowingBooksRecord";
    }

    /**
     * 返回还书页面
     * @return
     */
    @RequestMapping("/userReturnBooksPage")
    public String userReturnBooksPage(){
        return "user/returnBooks";
    }

    /**
     * 返回个人信息页面
     * @return
     */
    @RequestMapping("/userMessagePage")
    public String userMessagePage(Model model,HttpServletRequest request){
        User session_user= (User) request.getSession().getAttribute("user");
        User user=userService.findUserById(session_user.getUserId());
        model.addAttribute("message_user",user);
        return "user/userMessage";
    }

    /**
     * 返回借书页面
     * @return
     */
    @RequestMapping("/borrowingPage")
    public String borrowing(){
        return "user/borrowingBooks";
    }
    /**
     * 返回用户首页
     * @return
     */
    @RequestMapping("/userIndex")
    public String userIndex(){
        return "user/index";
    }
    /**
     * @author yangxuechen
     * @date  2018/10/11
     * 更新用户信息
     * @param user
     * @param request
     * @return
     */
    @RequestMapping("/updateUser")
    @ResponseBody
    public boolean updateUser(User user,HttpServletRequest request){
        return userService.updateUser(user,request);
    }

    /**
     * 用户还书
     * @param bookId
     * @param request
     * @return
     */
    @RequestMapping("/userReturnBook")
    @ResponseBody
    public boolean returnBook(int bookId,HttpServletRequest request){
        return userService.userReturnBook(bookId,request);
    }

    /**
     * 用户借书
     * @param bookId
     * @param request
     * @return
     */
    @RequestMapping("/userBorrowingBook")
    @ResponseBody
    public Map<String, Object> borrowingBook(int bookId, HttpServletRequest request) {
        return userService.userBorrowingBook(bookId, request);
    }

    /**
     * 返回管理员登陆界面
     * @return
     */
    @RequestMapping("/adminLoginPage")
    public String adminLoginPage(){
        return "adminLogin";
    }

    /**
     * 用户退出登陆
     * @param request
     * @return
     */
    @RequestMapping("/userLogOut")
    public String userLogOut(HttpServletRequest request){
        request.getSession().invalidate();
        return "index";
    }

    /**
     * 返回用户索书页面
     * @return
     */
    @RequestMapping("/findBookPage")
    public String findBookPage(){
        return "user/findBook";
    }

    // ==================== 新增功能：用户注册和头像上传 ====================

    /**
     * 返回用户注册页面
     * @return
     */
    @RequestMapping("/registerPage")
    public String registerPage() {
        return "user/register";
    }

    /**
     * 用户注册
     * @param userName 用户名
     * @param userPwd 密码
     * @param confirmPwd 确认密码
     * @param userEmail 邮箱
     * @param avatarFile 头像文件
     * @return 注册结果
     */
    @RequestMapping("/register")
    @ResponseBody
    public Map<String, Object> register(@RequestParam("userName") String userName,
                                        @RequestParam("userPwd") String userPwd,
                                        @RequestParam("confirmPwd") String confirmPwd,
                                        @RequestParam("userEmail") String userEmail,
                                        @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile) {
        Map<String, Object> result = new HashMap<>();

        try {
            RegisterVo registerVo = new RegisterVo();
            registerVo.setUserName(userName);
            registerVo.setUserPwd(userPwd);
            registerVo.setConfirmPwd(confirmPwd);
            registerVo.setUserEmail(userEmail);

            // 处理头像上传
            if (avatarFile != null && !avatarFile.isEmpty()) {
                String avatarPath = userService.uploadAvatar(avatarFile, null);
                registerVo.setAvatar(avatarPath);
            }

            boolean success = userService.registerUser(registerVo);
            if (success) {
                result.put("success", true);
                result.put("message", "注册成功");
            } else {
                result.put("success", false);
                result.put("message", "注册失败，请检查输入信息");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "注册失败：" + e.getMessage());
        }

        return result;
    }

    /**
     * 检查用户名是否已存在
     * @param username 用户名
     * @return 是否存在
     */
    @RequestMapping("/checkUsername")
    @ResponseBody
    public Map<String, Object> checkUsername(@RequestParam("username") String username) {
        Map<String, Object> result = new HashMap<>();
        boolean exists = userService.checkUsernameExists(username);
        result.put("exists", exists);
        result.put("valid", !exists);
        result.put("message", exists ? "用户名已存在" : "用户名可用");
        return result;
    }

    /**
     * 检查邮箱是否已存在
     * @param email 邮箱
     * @return 是否存在
     */
    @RequestMapping("/checkEmail")
    @ResponseBody
    public Map<String, Object> checkEmail(@RequestParam("email") String email) {
        Map<String, Object> result = new HashMap<>();
        boolean exists = userService.checkEmailExists(email);
        result.put("exists", exists);
        result.put("valid", !exists);
        result.put("message", exists ? "邮箱已注册" : "邮箱可用");
        return result;
    }

    /**
     * 返回头像上传页面
     * @return
     */
    @RequestMapping("/uploadAvatarPage")
    public String uploadAvatarPage() {
        return "user/updateAvatar";
    }

    /**
     * 上传用户头像
     * @param file 头像文件
     * @param request HTTP请求
     * @return 上传结果
     */
    @RequestMapping("/uploadAvatar")
    @ResponseBody
    public Map<String, Object> uploadAvatar(@RequestParam("avatarFile") MultipartFile file,
                                            HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 从session获取当前用户
            User sessionUser = (User) request.getSession().getAttribute("user");
            if (sessionUser == null) {
                result.put("success", false);
                result.put("message", "用户未登录");
                return result;
            }

            // 上传头像
            String avatarPath = userService.uploadAvatar(file, sessionUser.getUserId());

            if (avatarPath != null) {
                // 更新session中的用户信息
                User updatedUser = userService.findUserById(sessionUser.getUserId());
                request.getSession().setAttribute("user", updatedUser);

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
     * 获取当前用户的头像信息
     * @param request HTTP请求
     * @return 头像信息
     */
    @RequestMapping("/getUserAvatar")
    @ResponseBody
    public Map<String, Object> getUserAvatar(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();

        User sessionUser = (User) request.getSession().getAttribute("user");
        if (sessionUser != null) {
            User user = userService.findUserById(sessionUser.getUserId());
            result.put("success", true);
            result.put("avatar", user.getAvatar());
            result.put("userName", user.getUserName());
        } else {
            result.put("success", false);
            result.put("message", "用户未登录");
        }

        return result;
    }

    // ==================== 新增功能：用户收藏管理 ====================

    /**
     * 返回用户收藏页面
     */
    @RequestMapping("/userFavoritesPage")
    public String userFavoritesPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/index";
        }

        List<Favorite> favorites = favoriteService.getFavoritesByUserId(user.getUserId());
        model.addAttribute("favorites", favorites);
        return "user/userFavorites";
    }

    /**
     * 添加收藏
     */
    @PostMapping("/addFavorite")
    @ResponseBody
    public Map<String, Object> addFavorite(@RequestParam Integer bookId,
                                           HttpSession session,
                                           HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            result.put("success", false);
            result.put("message", "用户未登录");
            return result;
        }

        try {
            boolean success = favoriteService.addFavorite(user.getUserId(), bookId);
            if (success) {
                result.put("success", true);
                result.put("message", "收藏成功");

                // 记录操作日志
                OperationLogUtil.recordLogWithTarget(
                        1,
                        user.getUserName(),
                        "收藏",
                        "图书管理",
                        "用户收藏图书",
                        "图书ID: " + bookId,
                        request
                );
            } else {
                result.put("success", false);
                result.put("message", "收藏失败或已收藏");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "收藏失败：" + e.getMessage());
        }

        return result;
    }

    /**
     * 取消收藏
     */
    @PostMapping("/removeFavorite")
    @ResponseBody
    public Map<String, Object> removeFavorite(@RequestParam Integer favoriteId,
                                              HttpSession session,
                                              HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            result.put("success", false);
            result.put("message", "用户未登录");
            return result;
        }

        try {
            boolean success = favoriteService.removeFavorite(favoriteId, user.getUserId());
            if (success) {
                result.put("success", true);
                result.put("message", "取消收藏成功");

                // 记录操作日志
                OperationLogUtil.recordLogWithTarget(
                        1,
                        user.getUserName(),
                        "取消收藏",
                        "图书管理",
                        "用户取消收藏图书",
                        "收藏ID: " + favoriteId,
                        request
                );
            } else {
                result.put("success", false);
                result.put("message", "取消收藏失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "取消收藏失败：" + e.getMessage());
        }

        return result;
    }

    /**
     * 检查是否已收藏
     */
    @GetMapping("/checkFavorite")
    @ResponseBody
    public Map<String, Object> checkFavorite(@RequestParam Integer bookId,
                                             HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            result.put("success", false);
            result.put("isFavorite", false);
            return result;
        }

        try {
            boolean isFavorite = favoriteService.isFavorite(user.getUserId(), bookId);
            result.put("success", true);
            result.put("isFavorite", isFavorite);
        } catch (Exception e) {
            result.put("success", false);
            result.put("isFavorite", false);
        }

        return result;
    }

    /**
     * 批量检查收藏状态
     */
    @PostMapping("/checkFavoritesStatus")
    @ResponseBody
    public Map<String, Object> checkFavoritesStatus(@RequestBody List<Integer> bookIds,
                                                    HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            result.put("success", false);
            result.put("message", "未登录");
            return result;
        }

        try {
            Map<String, Boolean> statusMap = new HashMap<>();
            for (Integer bookId : bookIds) {
                boolean isFavorite = favoriteService.isFavorite(user.getUserId(), bookId);
                statusMap.put(String.valueOf(bookId), isFavorite);
            }
            result.put("success", true);
            result.putAll(statusMap);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }

        return result;
    }

    /**
     * 根据图书ID取消收藏
     */
    @PostMapping("/removeFavoriteByBook")
    @ResponseBody
    public Map<String, Object> removeFavoriteByBook(@RequestParam Integer bookId,
                                                    HttpSession session,
                                                    HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            result.put("success", false);
            result.put("message", "用户未登录");
            return result;
        }

        try {
            // 先查询收藏记录
            List<Favorite> favorites = favoriteService.getFavoritesByUserId(user.getUserId());
            Integer favoriteId = null;

            for (Favorite favorite : favorites) {
                if (favorite.getBookId().equals(bookId)) {
                    favoriteId = favorite.getFavoriteId();
                    break;
                }
            }

            if (favoriteId == null) {
                result.put("success", false);
                result.put("message", "未找到收藏记录");
                return result;
            }

            boolean success = favoriteService.removeFavorite(favoriteId, user.getUserId());
            if (success) {
                result.put("success", true);
                result.put("message", "取消收藏成功");

                // 记录操作日志
                OperationLogUtil.recordLogWithTarget(
                        1,
                        user.getUserName(),
                        "取消收藏",
                        "图书管理",
                        "用户取消收藏图书",
                        "图书ID: " + bookId,
                        request
                );
            } else {
                result.put("success", false);
                result.put("message", "取消收藏失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "取消收藏失败：" + e.getMessage());
        }

        return result;
    }

}
