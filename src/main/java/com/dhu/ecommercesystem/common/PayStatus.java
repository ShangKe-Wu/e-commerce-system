package com.dhu.ecommercesystem.common;

/**
 * @author:WuShangke
 * @create:2022/7/14-21:03
 * 订单支付状态
 */
public enum PayStatus {
    DEFAULT(-1, "支付失败"),
    PAY_ING(0, "支付中"),
    PAY_SUCCESS(1, "支付成功");

    private int payStatus;

    private String name;

    PayStatus(int payStatus, String name) {
        this.payStatus = payStatus;
        this.name = name;
    }

    public static PayStatus getPayStatusEnumByStatus(int payStatus) {
        for (PayStatus payStatus1 : PayStatus.values()) {
            if (payStatus1.getPayStatus() == payStatus) {
                return payStatus1;
            }
        }
        return DEFAULT;
    }

    public int getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(int payStatus) {
        this.payStatus = payStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
