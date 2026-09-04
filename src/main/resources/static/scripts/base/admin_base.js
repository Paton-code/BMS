// 页面URL映射表
var pageUrlMap = {
    'addBook': '/addBookPage',
    'addCategory': '/addCategoryPage?pageNum=1',
    'showBooks': '/showBooksPage',
    'showAllBooks': '/adminShowAllBooks',
    'bookStatistics': '/bookStatisticsPage',
    'showUsers': '/showUsersPage?pageNum=1',
    'addUser': '/addUserPage',
    'allBorrowRecords': '/allBorrowingBooksRecordPage?pageNum=1',
    'operationLog': '/operationLogByType?pageNum=1',
    'adminUserFavorites': '/adminUserFavoritesPage?pageNum=1'
};

// 页面专属JS文件映射表
var pageScriptMap = {
    'addCategory': '/scripts/admin/addBookCategory.js',
    'showUsers': '/scripts/admin/showUsers.js',
    'addUser': '/scripts/admin/addUser.js',
    'allBorrowRecords': '/scripts/admin/allBorrowingBooksRecord.js'
};

// 已加载的脚本缓存
var loadedScripts = {};

// 保存进入详情页之前的主内容区HTML，供"返回"按钮恢复
window._previousContent = null;

// 用于抑制 hashchange 事件的标志（防止 loadFavoritesPage / loadOperationLogPage 触发双重加载）
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

    // ========== 拦截所有"查看详情"链接，在外壳中加载（全局委托） ==========
    $(document).on('click', '.view-details-btn', function(e) {
        e.preventDefault();
        e.stopPropagation();
        var url = $(this).attr('href');
        if (!url) return;
        loadPageUrl(url, 'showBooks');
    });

    // ========== 图书详情页按钮事件委托（全局） ==========
    // "返回"按钮：恢复进入详情页之前的查询结果
    $(document).on('click', '#btnGoBack', function(e) {
        e.preventDefault();
        if (window._previousContent) {
            $('#main-content').html(window._previousContent);
            window._previousContent = null;
            // 应用详情页中修改的库存更新
            var pending = sessionStorage.getItem('pendingStockUpdates');
            if (pending) {
                var updates = JSON.parse(pending);
                for (var bookId in updates) {
                    var newStock = updates[bookId];
                    var $card = $('#bookResultArea').find('a[href*="bookId="]').filter(function () {
                        var m = $(this).attr('href').match(/bookId=(\d+)/);
                        return m && m[1] === String(bookId);
                    }).closest('.book-card');
                    if ($card.length) {
                        $card.find('.book-remain').text('剩' + newStock + '本');
                        var $status = $card.find('.book-status');
                        if (newStock <= 0) {
                            $status.text('不可借').removeClass('status-available').addClass('status-unavailable');
                        } else {
                            $status.text('可借').removeClass('status-unavailable').addClass('status-available');
                        }
                    }
                }
                sessionStorage.removeItem('pendingStockUpdates');
            }
                // 应用封面更新
                var pendingCovers = sessionStorage.getItem('pendingCoverUpdates');
                if (pendingCovers) {
                    var coverUpdates = JSON.parse(pendingCovers);
                    for (var bookId in coverUpdates) {
                        var newCover = coverUpdates[bookId];
                        var $card = $('#bookResultArea').find('a[href*="bookId="]').filter(function() {
                            var m = $(this).attr('href').match(/bookId=(\d+)/);
                            return m && m[1] === String(bookId);
                        }).closest('.book-card');
                        if ($card.length) {
                            $card.find('.book-cover').attr('src', newCover + '?t=' + Date.now());
                        }
                    }
                    sessionStorage.removeItem('pendingCoverUpdates');
                }
            highlightCurrentNav('showBooks');
            if (typeof loadBookCategories === 'function') {
                loadBookCategories();
            }
        } else {
            loadPageContent('showBooks');
        }
    });

    // "返回列表"按钮：加载全新的查询书籍页
    $(document).on('click', '#btnGoBackToList', function(e) {
        e.preventDefault();
        window._previousContent = null;
        loadPageContent('showBooks');
    });

    // "删除图书"按钮
    $(document).on('click', '#btnDeleteBook', function(e) {
        e.preventDefault();
        var bookId = $(this).attr('data-book-id');
        var bookName = $(this).attr('data-book-name');
        if (!bookId) return;
        if (confirm('确定要删除《' + bookName + '》吗？')) {
            $.ajax({
                url: '/deleteBook',
                type: 'POST',
                data: { bookId: bookId },
                success: function(response) {
                    if (response.success) {
                        alert(response.message);
                        window._previousContent = null;
                        loadPageContent('showBooks');
                    } else {
                        alert(response.message);
                    }
                },
                error: function() {
                    alert('删除失败，请稍后重试');
                }
            });
        }
    });

    // "修改封面"文件选择（全局委托）
    $(document).on('change', '#coverFileInput', function() {
        var file = this.files[0];
        if (!file) return;
        if (file.size > 5 * 1024 * 1024) { alert('文件大小不能超过5MB'); return; }

        var $status = $('#coverUploadStatus').show().text('上传中...').css('color', '#ffc107');
        var formData = new FormData();
        formData.append('coverFile', file);
        formData.append('bookId', $('#bid').val());

        $.ajax({
            url: '/uploadBookCover',
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: function(r) {
                if (r.success) {
                    $('#bookCoverImg').attr('src', r.coverPath + '?t=' + Date.now());
                    $status.text('封面更新成功').css('color', '#28a745');
                    // 缓存封面更新，返回列表时自动刷新
                    var pendingCovers = JSON.parse(sessionStorage.getItem('pendingCoverUpdates') || '{}');
                    pendingCovers[$('#bid').val()] = r.coverPath;
                    sessionStorage.setItem('pendingCoverUpdates', JSON.stringify(pendingCovers));
                } else {
                    $status.text('上传失败：' + r.message).css('color', '#dc3545');
                }
            },
            error: function() {
                $status.text('上传失败，请重试').css('color', '#dc3545');
            }
        });
    });

    // ========== 查询书籍页表单事件委托（支持"返回"后二次查询） ==========
    // 按类别查找
    $(document).on('submit', '#bookCategoryForm', function(e) {
        e.preventDefault();
        var category = $('#bookCategory').val();
        if (!category) {
            alert('请选择分类');
            return false;
        }
        if (typeof loadBookResult === 'function') {
            loadBookResult('/showBooksResultPageByCategoryId?bookCategory=' + category + '&pageNum=1');
        } else {
            window.location.href = '/showBooksResultPageByCategoryId?bookCategory=' + category + '&pageNum=1';
        }
    });

    // 按书名查找
    $(document).on('submit', '#findBookByNameForm', function(e) {
        e.preventDefault();
        var bookName = $('#bookName').val().trim();
        if (!bookName) {
            alert('请输入书名');
            return false;
        }
        if (typeof loadBookResult === 'function') {
            loadBookResult('/adminFindBookByName?bookName=' + encodeURIComponent(bookName));
        } else {
            window.location.href = '/adminFindBookByName?bookName=' + encodeURIComponent(bookName);
        }
    });

    // 收藏搜索表单提交
    $(document).on('submit', '#favoritesSearchForm', function(e) {
        e.preventDefault();
        var userName = $('#userName').val().trim();
        var bookName = $('#bookName').val().trim();
        var url = '/adminUserFavoritesPage?pageNum=1';
        if (userName) url += '&userName=' + encodeURIComponent(userName);
        if (bookName) url += '&bookName=' + encodeURIComponent(bookName);
        loadFavoritesPage(url);
    });

    // 收藏分页链接
    $(document).on('click', '#favoritesPagination a', function(e) {
        e.preventDefault();
        var href = $(this).attr('href');
        if (href && !$(this).hasClass('disabled')) {
            loadFavoritesPage(href);
        }
    });

    // 操作日志搜索表单提交
    $(document).on('submit', '#operationLogSearchForm', function(e) {
        console.log('[DEBUG] 操作日志搜索表单提交被拦截');
        e.preventDefault();
        var operationType = $('#operationType').val();
        var operationModule = $('#operationModule').val();
        console.log('[DEBUG] 搜索参数 - operationType:', operationType, 'operationModule:', operationModule);
        var url = '/operationLogByType?pageNum=1';
        if (operationType) url += '&operationType=' + encodeURIComponent(operationType);
        if (operationModule) url += '&operationModule=' + encodeURIComponent(operationModule);
        console.log('[DEBUG] 操作日志搜索URL:', url);
        loadOperationLogPage(url);
    });

    // 操作日志分页链接
    $(document).on('click', '#operationLogPagination a', function(e) {
        e.preventDefault();
        var href = $(this).attr('href');
        if (href && !$(this).hasClass('disabled')) {
            loadOperationLogPage(href);
        }
    });

    // 查看全部书籍
    $(document).on('click', '#showAllBooksBtn', function(e) {
        e.preventDefault();
        loadPageContent('showAllBooks');
    });

    // 分页链接（查询结果中的翻页）
    $(document).on('click', '#bookResultArea .pagination a', function(e) {
        e.preventDefault();
        var href = $(this).attr('href');
        if (href && !$(this).hasClass('disabled') && typeof loadBookResult === 'function') {
            loadBookResult(href);
        }
    });

    // ========== 类别管理页面事件委托 ==========
    // 添加类别表单提交
    $(document).on('submit', '#addBookCategoryForm', function(e) {
        e.preventDefault();
        if (typeof validateForm === 'function') {
            if (!validateForm().form()) return;
        }
        if (typeof addBookCategory === 'function') {
            addBookCategory();
        }
    });

    // 新增用户表单提交
    $(document).on('submit', '#addUserForm', function(e) {
        e.preventDefault();
        if (typeof validateAddUserForm === 'function') {
            if (!validateAddUserForm().form()) return;
        }
        if (typeof addUser === 'function') {
            addUser();
        }
    });

    // 删除类别按钮
    $(document).on('click', '.btn-delete-category', function(e) {
        e.preventDefault();
        var $btn = $(this);
        var bookCategoryId = $btn.val();
        if (confirm("确认删除?")) {
            if (typeof deleteBookCategoryById === 'function') {
                deleteBookCategoryById(bookCategoryId, $btn.closest('tr'));
            }
        }
    });

    // 删除用户按钮
    $(document).on('click', '.btn-delete-user', function(e) {
        e.preventDefault();
        var $btn = $(this);
        var userId = $btn.val();
        if (!confirm("确认删除该用户？")) return;
        if (typeof deleteUserById === 'function') {
            deleteUserById(userId, $btn.closest('tr'));
        }
    });

    // 类别分页（上一页/下一页）
    $(document).on('click', '#prePage, #nextPage', function(e) {
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
            loadCategoryPage(href);
        }
    });
});

/**
 * 加载用户收藏管理分页
 */
function loadFavoritesPage(url) {
    history.replaceState(null, '', '#adminUserFavorites');
    expandMenuForPage('adminUserFavorites');
    highlightCurrentNav('adminUserFavorites');
    $('#main-content').html('<div style="text-align:center;padding:60px;color:#999;">加载中...</div>');
    $.ajax({
        url: url,
        type: 'GET',
        dataType: 'html',
        success: function(html) {
            var tempDiv = document.createElement('div');
            tempDiv.innerHTML = html;
            var mainEl = tempDiv.querySelector('main.main-content');
            if (!mainEl) {
                $('#main-content').html('<div style="text-align:center;padding:60px;color:#c0392b;">加载失败</div>');
                return;
            }
            var scripts = mainEl.querySelectorAll('script');
            var scriptTexts = [];
            scripts.forEach(function(s) {
                scriptTexts.push(s.textContent.trim());
                s.remove();
            });
            var styles = tempDiv.querySelectorAll('style');
            var stylesHtml = '';
            styles.forEach(function(s) { stylesHtml += s.outerHTML; });
            $('#main-content').html(stylesHtml + mainEl.innerHTML);
            scriptTexts.forEach(function(scriptText) {
                if (scriptText) {
                    try {
                        (0, eval)(scriptText);
                    } catch (e) {
                        console.error('内联脚本执行错误:', e);
                    }
                }
            });
        },
        error: function() {
            $('#main-content').html('<div style="text-align:center;padding:60px;color:#c0392b;">加载失败</div>');
        }
    });
}

/**
 * 加载操作日志查询页面（用于分页和搜索）
 */
function loadOperationLogPage(url) {
    console.log('[DEBUG] loadOperationLogPage 被调用, URL:', url);
    history.replaceState(null, '', '#operationLog');
    expandMenuForPage('operationLog');
    highlightCurrentNav('operationLog');
    $('#main-content').html('<div style="text-align:center;padding:60px;color:#999;">加载中...</div>');
    $.ajax({
        url: url,
        type: 'GET',
        dataType: 'html',
        success: function(html) {
            console.log('[DEBUG] 操作日志 AJAX 成功, 响应长度:', html.length);
            var tempDiv = document.createElement('div');
            tempDiv.innerHTML = html;
            var mainEl = tempDiv.querySelector('main.main-content');
            if (!mainEl) {
                console.error('[DEBUG] 未找到 main.main-content');
                $('#main-content').html('<div style="text-align:center;padding:60px;color:#c0392b;">加载失败</div>');
                return;
            }
            var scripts = mainEl.querySelectorAll('script');
            var scriptTexts = [];
            scripts.forEach(function(s) {
                scriptTexts.push(s.textContent.trim());
                s.remove();
            });
            console.log('[DEBUG] 提取到内联脚本数:', scriptTexts.length);
            var styles = tempDiv.querySelectorAll('style');
            var stylesHtml = '';
            styles.forEach(function(s) { stylesHtml += s.outerHTML; });
            $('#main-content').html(stylesHtml + mainEl.innerHTML);
            scriptTexts.forEach(function(scriptText) {
                if (scriptText) {
                    try {
                        (0, eval)(scriptText);
                    } catch (e) {
                        console.error('[DEBUG] 内联脚本执行错误:', e);
                    }
                }
            });
            console.log('[DEBUG] 操作日志页面加载完成');
        },
        error: function(xhr, status, error) {
            console.error('[DEBUG] 操作日志 AJAX 失败:', status, error);
            $('#main-content').html('<div style="text-align:center;padding:60px;color:#c0392b;">加载失败</div>');
        }
    });
}

/**
 * 加载用户管理分页
 */
function loadUserPage(url) {
    $('#main-content').html('<div style="text-align:center;padding:60px;color:#999;">加载中...</div>');
    $.ajax({
        url: url,
        type: 'GET',
        dataType: 'html',
        success: function(html) {
            var tempDiv = document.createElement('div');
            tempDiv.innerHTML = html;
            var mainEl = tempDiv.querySelector('main.main-content');
            if (!mainEl) {
                $('#main-content').html('<div style="text-align:center;padding:60px;color:#c0392b;">加载失败</div>');
                return;
            }
            var scripts = mainEl.querySelectorAll('script');
            scripts.forEach(function(s) { s.remove(); });
            var styles = tempDiv.querySelectorAll('style');
            var stylesHtml = '';
            styles.forEach(function(s) { stylesHtml += s.outerHTML; });
            $('#main-content').html(stylesHtml + mainEl.innerHTML);
        },
        error: function() {
            $('#main-content').html('<div style="text-align:center;padding:60px;color:#c0392b;">加载失败</div>');
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
 * 高亮当前选中的子菜单项
 */
function highlightCurrentNav(pageId) {
    if (pageId === 'showAllBooks') {
        pageId = 'showBooks';
    }
    $('.header__nav li ul li a').removeClass('nav-active');
    $('.header__nav a[data-page="' + pageId + '"]').addClass('nav-active');
}

/**
 * 通过 URL 直接加载页面（用于"查看详情"等动态链接）
 */
function loadPageUrl(url, highlightPageId) {
    // 保存进入详情页之前的主内容区，供"返回"按钮恢复
    window._previousContent = $('#main-content').html();

    $('#main-content').html('<div style="text-align:center;padding:60px;color:#999;">加载中...</div>');
    $.ajax({
        url: url,
        type: 'GET',
        dataType: 'html',
        success: function(html) {
            var tempDiv = document.createElement('div');
            tempDiv.innerHTML = html;
            var mainEl = tempDiv.querySelector('main.main-content');
            if (!mainEl) {
                $('#main-content').html('<div style="text-align:center;padding:60px;color:#c0392b;">加载失败</div>');
                return;
            }
            var scripts = mainEl.querySelectorAll('script');
            var scriptTexts = [];
            scripts.forEach(function(s) { scriptTexts.push(s.textContent.trim()); s.remove(); });
            var styles = tempDiv.querySelectorAll('style');
            var stylesHtml = '';
            styles.forEach(function(s) { stylesHtml += s.outerHTML; });
            $('#main-content').html(stylesHtml + mainEl.innerHTML);
            scriptTexts.forEach(function(t) { if(t) { try { (0, eval)(t); } catch(e) { console.error(e); } } });
            if (highlightPageId) {
                highlightCurrentNav(highlightPageId);
            }
        },
        error: function() {
            $('#main-content').html('<div style="text-align:center;padding:60px;color:#c0392b;">加载失败</div>');
        }
    });
}

/**
 * AJAX 加载类别管理页面（用于分页）
 */
function loadCategoryPage(url) {
    $('#main-content').html('<div style="text-align:center;padding:60px;color:#999;">加载中...</div>');
    $.ajax({
        url: url,
        type: 'GET',
        dataType: 'html',
        success: function(html) {
            var tempDiv = document.createElement('div');
            tempDiv.innerHTML = html;
            var mainEl = tempDiv.querySelector('main.main-content');
            if (!mainEl) {
                $('#main-content').html('<div style="text-align:center;padding:60px;color:#c0392b;">加载失败</div>');
                return;
            }
            var scripts = mainEl.querySelectorAll('script');
            scripts.forEach(function(s) { s.remove(); });
            var styles = tempDiv.querySelectorAll('style');
            var stylesHtml = '';
            styles.forEach(function(s) { stylesHtml += s.outerHTML; });
            $('#main-content').html(stylesHtml + mainEl.innerHTML);
        },
        error: function() {
            $('#main-content').html('<div style="text-align:center;padding:60px;color:#c0392b;">加载失败</div>');
        }
    });
}