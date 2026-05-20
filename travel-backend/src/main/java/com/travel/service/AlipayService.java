package com.travel.service;

public interface AlipayService {

    /**
     * 创建支付宝支付页面（PC网页支付）
     * @param orderNo 订单号
     * @param totalAmount 支付金额
     * @param subject 订单标题
     * @return 支付宝支付表单HTML
     */
    String createPayForm(String orderNo, String totalAmount, String subject);

    /**
     * 验证支付宝异步通知签名
     * @param params 通知参数
     * @return 是否验签通过
     */
    boolean verifyNotify(java.util.Map<String, String> params);
}
