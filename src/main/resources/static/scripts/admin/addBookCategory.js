// 事件绑定已移至 admin_base.js 全局委托处理

//表单验证
function validateForm() {
    return  $("#addBookCategoryForm").validate({
        rules:{
            categoryId:{
                required:true,
                digits:true
            },
            categoryName:{
                required:true
            }
        },
        messages:{
            categoryId:{
                required:"请输入类别编号",
                digits:"请输入整数"
            },
            categoryName:{
                required:"请输入图书类别"
            }
        }
    }) ;
}

//ajax添加种类
function addBookCategory() {
    $.ajax({
        async : false,
        type : "post",
        url : "/addBookCategory",
        dataType : "text",
        data: $("#addBookCategoryForm").serialize(),
        success: function (data) {
            var result = data.toString();
            if(result == "true"){
                alert("添加成功！");
                // 刷新表格，自动显示新增的类别
                var pageNum = $('#lab1').html();
                pageNum = pageNum ? $.trim(pageNum) : '1';
                if (typeof loadCategoryPage === 'function') {
                    loadCategoryPage('/addCategoryPage?pageNum=' + pageNum);
                }
            }else if(result == "duplicate_id"){
                alert("添加失败！该类别编号已存在，请更换编号后重试。");
            }else if(result == "duplicate_name"){
                alert("添加失败！该类别名称已存在，请更换名称后重试。");
            }else{
                alert("添加失败！请检查输入信息后重试。");
            }
        },
        error:function () {
            alert("添加失败！服务器异常，请稍后重试。");
        }
    });
};

//ajax删除种类
function deleteBookCategoryById(bookCategoryId, $row) {
    $.ajax({
        async : false,
        type : "post",
        url : "/deleteCategory",
        dataType : "text",
        data: {bookCategoryId:bookCategoryId},
        success: function (data) {
            if(data.toString()=="true"){
                alert("删除成功！");
                if ($row && $row.length) {
                    $row.remove();
                }
            }else{
                alert("删除失败！该类别下可能还有书籍，请先删除或移走相关书籍。");
            }
        },
        error:function () {
            alert("删除失败！服务器异常，请稍后重试。");
        }
    });
}