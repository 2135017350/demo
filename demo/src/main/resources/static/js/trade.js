$(document).ready(function() {
    $('.btn-buy').on('click', function() {
        const btn = $(this);
        const id = btn.data('id');
        const price = btn.data('price');

        layui.layer.confirm(`确认支付 <b>¥${price}</b> 购买吗？`, {title:'交易确认', icon:3}, function(index){
            const loadIdx = layui.layer.load(2);
            $.ajax({
                url: '/api/trade/buy',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({ itemId: id }),
                success: function(res) {
                    layui.layer.close(loadIdx);
                    layui.layer.close(index);
                    if(res.success) {
                        layui.layer.alert(res.msg, {icon: 1}, () => location.reload());
                    } else {
                        layui.layer.msg(res.msg, {icon: 2});
                    }
                }
            });
        });
    });
});