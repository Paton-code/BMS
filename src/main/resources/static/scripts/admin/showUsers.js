// 事件绑定已移至 admin_base.js 全局委托处理

//ajax删除用户
function deleteUserById(userId, $row) {
    $.ajax({
        async : false,
        type : "post",
        url : "/deleteUser",
        dataType : "text",
        data: {userId: userId},
        success: function (data) {
            if (data.toString() == "true") {
                alert("删除成功！");
                if ($row && $row.length) {
                    $row.remove();
                }
            } else {
                alert("删除失败！该用户可能还有未还书籍或存在关联数据。");
            }
        },
        error: function () {
            alert("删除失败！服务器异常，请稍后重试。");
        }
    });
}