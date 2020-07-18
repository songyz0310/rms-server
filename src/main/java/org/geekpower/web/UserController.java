package org.geekpower.web;

import org.geekpower.common.CurrentContext;
import org.geekpower.common.PageResult;
import org.geekpower.common.ParameterValidator;
import org.geekpower.common.RpcResponse;
import org.geekpower.common.Tuple;
import org.geekpower.common.dto.UserDTO;
import org.geekpower.common.dto.UserSessionDTO;
import org.geekpower.form.PageParam;
import org.geekpower.form.UserParam;
import org.geekpower.service.IUserService;
import org.geekpower.utils.GsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private IUserService userService;

    @PostMapping("/login")
    public RpcResponse<UserSessionDTO> login(@RequestBody UserParam param) {
        if (logger.isDebugEnabled())
            logger.debug("登录参数:{}", GsonUtil.toJson(param));

        try {
            UserSessionDTO session = userService.login(param);
            CurrentContext.setUser(session);
            return new RpcResponse<>(session);
        }
        catch (Exception exp) {
            Tuple.Pair<Integer, String> error = ParameterValidator.onException(exp);
            return new RpcResponse<>(error.getFirst(), error.getSecond());
        }
    }

    @PostMapping("/registe")
    public RpcResponse<Boolean> registe(@RequestBody UserParam param) {
        if (logger.isDebugEnabled())
            logger.debug("注册参数:{}", GsonUtil.toJson(param));

        try {
            return new RpcResponse<>(userService.registe(param));
        }
        catch (Exception exp) {
            Tuple.Pair<Integer, String> error = ParameterValidator.onException(exp);
            return new RpcResponse<>(error.getFirst(), error.getSecond());
        }
    }

    @GetMapping("/activate")
    public String activate(String key) {
        if (logger.isDebugEnabled())
            logger.debug("激活参数:{}", key);

        try {
            userService.activate(key);
            return "您的账号已激活，可以放心登录了！！！";
        }
        catch (Exception exp) {
            Tuple.Pair<Integer, String> error = ParameterValidator.onException(exp);
            return error.getSecond();
        }
    }

    @GetMapping("/list")
    public RpcResponse<PageResult<UserDTO>> getUserList(PageParam param) {
        if (logger.isDebugEnabled())
            logger.debug("查询人员列表参数:{}", GsonUtil.toJson(param));

        try {
            return new RpcResponse<>(userService.queryUserList(param));
        }
        catch (Exception exp) {
            Tuple.Pair<Integer, String> error = ParameterValidator.onException(exp);
            return new RpcResponse<>(error.getFirst(), error.getSecond());
        }
    }

}
