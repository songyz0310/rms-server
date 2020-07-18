package org.geekpower.common;

public enum BaseError {
    UNKNOWN_ERROR(30001001), // 未知错误
    PARAM_IS_NULL(20001002), // 参数为空
    PARAM_MISSED(20001003), // 缺少必要参数
    PARAM_FORMAT_WRONG(20001004), // 参数格式化异常
    PARAM_VALUE_INVALID(20001005), // 参数值非法
    OBJECT_STATUS_WRONG(20001006), // 实体状态异常
    API_NOT_FOUND(20001007), // 接口没有找到
    DATA_ENCODE_FAILED(20001008), // 数据编码异常
    DATA_DECODE_FAILED(20001009), // 数据解码异常
    SYS_CONFIG_WRONG(20001010), // 系统配置错误
    OBJ_CREATE_FAILED(30001013), // 实体创建异常
    SET_VALUE_FAILED(30001014), // 实体字段设置值异常
    GENERATE_RSA_KEY_FAILED(30001015), // 生成RSA Kye 异常
    FILE_NOTFOUND(20003001), // 文件没找到异常
    FILE_CREATE_FAILED(20003002), // 文件创建异常
    FILE_LOAD_FAILED(20003003), // 文件加载异常
    FILE_READ_FAILED(30003004), // 文件读取异常
    FILE_WRITE_FAILED(30003005), // 文件写入异常
    FILE_STORAGE_FULL(30003006), // 文件存储已满
    NET_CONNECTION_FAILED(30004001), // 网络连接失败
    NET_CONNECTION_CLOSED(20004002), // 网络连接关闭
    NET_CONNECTION_TIMEOUT(20004003), // 网络连接超时
    NET_READ_TIMEOUT(20004004), // 网络读取超时
    NET_READ_FAILED(30004005), // 网络读取失败
    NET_WRITE_FAILED(30004006), // 网络写入失败
    NET_WRONG_URL(20004007), // 网络地址错误
    CACHE_NOT_FOUND(10005001), // 缓存没找到
    CACHE_READ_FAILED(30005002), // 缓存读取失败
    CACHE_WRITE_FAILED(30005003), // 缓存写入失败
    CACHE_INIT_FAILED(30005004), // 缓存初始化失败
    MQ_CONNECT_FAILED(30006001), // MQ消息连接异常
    MQ_PUBLISH_FAILED(30006002), // MQ消息发布异常
    MQ_DISPATCH_FAILED(30006003), // MQ消息派发异常
    MQ_WRONG_PROVIDER(20006004), // MQ提供者异常
    SEC_AUTH_FAILED(10007001), // 鉴权异常
    SEC_NO_ACCOUNT(10007002), // 没有此账号
    SEC_WRONG_PWD(10007003), // 此账号密码错误
    SEC_NO_PERMISSION(20007004), // 此账号没有权限
    SEC_ENCRYPT_FAILED(30007005), // 加密异常
    SEC_DECRYPT_FAILED(30007006), // 解密异常
    SEC_ACCOUNT_EXPIRED(30007007), // 账号已过期
    SEC_REPEAT_ACCOUNT(30007008), // 账号已经被占用
    SEC_REPEAT_EMAIL(30007008), // 邮箱已经被占用
    SEC_ACTIVATE_TIMEOUT(30007009), // 激活链接超时
    SEC_NEED_LOGIN(30007008), // 需要登录

    APPLY_NOT_FOUND(40001001), // 申请不存在
    GOODS_NOT_FOUND(40001002), // 商品不存在
    GOODS_CAN_NOT_DEPLOY(40001003), // 商品不能申请发布
    APPLY_CAN_NOT_OPERAT(40001004),// 申请不能操作

    ;

    private final int code;

    private BaseError(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return this.name();
    }

}
