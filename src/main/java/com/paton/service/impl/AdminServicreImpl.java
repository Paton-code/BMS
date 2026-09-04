package com.paton.service.impl;

import com.paton.domain.*;
import com.paton.mapper.AdminMapper;
import com.paton.mapper.BookCategoryMapper;
import com.paton.mapper.BookMapper;
import com.paton.service.IAdminService;
import com.paton.utils.page.FileUploadUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class AdminServicreImpl implements IAdminService {

    @Resource
    private AdminMapper adminMapper;

    @Resource
    private BookMapper bookMapper;

    @Resource
    private BookCategoryMapper bookCategoryMapper;

    @Value("${file.upload.base-dir}")
    private String uploadBaseDir;

    @Value("${file.default.avatar}")
    private String defaultAvatar;

    @Override
    public boolean adminIsExist(String name) {
        AdminExample adminExample = new AdminExample();
        AdminExample.Criteria criteria = adminExample.createCriteria();
        criteria.andAdminNameEqualTo(name);
        List<Admin> admin = adminMapper.selectByExample(adminExample);
        if (null == admin)
            return false;
        if (admin.size() < 1) {
            return false;
        }
        return true;
    }

    @Override
    public boolean adminLogin(String name, String password) {
        AdminExample adminExample = new AdminExample();
        AdminExample.Criteria criteria = adminExample.createCriteria();
        criteria.andAdminNameEqualTo(name);
        List<Admin> admin = adminMapper.selectByExample(adminExample);
        if (null == admin) {
            return false;
        }
        for (Admin a : admin) {
            // 密码加密后比对
            String encryptedPwd = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
            if (a.getAdminPwd().equals(encryptedPwd)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean addBook(Book book) {
        int n = bookMapper.insert(book);
        if (n > 0) {
            return true;
        }
        return false;
    }

    @Override
    public List<BookCategory> getBookCategorys() {
        BookCategoryExample bookCategoryExample = new BookCategoryExample();
        return bookCategoryMapper.selectByExample(bookCategoryExample);
    }

    @Override
    public boolean addBookCategory(BookCategory bookCategory) {
        // 检查编号是否重复
        BookCategory existById = bookCategoryMapper.selectByPrimaryKey(bookCategory.getCategoryId());
        if (existById != null) {
            throw new RuntimeException("PRIMARY");
        }
        // 检查名称是否重复
        BookCategoryExample example = new BookCategoryExample();
        example.createCriteria().andCategoryNameEqualTo(bookCategory.getCategoryName());
        List<BookCategory> list = bookCategoryMapper.selectByExample(example);
        if (list != null && !list.isEmpty()) {
            throw new RuntimeException("category_name");
        }

        int n = bookCategoryMapper.insert(bookCategory);
        if (n > 0) {
            return true;
        }
        return false;
    }

    // ==================== 新增功能：管理员头像和图书封面 ====================

    @Override
    public String uploadAdminAvatar(MultipartFile file, Integer adminId) throws Exception {
        if (file == null || file.isEmpty() || adminId == null) {
            throw new IllegalArgumentException("文件或管理员ID不能为空");
        }

        // 检查文件大小（限制5MB）
        if (!FileUploadUtil.checkFileSize(file, 5)) {
            throw new IllegalArgumentException("文件大小不能超过5MB");
        }

        // 上传文件
        String relativePath = FileUploadUtil.uploadFile(file, uploadBaseDir, "avatars");

        if (relativePath != null) {
            // 更新管理员头像路径
            Admin admin = adminMapper.selectByPrimaryKey(adminId);
            if (admin != null) {
                // 如果原来有头像且不是默认头像，删除旧文件
                if (admin.getAvatar() != null && !admin.getAvatar().equals(defaultAvatar)) {
                    FileUploadUtil.deleteFile(admin.getAvatar(), uploadBaseDir);
                }

                // 更新数据库
                admin.setAvatar(relativePath);
                adminMapper.updateByPrimaryKeySelective(admin);
                return relativePath;
            }
        }

        return null;
    }

    @Override
    public Admin getAdminWithAvatar(Integer adminId) {
        Admin admin = adminMapper.selectByPrimaryKey(adminId);
        if (admin != null && admin.getAvatar() == null) {
            admin.setAvatar(defaultAvatar);
        }
        return admin;
    }

    @Override
    public String uploadBookCover(MultipartFile file, Integer bookId) throws Exception {
        if (file == null || file.isEmpty() || bookId == null) {
            throw new IllegalArgumentException("文件或图书ID不能为空");
        }

        // 检查文件大小（限制5MB）
        if (!FileUploadUtil.checkFileSize(file, 5)) {
            throw new IllegalArgumentException("文件大小不能超过5MB");
        }

        // 上传文件
        String relativePath = FileUploadUtil.uploadFile(file, uploadBaseDir, "book-covers");

        if (relativePath != null) {
            // 更新图书封面路径
            Book book = bookMapper.selectByPrimaryKey(bookId);
            if (book != null) {
                // 如果原来有封面，删除旧文件
                if (book.getCoverImage() != null) {
                    FileUploadUtil.deleteFile(book.getCoverImage(), uploadBaseDir);
                }

                // 更新数据库
                book.setCoverImage(relativePath);
                bookMapper.updateByPrimaryKeySelective(book);
                return relativePath;
            }
        }

        return null;
    }

    @Override
    public boolean addBookWithCover(Book book, MultipartFile coverFile) throws Exception {
        if (book == null) {
            return false;
        }

        // 插入图书记录
        int n = bookMapper.insertSelective(book);
        if (n > 0) {
            // 获取刚插入的图书ID - 需要重新查询获取
            BookExample example = new BookExample();
            example.createCriteria().andBookNameEqualTo(book.getBookName())
                    .andBookAuthorEqualTo(book.getBookAuthor())
                    .andBookPublishEqualTo(book.getBookPublish());
            List<Book> books = bookMapper.selectByExample(example);

            if (books == null || books.isEmpty()) {
                return false;
            }

            Integer bookId = books.get(0).getBookId();

            // 如果有封面文件，上传封面
            if (coverFile != null && !coverFile.isEmpty()) {
                try {
                    String coverPath = uploadBookCover(coverFile, bookId);
                    if (coverPath != null) {
                        // 更新图书封面路径
                        Book updateBook = new Book();
                        updateBook.setBookId(bookId);
                        updateBook.setCoverImage(coverPath);
                        bookMapper.updateByPrimaryKeySelective(updateBook);
                    }
                } catch (Exception e) {
                    // 封面上传失败不影响图书添加
                    System.err.println("封面上传失败：" + e.getMessage());
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean updateAdminAvatar(Integer adminId, String avatarPath) {
        if (adminId == null || avatarPath == null) {
            return false;
        }

        Admin admin = new Admin();
        admin.setAdminId(adminId);
        admin.setAvatar(avatarPath);

        return adminMapper.updateByPrimaryKeySelective(admin) > 0;
    }
}