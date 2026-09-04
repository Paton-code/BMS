package com.paton;

import com.paton.domain.BorrowingBooks;
import com.paton.mapper.BorrowingBooksMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BorrowingBooksMapperTest {

    @Resource
    private BorrowingBooksMapper borrowingBooksMapper;

    @Test
    public void testSelectByPage(){

        List<BorrowingBooks> list= (List<BorrowingBooks>) borrowingBooksMapper.selectByPageNumAndPageSize(1,2,1);
        if(null!=list){
           for(BorrowingBooks b:list){
               System.out.println(b.getId());
           }
        }else{
            System.out.println("null");
        }
    }

    @Test
    public void testSelectAllRecordCount(){
        int count=borrowingBooksMapper.selectAllRecordCount(1);
        System.out.println(count);
    }

    @Test
    public void testSelectAllByPageNum(){
        List<BorrowingBooks> list=borrowingBooksMapper.selectAllByPage(0,5);
        int count=borrowingBooksMapper.selectAll();
        System.out.println("count: "+count);
        if(null!=list){
            for(BorrowingBooks b:list){
                System.out.println(b.getId()+" "+b.getBookId());
            }
        }
    }
}
