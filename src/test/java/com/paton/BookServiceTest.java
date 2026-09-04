package com.paton;

import com.paton.domain.Book;
import com.paton.domain.Vo.BookVo;
import com.paton.mapper.BookMapper;
import com.paton.service.IBookService;
import com.paton.utils.page.Page;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BookServiceTest {

    @Resource
    private BookMapper bookMapper;
    @Resource
    private IBookService bookService;
    @Test
    public void testSelectBookByName(){
        List<BookVo> bookVoList=bookService.findBooksByBookName("平凡的世界");
        if(null!=bookVoList){
            for(BookVo bookVo:bookVoList){
                System.out.println(bookVo.getBookName()+" "+bookVo.getIsExist());
            }
        }
    }

    @Test
    public void testSelectByCategoryId(){
        // 修复：使用正确的分页方法名
        Page<BookVo> page=bookService.findBooksByCategoryIdWithPagination(1,1);
        if(null!=page){
            for(BookVo bookVo:page.getList()){
                System.out.println(bookVo.getBookName()+" "+bookVo.getIsExist());
            }
            System.out.println(page.getPageCount());
        }
    }

    @Test
    public void testSelectByCategoryAndPage(){
        // 修复：使用正确的分页方法名
        List<Book> books=bookMapper.selectByCategoryIdWithPagination(1,0,2);
        if(null!=books){
            for(Book b:books){
                System.out.println(b.getBookId()+" "+b.getBookName()+" "+b.getBookCategory());
            }
        }
    }

    @Test
    public void testFindAllBookCountByCategoryId(){
        int n=bookMapper.selectBookCountByCategoryId(1);
        System.out.println(n);
    }
}