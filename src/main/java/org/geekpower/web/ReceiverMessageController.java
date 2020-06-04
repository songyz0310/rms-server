package org.geekpower.web;

import java.util.Date;
import java.util.function.Consumer;

import org.geekpower.common.PageResult;
import org.geekpower.common.ParameterValidator;
import org.geekpower.common.RpcResponse;
import org.geekpower.common.Tuple;
import org.geekpower.common.dto.MessageRecipientDTO;
import org.geekpower.common.enums.Deleted;
import org.geekpower.common.enums.IsOrNo;
import org.geekpower.common.enums.MarkType;
import org.geekpower.entity.MessageRecipientPO;
import org.geekpower.form.DeleteMessageParam;
import org.geekpower.form.MarkMessageParam;
import org.geekpower.form.PageParam;
import org.geekpower.form.RevertMessageParam;
import org.geekpower.service.IMessageService;
import org.geekpower.utils.GsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/receiver/message")
public class ReceiverMessageController {

    private static Logger logger = LoggerFactory.getLogger(ReceiverMessageController.class);

    @Autowired
    private IMessageService messageService;

    /**
     * 收件箱列表
     * 
     * @param param
     * @return
     */
    @GetMapping("/formal/list")
    public RpcResponse<PageResult<MessageRecipientDTO>> formalList(PageParam param) {
        logger.info("收件箱查询参数:{}", GsonUtil.toJson(param));
        try {
            return new RpcResponse<>(messageService.getRecipientMessages(param, IsOrNo.NO.getCode()));
        }
        catch (Exception exp) {
            Tuple.Pair<Integer, String> error = ParameterValidator.onException(exp);
            return new RpcResponse<>(error.getFirst(), error.getSecond());
        }
    }

    /**
     * 垃圾箱列表
     * 
     * @param param
     * @return
     */
    @GetMapping("/rubbish/list")
    public RpcResponse<PageResult<MessageRecipientDTO>> rubbishList(PageParam param) {
        logger.info("垃圾箱查询参数:{}", GsonUtil.toJson(param));
        try {
            return new RpcResponse<>(messageService.getRecipientMessages(param, IsOrNo.IS.getCode()));
        }
        catch (Exception exp) {
            Tuple.Pair<Integer, String> error = ParameterValidator.onException(exp);
            return new RpcResponse<>(error.getFirst(), error.getSecond());
        }
    }

    @DeleteMapping("/delete")
    public RpcResponse<Boolean> deleteMessage(@RequestBody DeleteMessageParam param) {
        logger.info("删除收件箱参数:{}", GsonUtil.toJson(param));
        try {
            messageService.batchUpdateRecipient(po -> po.setIsDelete(Deleted.IS.getCode()), param.getIds());
            return new RpcResponse<>(true);
        }
        catch (Exception exp) {
            Tuple.Pair<Integer, String> error = ParameterValidator.onException(exp);
            return new RpcResponse<>(error.getFirst(), error.getSecond());
        }
    }

    @DeleteMapping("/real/delete")
    public RpcResponse<Boolean> realDeleteMessage(@RequestBody DeleteMessageParam param) {
        logger.info("永久删除收件箱参数:{}", GsonUtil.toJson(param));
        try {
            messageService.batchUpdateRecipient(po -> po.setIsDelete(Deleted.REAL.getCode()), param.getIds());
            return new RpcResponse<>(true);
        }
        catch (Exception exp) {
            Tuple.Pair<Integer, String> error = ParameterValidator.onException(exp);
            return new RpcResponse<>(error.getFirst(), error.getSecond());
        }
    }

    @PutMapping("/mark")
    public RpcResponse<Boolean> markMessage(@RequestBody MarkMessageParam param) {
        logger.info("标记信息参数:{}", GsonUtil.toJson(param));
        try {
            Consumer<MessageRecipientPO> action = null;
            switch (MarkType.get(param.getType())) {
                case IS_READED:
                    action = po -> {
                        po.setIsRead(IsOrNo.IS.getCode());
                        po.setReadTime(null);
                    };
                    break;
                case UN_READ:
                    action = po -> {
                        po.setIsRead(IsOrNo.NO.getCode());
                        po.setReadTime(new Date());
                    };
                    break;
                case IS_RUBBISH:
                    action = po -> po.setIsRubbish(IsOrNo.IS.getCode());
                    break;
                case UN_RUBBISH:
                    action = po -> po.setIsRubbish(IsOrNo.NO.getCode());
                    break;
            }

            messageService.batchUpdateRecipient(action, param.getIds());
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
            messageService.batchUpdateRecipient(po -> po.setIsDelete(Deleted.NO.getCode()), param.getIds());
            return new RpcResponse<>(true);
        }
        catch (Exception exp) {
            Tuple.Pair<Integer, String> error = ParameterValidator.onException(exp);
            return new RpcResponse<>(error.getFirst(), error.getSecond());
        }
    }

}
