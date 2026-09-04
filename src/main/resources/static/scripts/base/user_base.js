// 页面URL映射表
var pageUrlMap = {
    'borrowingBookRecord': '/userBorrowingBooksPage?pageNum=1',
    'borrowingBook': '/borrowingPage',
    'returnBook': '/userReturnBooksPage',
    'findBook': '/findBookPage',
    'userMessage': '/userMessagePage',
    'userFavorites': '/userFavoritesPage'
};

// 页面专属JS文件映射表
var pageScriptMap = {
    'borrowingBookRecord': '/scripts/user/borrowingBooksRecord.js',

};

// 已加载的脚本缓存
var loadedScripts = {};

// 保存进入详情页之前的主内容区HTML
window._previousContent = null;

// 用于抑制 hashchange 事件的标志
var _suppressHashChange = false;

$(document).ready(function () {

    // ========== 保存首页欢迎内容 ==========
    var _homeContent = $('#main-content').html();

    // ========== 首页导航点击 ==========
    $('#navHome').click(function (e) {
        e.preventDefault();
        $('#main-content').html(_homeContent);
        $('.header__nav li ul li a').removeClass('nav-active');
        history.replaceState(null, '', window.location.pathname);
    });

    // ========== 父级菜单展开/折叠 ==========
    $("#click_1").click(function (e) {
        if ($(e.target).closest('ul').attr('id') === 'sub_1') return;
        e.preventDefault();
        $(this).toggleClass("open");
        $("#sub_1").slideToggle(200);
    });

    $("#click_2").click(function (e) {
        if ($(e.target).closest('ul').attr('id') === 'sub_2') return;
        e.preventDefault();
        $(this).toggleClass("open");
        $("#sub_2").slideToggle(200);
    });

    $("#click_3").click(function (e) {
        if ($(e.target).closest('ul').attr('id') === 'sub_3') return;
        e.preventDefault();
        $(this).toggleClass("open");
        $("#sub_3").slideToggle(200);
    });

    $("#click_4").click(function (e) {
        if ($(e.target).closest('ul').attr('id') === 'sub_4') return;
        e.preventDefault();
        $(this).toggleClass("open");
        $("#sub_4").slideToggle(200);
    });

    // ========== 子菜单项点击 - 动态加载内容 ==========
    $(".header__nav a[data-page]").click(function (e) {
        e.preventDefault();
        e.stopPropagation();
        var pageId = $(this).attr('data-page');
        loadPageContent(pageId);
    });

    // ========== 检查URL hash，恢复页面状态 ==========
    var hash = window.location.hash;
    if (hash && hash.length > 1) {
        var pageId = hash.substring(1);
        if (pageUrlMap[pageId]) {
            expandMenuForPage(pageId);
            loadPageContent(pageId);
        }
    }

    // ========== 监听浏览器前进/后退 ==========
    $(window).on('hashchange', function () {
        if (_suppressHashChange) return;
        var hash = window.location.hash;
        if (hash && hash.length > 1) {
            var pageId = hash.substring(1);
            if (pageUrlMap[pageId]) {
                expandMenuForPage(pageId);
                loadPageContent(pageId, true);
            }
        }
    });

    // ========== 借书记录分页拦截 ==========
    $(document).on('click', '#prePage, #nextPage', function (e) {
        e.preventDefault();
        var $lab1 = $('#lab1');
        var $lab2 = $('#lab2');
        if ($lab1.length === 0 || $lab2.length === 0) return;
        var lab1 = parseInt($lab1.html().trim());
        var lab2 = parseInt($lab2.html().trim());
        var isPrePage = $(this).attr('id') === 'prePage';
        if (isPrePage && lab1 === 1) {
            alert("已经是第一页了!");
            return false;
        }
        if (!isPrePage && lab1 === lab2) {
            alert("已经是最后一页了!");
            return false;
        }
        var href = $(this).attr('href');
        if (href) {
            loadPageContentByUrl(href, 'borrowingBookRecord');
        }
    });

    // ========== 查询书籍表单提交拦截 ==========
    $(document).on('submit', '#findBookByCategoryForm', function (e) {
        e.preventDefault();
        var category = $('#bookCategory').val();
        if (!category) {
            alert('请选择分类');
            return false;
        }
        loadPageContentByUrl('/findBookByName?bookCategory=' + category, 'findBook');
    });

    $(document).on('submit', '#findBookByNameForm', function (e) {
        e.preventDefault();
        var bookName = $('#bookName').val().trim();
        if (!bookName) {
            alert('请输入书名');
            return false;
        }
        loadPageContentByUrl('/findBookByName?bookName=' + encodeURIComponent(bookName), 'findBook');
    });

    // 查看全部书籍按钮
    $(document).on('click', '[onclick="showAllBooks()"]', function (e) {
        e.preventDefault();
        loadPageContentByUrl('/findBookByName', 'findBook');
    });

    // ========== 借阅图书按钮 ==========
    $(document).on('click', '.borrow-btn', function (e) {
        e.preventDefault();
        var $btn = $(this);
        var onclick = $btn.attr('onclick');
        if (onclick && onclick.indexOf('borrowBook') !== -1) {
            var match = onclick.match(/borrowBook\((\d+)\)/);
            if (match) {
                var bookId = match[1];
                if (confirm('确定要借阅这本书吗？')) {
                    $.ajax({
                        url: '/userBorrowingBook',
                        type: 'POST',
                        data: { bookId: bookId },
                        success: function (response) {
                            if (response.success) {
                                alert('借阅成功！');
                                var $card = $btn.closest('.book-info');
                                var $remain = $card.find('.book-remain');
                                if (response.remainCount > 0) {
                                    $btn.text('借阅').prop('disabled', false);
                                    $remain.text('剩' + response.remainCount + '本');
                                } else {
                                    $btn.text('已借出').prop('disabled', true).removeAttr('onclick');
                                    $remain.text('剩0本');
                                    $card.find('.book-status')
                                        .text('不可借')
                                        .removeClass('status-available')
                                        .addClass('status-unavailable');
                                }
                            } else {
                                alert(response.message || '借阅失败，书籍可能已被借出');
                            }
                        },
                        error: function () {
                            alert('请求失败，请检查网络连接');
                        }
                    });
                }
            }
        }
    });

    // ========== 借书表单提交 ==========
    $(document).on('submit', '#borrowingBookForm', function (e) {
        e.preventDefault();
        var bookId = $('#bookId').val().trim();
        if (!bookId) {
            alert('请输入书籍编号');
            return false;
        }
        $.ajax({
            type: 'POST',
            url: '/userBorrowingBook',
            data: { bookId: bookId },
            success: function (data) {
                if (data.success) {
                    alert('借书成功!');
                    loadPageContent('borrowingBook');
                } else {
                    alert(data.message || '借书失败!');
                }
            },
            error: function () {
                alert('请求失败，请检查网络连接');
            }
        });
    });

    // ========== 还书表单提交 ==========
    $(document).on('submit', '#returnBookForm', function (e) {
        e.preventDefault();
        var bookId = $('#bookId').val().trim();
        if (!bookId) {
            alert('请输入书籍编号');
            return false;
        }
        $.ajax({
            type: 'POST',
            url: '/userReturnBook',
            data: { bookId: bookId },
            dataType: 'text',
            success: function (data) {
                if ($.trim(data) === 'true') {
                    alert('还书成功!');
                    loadPageContent('returnBook');
                } else {
                    alert('还书失败，可能未借阅此书或书籍编号错误');
                }
            },
            error: function (xhr) {
                alert('请求失败 [' + xhr.status + ']，请检查网络连接');
            }
        });
    });

    // ========== 用户收藏页面取消收藏按钮 ==========
    $(document).on('click', '.remove-btn', function (e) {
        e.preventDefault();
        var onclick = $(this).attr('onclick');
        if (onclick && onclick.indexOf('removeFavorite') !== -1) {
            var match = onclick.match(/removeFavorite\((\d+)\)/);
            if (match) {
                var favoriteId = match[1];
                if (confirm('确定要取消收藏吗？')) {
                    $.ajax({
                        url: '/removeFavorite',
                        type: 'POST',
                        data: { favoriteId: favoriteId },
                        success: function (response) {
                            if (response.success) {
                                alert('取消收藏成功');
                                loadPageContent('userFavorites');
                            } else {
                                alert('取消收藏失败：' + response.message);
                            }
                        },
                        error: function () {
                            alert('网络错误，请重试');
                        }
                    });
                }
            }
        }
    });

    // ========== 个人信息页面事件委托 ==========
    $(document).on('click', '#selectAvatarBtn', function() {
        $('#avatarFile').click();
    });
    $(document).on('change', '#avatarFile', function(e) {
        var file = e.target.files[0];
        if (!file) return;
        if (file.size > 5 * 1024 * 1024) { alert('文件大小不能超过5MB'); return; }
        var reader = new FileReader();
        reader.onload = function(ev) { $('#userAvatar').attr('src', ev.target.result); };
        reader.readAsDataURL(file);
        $('#avatarHint').text('已选择: ' + file.name).show();
    });
    $(document).on('click', '#editBtn', function() {
        $('#name, #email').removeAttr('readonly').css('background-color','#fff');
        $('#pwdField, #avatarEditGroup').show();
        $('#editGroup').hide();
        $('#saveGroup').show();
    });
    $(document).on('click', '#cancelBtn', function() {
        loadPageContent('userMessage');
    });
    $(document).on('click', '#saveBtn', function() {
        var $btn = $(this).prop('disabled', true).text('保存中...');
        var avatarFile = $('#avatarFile')[0].files[0];

        function doUpdateUser() {
            $.ajax({
                url: '/updateUser',
                type: 'POST',
                data: {
                    userName: $('#name').val(),
                    userEmail: $('#email').val(),
                    userPwd: $('#password').val()
                },
                success: function(response) {
                    if (response) {
                        alert('信息更新成功');
                        refreshNavbar();
                        loadPageContent('userMessage');
                    } else {
                        alert('信息更新失败');
                        $btn.prop('disabled', false).text('保存');
                    }
                },
                error: function() {
                    alert('更新失败，请稍后重试');
                    $btn.prop('disabled', false).text('保存');
                }
            });
        }

        if (avatarFile) {
            var fd = new FormData();
            fd.append('avatarFile', avatarFile);
            $.ajax({
                url: '/uploadAvatar',
                type: 'POST',
                data: fd,
                processData: false,
                contentType: false,
                success: function(res) {
                    if (res.success) {
                        doUpdateUser();
                    } else {
                        alert('头像上传失败: ' + (res.message || '未知错误'));
                        $btn.prop('disabled', false).text('保存');
                    }
                },
                error: function() {
                    alert('头像上传失败，请重试');
                    $btn.prop('disabled', false).text('保存');
                }
            });
        } else {
            doUpdateUser();
        }
    });
});

/**
 * 刷新导航栏头像和用户名
 */
function refreshNavbar() {
    $.ajax({
        url: '/getUserAvatar',
        type: 'GET',
        success: function(res) {
            if (res.success) {
                if (res.avatar) {
                    $('.avatar-img').attr('src', res.avatar);
                }
                if (res.userName) {
                    $('.username').text(res.userName);
                    window._userName = res.userName;
                }
            }
        }
    });
}

/**
 * 展开指定页面对应的父级菜单
 */
function expandMenuForPage(pageId) {
    var $link = $('.header__nav a[data-page="' + pageId + '"]');
    var $parentUl = $link.closest('ul');
    var $parentLi = $parentUl.parent('li');
    if ($parentLi.length > 0 && !$parentLi.hasClass('open')) {
        $parentLi.addClass('open');
        $parentUl.show();
    }
}

/**
 * 动态加载页面内容到主内容区域
 */
function loadPageContent(pageId, fromHashChange) {
    var url = pageUrlMap[pageId];
    if (!url) {
        console.error('未知页面ID: ' + pageId);
        return;
    }

    if (!fromHashChange) {
        _suppressHashChange = true;
        window.location.hash = pageId;
        _suppressHashChange = false;
    }

    expandMenuForPage(pageId);
    highlightCurrentNav(pageId);

    $('#main-content').html('<div style="text-align:center;padding:60px;color:#999;">加载中...</div>');

    $.ajax({
        url: url,
        type: 'GET',
        dataType: 'html',
        success: function (html) {
            var tempDiv = document.createElement('div');
            tempDiv.innerHTML = html;
            var mainEl = tempDiv.querySelector('main.main-content');

            if (!mainEl) {
                console.error('未找到 main.main-content 元素，URL:', url);
                $('#main-content').html(
                    '<div style="text-align:center;padding:60px;color:#c0392b;">' +
                    '页面加载失败：未找到内容区域</div>'
                );
                return;
            }

            var inlineScripts = mainEl.querySelectorAll('script');
            var scriptTexts = [];
            inlineScripts.forEach(function (s) {
                scriptTexts.push(s.textContent.trim());
                s.remove();
            });

            var mainContent = mainEl.innerHTML;

            if (!mainContent || mainContent.trim() === '') {
                $('#main-content').html(
                    '<div style="text-align:center;padding:60px;color:#c0392b;">' +
                    '页面加载失败：内容为空</div>'
                );
                return;
            }

            var styleEls = tempDiv.querySelectorAll('style');
            var stylesHtml = '';
            styleEls.forEach(function (s) {
                stylesHtml += s.outerHTML;
            });

            $('#main-content').html(stylesHtml + mainContent);

            scriptTexts.forEach(function (scriptText) {
                if (scriptText) {
                    try {
                        (0, eval)(scriptText);
                    } catch (e) {
                        console.error('内联脚本执行错误:', e);
                    }
                }
            });

            var scriptUrl = pageScriptMap[pageId];
            if (scriptUrl && !loadedScripts[scriptUrl]) {
                loadedScripts[scriptUrl] = true;
                $.getScript(scriptUrl).fail(function () {
                    console.warn('外部脚本加载失败: ' + scriptUrl);
                    loadedScripts[scriptUrl] = false;
                });
            }

            console.log('页面加载成功: ' + pageId + ' (' + url + ')');
        },
        error: function (xhr, status, error) {
            console.error('AJAX请求失败:', status, error, 'URL:', url);
            $('#main-content').html(
                '<div style="text-align:center;padding:60px;color:#c0392b;">' +
                '页面加载失败，请检查网络连接</div>'
            );
        }
    });
}

/**
 * 通过 URL 直接加载页面（用于查询结果、分页等）
 */
function loadPageContentByUrl(url, highlightPageId) {
    _suppressHashChange = true;
    window.location.hash = highlightPageId;
    _suppressHashChange = false;

    expandMenuForPage(highlightPageId);
    highlightCurrentNav(highlightPageId);

    $('#main-content').html('<div style="text-align:center;padding:60px;color:#999;">加载中...</div>');

    $.ajax({
        url: url,
        type: 'GET',
        dataType: 'html',
        success: function (html) {
            var tempDiv = document.createElement('div');
            tempDiv.innerHTML = html;
            var mainEl = tempDiv.querySelector('main.main-content');

            if (!mainEl) {
                $('#main-content').html(
                    '<div style="text-align:center;padding:60px;color:#c0392b;">加载失败</div>'
                );
                return;
            }

            var scripts = mainEl.querySelectorAll('script');
            var scriptTexts = [];
            scripts.forEach(function (s) {
                scriptTexts.push(s.textContent.trim());
                s.remove();
            });

            var styles = tempDiv.querySelectorAll('style');
            var stylesHtml = '';
            styles.forEach(function (s) { stylesHtml += s.outerHTML; });

            $('#main-content').html(stylesHtml + mainEl.innerHTML);

            scriptTexts.forEach(function (t) {
                if (t) {
                    try { (0, eval)(t); } catch (e) { console.error(e); }
                }
            });
        },
        error: function () {
            $('#main-content').html(
                '<div style="text-align:center;padding:60px;color:#c0392b;">加载失败</div>'
            );
        }
    });
}

/**
 * 高亮当前选中的子菜单项
 */
function highlightCurrentNav(pageId) {
    $('.header__nav li ul li a').removeClass('nav-active');
    $('.header__nav a[data-page="' + pageId + '"]').addClass('nav-active');
}