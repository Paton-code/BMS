// 注册页面JavaScript
$(document).ready(function() {
    // 初始化表单验证
    initFormValidation();

    // 绑定事件
    bindEvents();

    // 头像预览
    initAvatarPreview();
});

// 初始化表单验证
function initFormValidation() {
    // 用户名实时验证
    $('#userName').on('blur', function() {
        validateUsername($(this).val());
    });

    // 密码实时验证
    $('#userPwd').on('input', function() {
        validatePassword($(this).val());
    });

    // 确认密码实时验证
    $('#confirmPwd').on('input', function() {
        validateConfirmPassword($(this).val());
    });

    // 邮箱实时验证
    $('#userEmail').on('blur', function() {
        validateEmail($(this).val());
    });
}

// 绑定事件
function bindEvents() {
    // 表单提交
    $('#registerForm').submit(function(e) {
        e.preventDefault();
        submitRegisterForm();
    });

    // 回车键提交
    $(document).keypress(function(e) {
        if (e.which == 13) {
            $('#registerForm').submit();
        }
    });
}

// 初始化头像预览
function initAvatarPreview() {
    $('#avatarFile').change(function(e) {
        var file = e.target.files[0];
        if (file) {
            // 检查文件大小（5MB限制）
            if (file.size > 5 * 1024 * 1024) {
                showMessage('文件大小不能超过5MB', 'error');
                $(this).val('');
                return;
            }

            // 检查文件类型
            var validTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif'];
            if (validTypes.indexOf(file.type) === -1) {
                showMessage('只支持JPG、PNG、GIF格式的图片', 'error');
                $(this).val('');
                return;
            }

            // 预览图片
            var reader = new FileReader();
            reader.onload = function(e) {
                $('#previewImage').attr('src', e.target.result);
            }
            reader.readAsDataURL(file);
        }
    });
}

// 验证用户名
function validateUsername(username) {
    var hint = $('#usernameHint');

    if (!username) {
        showHint(hint, '请输入用户名', 'error');
        return false;
    }

    if (username.length < 2 || username.length > 20) {
        showHint(hint, '用户名长度应为2-20个字符', 'error');
        return false;
    }

    // 检查用户名是否已存在
    $.ajax({
        url: '/checkUsername',
        type: 'GET',
        data: { username: username },
        success: function(response) {
            if (response.exists) {
                showHint(hint, '用户名已存在', 'error');
            } else {
                showHint(hint, '用户名可用', 'success');
            }
        },
        error: function() {
            showHint(hint, '验证失败，请稍后重试', 'error');
        }
    });

    return true;
}

// 验证密码
function validatePassword(password) {
    var hint = $('#passwordHint');

    if (!password) {
        showHint(hint, '请输入密码', 'error');
        return false;
    }

    if (password.length < 6 || password.length > 20) {
        showHint(hint, '密码长度应为6-20个字符', 'error');
        return false;
    }

    // 密码强度检查
    var strength = checkPasswordStrength(password);
    showHint(hint, strength.message, strength.type);

    return true;
}

// 验证确认密码
function validateConfirmPassword(confirmPwd) {
    var hint = $('#confirmPwdHint');
    var password = $('#userPwd').val();

    if (!confirmPwd) {
        showHint(hint, '请确认密码', 'error');
        return false;
    }

    if (confirmPwd !== password) {
        showHint(hint, '两次输入的密码不一致', 'error');
        return false;
    }

    showHint(hint, '密码一致', 'success');
    return true;
}

// 验证邮箱
function validateEmail(email) {
    var hint = $('#emailHint');

    if (!email) {
        showHint(hint, '请输入邮箱', 'error');
        return false;
    }

    var emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
        showHint(hint, '邮箱格式不正确', 'error');
        return false;
    }

    // 检查邮箱是否已存在
    $.ajax({
        url: '/checkEmail',
        type: 'GET',
        data: { email: email },
        success: function(response) {
            if (response.exists) {
                showHint(hint, '邮箱已注册', 'error');
            } else {
                showHint(hint, '邮箱可用', 'success');
            }
        },
        error: function() {
            showHint(hint, '验证失败，请稍后重试', 'error');
        }
    });

    return true;
}

// 检查密码强度
function checkPasswordStrength(password) {
    var strength = {
        level: 0,
        message: '',
        type: 'error'
    };

    // 长度检查
    if (password.length >= 6) strength.level++;

    // 包含小写字母
    if (/[a-z]/.test(password)) strength.level++;

    // 包含大写字母
    if (/[A-Z]/.test(password)) strength.level++;

    // 包含数字
    if (/[0-9]/.test(password)) strength.level++;

    // 包含特殊字符
    if (/[^a-zA-Z0-9]/.test(password)) strength.level++;

    switch(strength.level) {
        case 1:
        case 2:
            strength.message = '密码强度：弱';
            strength.type = 'error';
            break;
        case 3:
        case 4:
            strength.message = '密码强度：中等';
            strength.type = 'warning';
            break;
        case 5:
            strength.message = '密码强度：强';
            strength.type = 'success';
            break;
        default:
            strength.message = '密码强度：非常弱';
            strength.type = 'error';
    }

    return strength;
}

// 提交注册表单
function submitRegisterForm() {
    // 验证所有字段
    if (!validateAllFields()) {
        showMessage('请填写正确的信息', 'error');
        return;
    }

    // 检查是否同意协议
    if (!$('#agreeTerms').is(':checked')) {
        showMessage('请同意用户协议', 'error');
        return;
    }

    // 收集表单数据
    var formData = new FormData();
    formData.append('userName', $('#userName').val());
    formData.append('userPwd', $('#userPwd').val());
    formData.append('confirmPwd', $('#confirmPwd').val());
    formData.append('userEmail', $('#userEmail').val());

    // 获取头像文件
    var avatarFile = $('#avatarFile')[0].files[0];
    if (avatarFile) {
        formData.append('avatarFile', avatarFile);
    }

    // 禁用提交按钮
    $('.btn-register').prop('disabled', true).text('注册中...');

    // 发送注册请求
    $.ajax({
        url: '/register',
        type: 'POST',
        processData: false,
        contentType: false,
        data: formData,
        success: function(response) {
            if (response.success) {
                showMessage(response.message, 'success');
                // 3秒后跳转到登录页面
                setTimeout(function() {
                    window.location.href = '/';
                }, 3000);
            } else {
                showMessage(response.message, 'error');
                $('.btn-register').prop('disabled', false).text('注册');
            }
        },
        error: function(xhr, status, error) {
            showMessage('注册失败：' + (xhr.responseJSON ? xhr.responseJSON.message : error), 'error');
            $('.btn-register').prop('disabled', false).text('注册');
        }
    });
}

// 验证所有字段
function validateAllFields() {
    return validateUsername($('#userName').val()) &&
        validatePassword($('#userPwd').val()) &&
        validateConfirmPassword($('#confirmPwd').val()) &&
        validateEmail($('#userEmail').val());
}

// 显示提示信息
function showHint(element, message, type) {
    element.removeClass('success error');
    element.addClass(type);
    element.text(message);
}

// 显示全局消息
function showMessage(message, type) {
    // 移除现有的消息框
    $('.message-box').remove();

    // 创建消息框
    var messageBox = $('<div class="message-box"></div>');
    messageBox.addClass(type);
    messageBox.text(message);

    // 添加到页面
    $('body').append(messageBox);

    // 显示消息框
    messageBox.fadeIn();

    // 3秒后自动消失
    setTimeout(function() {
        messageBox.fadeOut(function() {
            $(this).remove();
        });
    }, 3000);
}

// 重置表单
function resetForm() {
    $('#registerForm')[0].reset();
    $('#previewImage').attr('src', '/images/default-avatar.png');
    $('.hint').text('').removeClass('success error');
    showMessage('表单已重置', 'info');
}

// 添加消息框样式
var style = document.createElement('style');
style.innerHTML = `
    .message-box {
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 15px 20px;
        border-radius: 5px;
        color: white;
        font-weight: bold;
        z-index: 9999;
        box-shadow: 0 5px 15px rgba(0,0,0,0.2);
        display: none;
        animation: slideIn 0.3s ease-out;
    }
    
    .message-box.success {
        background: #2ed573;
    }
    
    .message-box.error {
        background: #ff4757;
    }
    
    .message-box.info {
        background: #1e90ff;
    }
    
    @keyframes slideIn {
        from {
            transform: translateX(100%);
            opacity: 0;
        }
        to {
            transform: translateX(0);
            opacity: 1;
        }
    }
    
    @keyframes slideOut {
        from {
            transform: translateX(0);
            opacity: 1;
        }
        to {
            transform: translateX(100%);
            opacity: 0;
        }
    }
`;
document.head.appendChild(style);
