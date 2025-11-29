// auth.js - 处理登录和注册
$(document).ready(function() {

    // 登录表单提交
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

    // 注册表单提交
    $('#registerForm').on('submit', function(e) {
        e.preventDefault();
        const data = {
            username: $('input[name="username"]').val(),
            password: $('input[name="password"]').val(),
            nickname: $('input[name="nickname"]').val()
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