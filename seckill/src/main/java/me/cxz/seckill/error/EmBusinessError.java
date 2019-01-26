package me.cxz.seckill.error;

public enum EmBusinessError implements CommonError {

    // 通用错误类型  10001
    PARAMETER_VALIDATION_ERROR(1001, "参数不合法"),
    UNKNOWN_ERROR(1002, "未知错误"),

    // 2000开头为用户信息相关错误定义
    USER_NOT_EXIST(2001, "用户不存在"),
    USER_LOGIN_FAIL(2002, "用户手机号或密码不正确"),
    USER_NOT_LOGIN(2003, "用户还未登录"),

    // 3000开头为订单交易信息相关错误定义
    STOCK_NOT_ENOUGH(3001, "库存不足"),
    ;

    private EmBusinessError(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    private int errorCode;
    private String errorMsg;

    @Override
    public int getErrorCode() {
        return this.errorCode;
    }

    @Override
    public String getErrorMsg() {
        return this.errorMsg;
    }

    @Override
    public CommonError setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
        return this;
    }
}
