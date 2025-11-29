$(document).ready(function() {

    // 登录表单提交 (逻辑不变，后端Controller处理了多账号类型的判断)
    $('#loginForm').on('submit', function(e) {
        e.preventDefault();
        const data = {
            username: $('input[name="username"]').val(),
            password: $('input[name="password"]').val()
        };

        $.ajax({
            url: '/api/login',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function(res) {
                if(res.success) {
                    layer.msg('登录成功', {icon: 1, time: 1000}, function() {
                        window.location.href = '/';
                    });
                } else {
                    layer.msg(res.msg, {icon: 2, anim: 6});
                }
            }
        });
    });

    // 注册表单提交 (新增字段收集)
    $('#registerForm').on('submit', function(e) {
        e.preventDefault();
        const data = {
            username: $('input[name="username"]').val(),
            nickname: $('input[name="nickname"]').val(),
            phone: $('input[name="phone"]').val(), // 新增
            email: $('input[name="email"]').val(), // 新增
            password: $('input[name="password"]').val()
        };

        $.ajax({
            url: '/api/register',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function(res) {
                if(res.success) {
                    layer.alert(res.msg, {icon: 1}, function() {
                        window.location.href = '/login';
                    });
                } else {
                    layer.msg(res.msg, {icon: 2, anim: 6});
                }
            }
        });
    });
});