// 头像上传JavaScript
$(document).ready(function() {
    // 初始化头像预览
    initAvatarPreview();

    // 绑定表单提交事件
    $('#uploadAvatarForm').submit(function(e) {
        e.preventDefault();
        uploadAvatar();
    });

    // 加载当前用户头像
    loadCurrentAvatar();
});

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
                $('#avatarPreview').attr('src', e.target.result);
                $('#avatarPreview').show();
            }
            reader.readAsDataURL(file);
        }
    });
}

// 加载当前用户头像
function loadCurrentAvatar() {
    $.ajax({
        url: '/getUserAvatar',
        type: 'GET',
        success: function(response) {
            if (response.success && response.avatar) {
                $('#avatarPreview').attr('src', response.avatar);
                $('#avatarPreview').show();
            }
        },
        error: function() {
            // 使用默认头像
            $('#avatarPreview').attr('src', '/images/default-avatar.png');
            $('#avatarPreview').show();
        }
    });
}

// 上传头像
function uploadAvatar() {
    var fileInput = $('#avatarFile')[0];
    var file = fileInput.files[0];

    if (!file) {
        showMessage('请选择要上传的头像文件', 'error');
        return;
    }

    // 创建FormData对象
    var formData = new FormData();
    formData.append('avatarFile', file);

    // 显示上传进度
    var progressBar = $('.progress-bar');
    var progressText = $('.progress-text');
    progressBar.css('width', '0%');
    progressText.text('0%');

    // 禁用提交按钮
    $('#uploadBtn').prop('disabled', true).text('上传中...');

    // 发送AJAX请求
    $.ajax({
        url: '/uploadAvatar',
        type: 'POST',
        data: formData,
        contentType: false,
        processData: false,
        xhr: function() {
            var xhr = new window.XMLHttpRequest();
            xhr.upload.addEventListener('progress', function(e) {
                if (e.lengthComputable) {
                    var percentComplete = (e.loaded / e.total) * 100;
                    progressBar.css('width', percentComplete + '%');
                    progressText.text(Math.round(percentComplete) + '%');
                }
            }, false);
            return xhr;
        },
        success: function(response) {
            if (response.success) {
                showMessage('头像上传成功', 'success');
                // 3秒后返回用户信息页面
                setTimeout(function() {
                    window.location.href = '/userMessagePage';
                }, 3000);
            } else {
                showMessage('上传失败：' + response.message, 'error');
                $('#uploadBtn').prop('disabled', false).text('上传头像');
            }
        },
        error: function(xhr, status, error) {
            showMessage('上传失败：' + (xhr.responseJSON ? xhr.responseJSON.message : error), 'error');
            $('#uploadBtn').prop('disabled', false).text('上传头像');
        }
    });
}

// 显示消息
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
    $('#uploadAvatarForm')[0].reset();
    loadCurrentAvatar();
    $('.progress-bar').css('width', '0%');
    $('.progress-text').text('0%');
    $('#uploadBtn').prop('disabled', false).text('上传头像');
}