package com.paton.service.impl;

import com.paton.domain.Book;
import com.paton.domain.BookExample;
import com.paton.domain.BorrowingBooks;
import com.paton.domain.BorrowingBooksExample;
import com.paton.domain.Vo.BookVo;
import com.paton.mapper.BookMapper;
import com.paton.mapper.BorrowingBooksMapper;
import com.paton.service.IBookService;
import com.paton.utils.page.Page;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class BookServiceImpl implements IBookService {

    @Resource
    private BookMapper bookMapper;
    @Resource
    private BorrowingBooksMapper borrowingBooksMapper;

    @Override
    public List<BookVo> findBooksByCategoryId(Integer categoryId) {
        List<Book> books = bookMapper.selectByCategoryId(categoryId);
        return convertToBookVoList(books);
    }

    @Override
    public List<BookVo> findBooksByBookName(String bookName) {
        BookExample bookExample = new BookExample();
        BookExample.Criteria criteria = bookExample.createCriteria();
        criteria.andBookNameLike("%" + bookName + "%");
        List<Book> books = bookMapper.selectByExample(bookExample);
        return convertToBookVoList(books);
    }

    @Override
    public List<BookVo> findAllBooks() {
        BookExample bookExample = new BookExample();
        List<Book> books = bookMapper.selectByExample(bookExample);
        return convertToBookVoList(books);
    }

    /**
     * 按分类ID分页查询书籍
     * @param categoryId 分类ID
     * @param pageNum 页码
     * @return 分页结果
     */
    public Page<BookVo> findBooksByCategoryIdWithPagination(int categoryId, int pageNum) {
        List<Book> books = bookMapper.selectByCategoryIdWithPagination(categoryId, (pageNum - 1) * 10, 10);
        List<BookVo> bookVos = new LinkedList<>();
        Page<BookVo> page = new Page<>();

        if (null == books) {
            page.setPageNum(1);
            page.setPageCount(1);
            return page;
        }

        for (Book b : books) {
            BookVo bookVo = convertToBookVo(b);
            bookVos.add(bookVo);
        }

        page.setList(bookVos);
        page.setPageNum(pageNum);
        page.setPageSize(10);

        int bookCount = bookMapper.selectBookCountByCategoryId(categoryId);
        int pageCount = 0;
        pageCount = bookCount / 10;
        if (bookCount % 10 != 0) {
            pageCount++;
        }
        page.setPageCount(pageCount);
        if (bookCount == 0) {
            page.setPageCount(1);
        }

        return page;
    }

    @Override
    public BookVo getBookDetailById(Integer bookId) {
        if (bookId == null) {
            return null;
        }

        Book book = bookMapper.selectByPrimaryKey(bookId);
        if (book == null) {
            return null;
        }

        BookVo bookVo = convertToBookVo(book);

        // 设置详细信息
        bookVo.setBookIntroduction(book.getBookIntroduction());
        bookVo.setDescription(book.getDescription());
        bookVo.setBookPrice(book.getBookPrice());

        return bookVo;
    }

    @Override
    public boolean isBookAvailable(Integer bookId) {
        if (bookId == null) {
            return false;
        }

        BorrowingBooksExample example = new BorrowingBooksExample();
        BorrowingBooksExample.Criteria criteria = example.createCriteria();
        criteria.andBookIdEqualTo(bookId);
        criteria.andStatusEqualTo((byte) 1);

        long borrowingCount = borrowingBooksMapper.countByExample(example);
        return borrowingCount == 0;
    }

    @Override
    public int deleteBookById(Integer bookId) {
        if (bookId == null) {
            return 0;
        }
        return bookMapper.deleteByPrimaryKey(bookId);
    }

    @Override
    public List<Map<String, Object>> countBooksByCategory() {
        List<Map<String, Object>> result = new ArrayList<>();

        // 获取所有分类
        List<Map<String, Object>> categories = bookMapper.selectAllCategories();

        for (Map<String, Object> category : categories) {
            Integer categoryId = (Integer) category.get("categoryId");
            String categoryName = (String) category.get("categoryName");

            // 统计该分类下的图书数量
            int bookCount = bookMapper.selectBookCountByCategoryId(categoryId);

            Map<String, Object> categoryStats = new HashMap<>();
            categoryStats.put("categoryId", categoryId);
            categoryStats.put("categoryName", categoryName);
            categoryStats.put("bookCount", bookCount);

            result.add(categoryStats);
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> getBooksStockInfo() {
        return bookMapper.selectAllBooksStockInfo();
    }

    // ==================== 辅助方法 ====================

    /**
     * 将Book列表转换为BookVo列表
     * @param books Book列表
     * @return BookVo列表
     */
    private List<BookVo> convertToBookVoList(List<Book> books) {
        List<BookVo> bookVos = new ArrayList<>();
        if (books != null) {
            for (Book book : books) {
                BookVo bookVo = convertToBookVo(book);
                bookVos.add(bookVo);
            }
        }
        return bookVos;
    }

    /**
     * 将Book转换为BookVo
     */
    private BookVo convertToBookVo(Book book) {
        BookVo bookVo = new BookVo();
        bookVo.setBookId(book.getBookId());
        bookVo.setBookName(book.getBookName());
        bookVo.setBookAuthor(book.getBookAuthor());
        bookVo.setBookPublish(book.getBookPublish());
        bookVo.setBookPrice(book.getBookPrice());
        bookVo.setBookIntroduction(book.getBookIntroduction());
        bookVo.setCoverImage(book.getCoverImage());
        bookVo.setDescription(book.getDescription());
        bookVo.setStock(book.getStock());

        // 设置是否可借 + 剩余数量
        BorrowingBooksExample borrowingBooksExample = new BorrowingBooksExample();
        BorrowingBooksExample.Criteria criteria1 = borrowingBooksExample.createCriteria();
        criteria1.andBookIdEqualTo(book.getBookId());
        List<BorrowingBooks> borrowingBooks = borrowingBooksMapper.selectByExample(borrowingBooksExample);

        int borrowedCount = (borrowingBooks != null) ? borrowingBooks.size() : 0;
        int stockCount = (book.getStock() != null) ? book.getStock() : 1;
        int available = stockCount - borrowedCount;
        bookVo.setAvailableCount(available > 0 ? available : 0);

        if (available > 0) {
            bookVo.setIsExist("可借");
        } else {
            bookVo.setIsExist("不可借");
        }

        return bookVo;
    }

    @Override
    public boolean updateBookStock(Integer bookId, Integer stock) {
        if (bookId == null || stock == null || stock < 0) {
            return false;
        }
        Book book = new Book();
        book.setBookId(bookId);
        book.setStock(stock);
        return bookMapper.updateByPrimaryKeySelective(book) > 0;
    }
}