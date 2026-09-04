package com.paton.controller;

import com.paton.domain.Book;
import com.paton.domain.BookCategory;
import com.paton.domain.Vo.BookVo;
import com.paton.service.IAdminService;
import com.paton.service.IBookCategoryService;
import com.paton.service.IBookService;
import com.paton.utils.page.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class BookController {
    @Resource
    private IAdminService adminService;
    @Resource
    private IBookService bookService;
    @Resource
    private IBookCategoryService bookCategoryService;

    /**
     * 管理员录入新书（带封面）
     * @param book 图书信息
     * @param coverFile 封面文件
     * @return 添加结果
     */
    @RequestMapping("/addBookWithCover")
    @ResponseBody
    public Map<String, Object> addBookWithCover(Book book,
                                                @RequestParam(value = "coverFile", required = false) MultipartFile coverFile) {
        Map<String, Object> result = new HashMap<>();

        try {
            boolean success = adminService.addBookWithCover(book, coverFile);

            if (success) {
                result.put("success", true);
                result.put("message", "图书添加成功");
            } else {
                result.put("success", false);
                result.put("message", "图书添加失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "图书添加失败：" + e.getMessage());
        }

        return result;
    }

    /**
     * 上传图书封面
     * @param file 封面文件
     * @param bookId 图书ID
     * @return 上传结果
     */
    @RequestMapping("/uploadBookCover")
    @ResponseBody
    public Map<String, Object> uploadBookCover(@RequestParam("coverFile") MultipartFile file,
                                               @RequestParam("bookId") Integer bookId) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 上传封面
            String coverPath = adminService.uploadBookCover(file, bookId);

            if (coverPath != null) {
                result.put("success", true);
                result.put("message", "封面上传成功");
                result.put("coverPath", coverPath);
            } else {
                result.put("success", false);
                result.put("message", "封面上传失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "封面上传失败：" + e.getMessage());
        }

        return result;
    }

    /**
     * 返回查询书籍结果页
     * @param pageNum
     * @param model
     * @return
     */
    @RequestMapping("/showBooksResultPageByCategoryId")
    public String showBooksResultPageByCategoryId(@RequestParam("pageNum") int pageNum,
                                                  @RequestParam("bookCategory") int bookCategory,
                                                  Model model){
        // 修复这里：使用新的分页方法名
        Page<BookVo> page = bookService.findBooksByCategoryIdWithPagination(bookCategory, pageNum);
        model.addAttribute("page", page);
        model.addAttribute("bookCategory", bookCategory);
        return "admin/showBooks";
    }

    /**
     * 返回管理员查询书籍结果页
     * @param bookName 书名（可选）
     * @param bookCategory 分类ID（可选）
     * @return
     */
    @RequestMapping("/adminFindBookByName")
    public String adminFindBooksResultPage(@RequestParam(value = "bookName", required = false) String bookName,
                                           @RequestParam(value = "bookCategory", required = false) Integer bookCategory,
                                           Model model){
        List<BookVo> bookVos;

        if (bookCategory != null && bookCategory > 0) {
            // 按分类查询
            bookVos = bookService.findBooksByCategoryId(bookCategory);
        } else if (bookName != null && !bookName.trim().isEmpty()) {
            // 按书名查询
            bookVos = bookService.findBooksByBookName(bookName);
        } else {
            // 默认查询所有书籍
            bookVos = bookService.findAllBooks();
        }

        model.addAttribute("bookList", bookVos);
        return "admin/showBooks";
    }

    /**
     * 管理员查看所有书籍
     * @param model
     * @return
     */
    @RequestMapping("/adminShowAllBooks")
    public String adminShowAllBooks(Model model){
        List<BookVo> bookVos = bookService.findAllBooks();
        model.addAttribute("bookList", bookVos);
        return "admin/showBooks";
    }

    /**
     * 返回用户查询书籍结果页
     * @param bookName 书名（可选）
     * @param bookCategory 分类ID（可选）
     * @return
     */
    @RequestMapping("/findBookByName")
    public String findBooksResultPage(@RequestParam(value = "bookName", required = false) String bookName,
                                      @RequestParam(value = "bookCategory", required = false) Integer bookCategory,
                                      Model model){
        List<BookVo> bookVos;

        if (bookCategory != null && bookCategory > 0) {
            // 按分类查询
            bookVos = bookService.findBooksByCategoryId(bookCategory);
        } else if (bookName != null && !bookName.trim().isEmpty()) {
            // 按书名查询
            bookVos = bookService.findBooksByBookName(bookName);
        } else {
            // 默认查询所有书籍
            bookVos = bookService.findAllBooks();
        }

        model.addAttribute("bookList", bookVos);
        return "user/findBook";
    }

    /**
     * 按分类查询书籍
     * @param bookCategory 分类ID
     * @return
     */
    @RequestMapping("/findBooksByCategory")
    @ResponseBody
    public List<BookVo> findBooksByCategory(@RequestParam("bookCategory") Integer bookCategory) {
        return bookService.findBooksByCategoryId(bookCategory);
    }

    /**
     * 查询所有书籍种类
     * @return
     */
    @RequestMapping("/findAllBookCategory")
    @ResponseBody
    public List<BookCategory> findAllBookCategory(){
        return adminService.getBookCategorys();
    }

    // ==================== 新增功能：图书详情 ====================

    /**
     * 返回图书详情页面
     * @param bookId 图书ID
     * @param model Model对象
     * @return 图书详情页面
     */
    @RequestMapping("/bookDetailPage")
    public String bookDetailPage(@RequestParam("bookId") Integer bookId, Model model) {
        BookVo bookDetail = bookService.getBookDetailById(bookId);
        model.addAttribute("book", bookDetail);
        return "admin/bookDetail";
    }

    /**
     * 返回用户图书详情页面
     * @param bookId 图书ID
     * @param model Model对象
     * @return 用户图书详情页面
     */
    @RequestMapping("/userBookDetailPage")
    public String userBookDetailPage(@RequestParam("bookId") Integer bookId, Model model) {
        BookVo bookDetail = bookService.getBookDetailById(bookId);
        model.addAttribute("book", bookDetail);
        return "user/userBookDetail";
    }

    /**
     * 获取图书详情JSON数据
     * @param bookId 图书ID
     * @return 图书详情
     */
    @RequestMapping("/getBookDetail")
    @ResponseBody
    public Map<String, Object> getBookDetail(@RequestParam("bookId") Integer bookId) {
        Map<String, Object> result = new HashMap<>();

        try {
            BookVo bookDetail = bookService.getBookDetailById(bookId);

            if (bookDetail != null) {
                result.put("success", true);
                result.put("book", bookDetail);
            } else {
                result.put("success", false);
                result.put("message", "图书不存在");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取图书详情失败：" + e.getMessage());
        }

        return result;
    }

    /**
     * 检查图书是否可借
     * @param bookId 图书ID
     * @return 是否可借
     */
    @RequestMapping("/checkBookAvailability")
    @ResponseBody
    public Map<String, Object> checkBookAvailability(@RequestParam("bookId") Integer bookId) {
        Map<String, Object> result = new HashMap<>();

        try {
            boolean isAvailable = bookService.isBookAvailable(bookId);
            result.put("success", true);
            result.put("available", isAvailable);
            result.put("message", isAvailable ? "图书可借" : "图书已借出");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "检查失败：" + e.getMessage());
        }

        return result;
    }

    /**
     * 更新图书库存
     * @param bookId 图书ID
     * @param stock 新库存数量
     * @return 更新结果
     */
    @RequestMapping("/updateBookStock")
    @ResponseBody
    public Map<String, Object> updateBookStock(@RequestParam("bookId") Integer bookId,
                                               @RequestParam("stock") Integer stock) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 校验：新库存不能小于已借出数量
            BookVo currentBook = bookService.getBookDetailById(bookId);
            if (currentBook != null) {
                int currentStock = (currentBook.getStock() != null) ? currentBook.getStock() : 0;
                int borrowedCount = currentStock - currentBook.getAvailableCount();
                if (stock < borrowedCount) {
                    result.put("success", false);
                    result.put("message", "总库存不能小于已借出数量（当前已借出 " + borrowedCount + " 本）");
                    return result;
                }
            }
            boolean success = bookService.updateBookStock(bookId, stock);
            result.put("success", success);
            result.put("message", success ? "库存更新成功" : "库存更新失败");
            if (success) {
                BookVo bookVo = bookService.getBookDetailById(bookId);
                result.put("availableCount", bookVo != null ? bookVo.getAvailableCount() : 0);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "更新失败：" + e.getMessage());
        }

        return result;
    }

    /**
     * 删除图书
     * @param bookId 图书ID
     * @return 删除结果
     */
    @RequestMapping("/deleteBook")
    @ResponseBody
    public Map<String, Object> deleteBook(@RequestParam("bookId") Integer bookId) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 删除图书（管理员有权删除任何书籍）
            int deleteResult = bookService.deleteBookById(bookId);

            if (deleteResult > 0) {
                result.put("success", true);
                result.put("message", "图书删除成功");
            } else {
                result.put("success", false);
                result.put("message", "图书删除失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除失败：" + e.getMessage());
        }

        return result;
    }

    /**
     * 跳转到书籍统计页面（包含分类统计 + 每本书库存信息）
     */
    @RequestMapping("/bookStatisticsPage")
    public String bookStatisticsPage(Model model) {
        List<Map<String, Object>> categoryStats = bookService.countBooksByCategory();
        List<Map<String, Object>> booksStockInfo = bookService.getBooksStockInfo();
        model.addAttribute("categoryStats", categoryStats);
        model.addAttribute("booksStockInfo", booksStockInfo);
        return "admin/bookStatistics";
    }

    /**
     * 获取分类统计数据的API接口
     */
    @RequestMapping("/getCategoryStatistics")
    @ResponseBody
    public List<Map<String, Object>> getCategoryStatistics() {
        return bookService.countBooksByCategory();
    }

    /**
     * 获取书籍库存统计数据的API接口
     */
    @RequestMapping("/getBooksStockInfo")
    @ResponseBody
    public List<Map<String, Object>> getBooksStockInfo() {
        return bookService.getBooksStockInfo();
    }
}