package org.geekpower.web;

import java.util.Objects;

import org.geekpower.common.PageResult;
import org.geekpower.common.ParameterValidator;
import org.geekpower.common.RpcResponse;
import org.geekpower.common.Tuple;
import org.geekpower.common.dto.MessageDTO;
import org.geekpower.common.enums.Deleted;
import org.geekpower.common.enums.MessageStatus;
import org.geekpower.form.DeleteMessageParam;
import org.geekpower.form.MessageParam;
import org.geekpower.form.PageParam;
import org.geekpower.form.RevertMessageParam;
import org.geekpower.service.IMessageService;
import org.geekpower.utils.GsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sender/message")
public class SenderMessageController {

    private static Logger logger = LoggerFactory.getLogger(SenderMessageController.class);

    @Autowired
    private IMessageService messageService;

    /**
     * 发件箱列表
     * 
     * @param param
     * @return
     */
    @GetMapping("/formal/list")
    public RpcResponse<PageResult<MessageDTO>> formalList(PageParam param) {
        logger.info("发件箱查询参数:{}", GsonUtil.toJson(param));
        try {
            return new RpcResponse<>(messageService.getSendedMessages(param, MessageStatus.FORMAL.getCode()));
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
            return new RpcResponse<>(messageService.getSendedMessages(param, MessageStatus.DRAFT.getCode()));
        }
        catch (Exception exp) {
            Tuple.Pair<Integer, String> error = ParameterValidator.onException(exp);
            return new RpcResponse<>(error.getFirst(), error.getSecond());
        }
    }

    @PostMapping("/save/formal")
    public RpcResponse<Integer> saveFormalMessage(@RequestBody MessageParam param) {
        logger.info("保存正式消息:{}", GsonUtil.toJson(param));
        try {
            param.setStatus(MessageStatus.FORMAL.getCode());
            if (Objects.isNull(param.getMessageId())) {
                return new RpcResponse<>(messageService.createMessage(param));
            }
            else {
                return new RpcResponse<>(messageService.updateMessage(param));
            }
        }
        catch (Exception exp) {
            Tuple.Pair<Integer, String> error = ParameterValidator.onException(exp);
            return new RpcResponse<>(error.getFirst(), error.getSecond());
        }
    }

    @PostMapping("/save/draft")
    public RpcResponse<Integer> saveDraftMessage(@RequestBody MessageParam param) {
        logger.info("保存草稿消息:{}", GsonUtil.toJson(param));
        try {
            param.setStatus(MessageStatus.DRAFT.getCode());
            if (Objects.isNull(param.getMessageId())) {
                return new RpcResponse<>(messageService.createMessage(param));
            }
            else {
                return new RpcResponse<>(messageService.updateMessage(param));
            }
        }
        catch (Exception exp) {
            Tuple.Pair<Integer, String> error = ParameterValidator.onException(exp);
            return new RpcResponse<>(error.getFirst(), error.getSecond());
        }
    }

    @DeleteMapping("/delete")
    public RpcResponse<Boolean> deleteMessage(@RequestBody DeleteMessageParam param) {
        logger.info("删除信息参数:{}", GsonUtil.toJson(param));
        try {
            messageService.batchUpdateMessage(po -> po.setIsDelete(Deleted.IS.getCode()), param.getIds());
            return new RpcResponse<>(true);
        }
        catch (Exception exp) {
            Tuple.Pair<Integer, String> error = ParameterValidator.onException(exp);
            return new RpcResponse<>(error.getFirst(), error.getSecond());
        }
    }

    @DeleteMapping("/real/delete")
    public RpcResponse<Boolean> realDeleteMessage(@RequestBody DeleteMessageParam param) {
        logger.info("永久删除信息参数:{}", GsonUtil.toJson(param));
        try {
            messageService.batchUpdateMessage(po -> po.setIsDelete(Deleted.REAL.getCode()), param.getIds());
            return new RpcResponse<>(true);
        }
        catch (Exception exp) {
            Tuple.Pair<Integer, String> error = ParameterValidator.onException(exp);
            return new RpcResponse<>(error.getFirst(), error.getSecond());
        }
    }

    @PutMapping("/revert")
    public RpcResponse<Boolean> revertMessage(@RequestBody RevertMessageParam param) {
        logger.info("恢复删除的信息参数:{}", GsonUtil.toJson(param));
        try {
            messageService.batchUpdateMessage(po -> po.setIsDelete(Deleted.NO.getCode()), param.getIds());
            return new RpcResponse<>(true);
        }
        catch (Exception exp) {
            Tuple.Pair<Integer, String> error = ParameterValidator.onException(exp);
            return new RpcResponse<>(error.getFirst(), error.getSecond());
        }
    }

}
