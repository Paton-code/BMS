package com.paton.service;

import com.paton.domain.Vo.BorrowingBooksVo;
import com.paton.utils.page.Page;

import javax.servlet.http.HttpServletRequest;

public interface IBorrowingBooksRecordService {

    //查询用户的借书记录
    public Page<BorrowingBooksVo> userSelectByPageNum(int pageNum, HttpServletRequest request);

    //分页查询所有用户借书记录
    public Page<BorrowingBooksVo> selectAllByPage(int pageNum);
}
