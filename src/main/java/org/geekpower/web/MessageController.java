package org.geekpower.web;

import org.geekpower.common.PageResult;
import org.geekpower.common.ParameterValidator;
import org.geekpower.common.RpcResponse;
import org.geekpower.common.Tuple;
import org.geekpower.common.dto.MessageDTO;
import org.geekpower.form.MessageParam;
import org.geekpower.form.PageParam;
import org.geekpower.service.IMessageService;
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
@RequestMapping("/message")
public class MessageController {

    private static Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private IMessageService messageService;

    /**
     * 收件箱列表
     * 
     * @param param
     * @return
     */
    @GetMapping("/recipient/list")
    public RpcResponse<PageResult<MessageDTO>> recipientList(PageParam param) {
        logger.info("收件箱查询参数:{}", GsonUtil.toJson(param));
        try {
            return new RpcResponse<>(messageService.getRecipientMessages(param));
        }
        catch (Exception exp) {
            Tuple.Pair<Integer, String> error = ParameterValidator.onException(exp);
            return new RpcResponse<>(error.getFirst(), error.getSecond());
        }
    }

    /**
     * 发件箱列表
     * 
     * @param param
     * @return
     */
    @GetMapping("/sended/list")
    public RpcResponse<PageResult<MessageDTO>> sendedList(PageParam param) {
        logger.info("发件箱查询参数:{}", GsonUtil.toJson(param));
        try {
            return new RpcResponse<>(messageService.getSendedMessages(param));
        }
        catch (Exception exp) {
            Tuple.Pair<Integer, String> error = ParameterValidator.onException(exp);
            return new RpcResponse<>(error.getFirst(), error.getSecond());
        }
    }

    /**
     * 草稿箱列表
     * 
     * @param param
     * @return
     */
    @GetMapping("/draft/list")
    public RpcResponse<PageResult<MessageDTO>> draftList(PageParam param) {
        logger.info("草稿箱查询参数:{}", GsonUtil.toJson(param));
        try {
            return new RpcResponse<>(messageService.getDraftMessages(param));
        }
        catch (Exception exp) {
            Tuple.Pair<Integer, String> error = ParameterValidator.onException(exp);
            return new RpcResponse<>(error.getFirst(), error.getSecond());
        }
    }

    @PostMapping("/create/formal")
    public RpcResponse<Integer> createFormalMessage(@RequestBody MessageParam param) {
        logger.info("创建正式消息:{}", GsonUtil.toJson(param));
        try {
            return new RpcResponse<>(messageService.createFormalMessage(param));
        }
        catch (Exception exp) {
            Tuple.Pair<Integer, String> error = ParameterValidator.onException(exp);
            return new RpcResponse<>(error.getFirst(), error.getSecond());
        }
    }

    @PostMapping("/create/draft")
    public RpcResponse<Integer> createDraftMessage(@RequestBody MessageParam param) {
        logger.info("创建草稿消息:{}", GsonUtil.toJson(param));
        try {
            return new RpcResponse<>(messageService.createDraftMessage(param));
        }
        catch (Exception exp) {
            Tuple.Pair<Integer, String> error = ParameterValidator.onException(exp);
            return new RpcResponse<>(error.getFirst(), error.getSecond());
        }
    }

}
