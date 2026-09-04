$(document).ready(function () {

    $("#btn_addUser").click(function (e) {
        e.preventDefault(); // 防止表单默认提交

        if(validateAddUserForm().form()){
            addUser();
        }
    });
});

// 表单验证
function validateAddUserForm() {
    return $("#addUserForm").validate({
        rules: {
            userName: {
                required: true,
                minlength: 3,
                maxlength: 20
            },
            userPwd: {
                required: true,
                minlength: 6,
                maxlength: 20
            },
            userEmail: {
                required: true,
                email: true
            }
        },
        messages: {
            userName: {
                required: "请输入用户名",
                minlength: "用户名至少3个字符",
                maxlength: "用户名最多20个字符"
            },
            userPwd: {
                required: "请输入密码",
                minlength: "密码至少6个字符",
                maxlength: "密码最多20个字符"
            },
            userEmail: {
                required: "请输入邮箱",
                email: "请输入有效的邮箱地址"
            }
        }
    });
}

//添加用户的ajax方法
function addUser() {
    $.ajax({
        async : false,
        type : 'post',
        url : '/addUser',
        dataType : 'text',
        data : $('#addUserForm').serialize(),
        success : function(data) {
            var result = data.toString();
            if (result === "true") {
                alert("添加成功！");
                // 清空表单
                $('#addUserForm')[0].reset();
            } else if (result === "duplicate_name") {
                alert("添加失败！该用户名已存在，请更换后重试。");
            } else if (result === "duplicate_email") {
                alert("添加失败！该邮箱已被注册，请更换后重试。");
            } else {
                alert("添加失败！请检查输入信息后重试。");
            }
        },
        error : function() {
            alert("添加失败！服务器异常，请稍后重试。");
        }
    });
}