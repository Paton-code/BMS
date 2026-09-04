package com.paton.controller;

import com.paton.domain.Vo.BorrowingBooksVo;
import com.paton.service.IQueryService;
import com.paton.utils.page.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;

@Controller
public class BorrowingController {

    @Resource
    private IQueryService queryService;

    /**
     * 返回所有用户借书记录页面
     * @return
     */
    @RequestMapping("/allBorrowBooksRecordPage")
    public String allBorrowingBooksRecordPage(Model model, @RequestParam("pageNum") int pageNum){
        Page<BorrowingBooksVo> page = queryService.queryBorrowingRecords(null, "admin", pageNum);
        model.addAttribute("page", page);
        return "admin/allBorrowingBooksRecord";
    }
}