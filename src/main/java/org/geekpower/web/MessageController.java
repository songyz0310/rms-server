package org.geekpower.web;

import org.geekpower.common.PageResult;
import org.geekpower.common.ParameterValidator;
import org.geekpower.common.RpcResponse;
import org.geekpower.common.Tuple;
import org.geekpower.common.dto.MessageRecipientDTO;
import org.geekpower.form.PageParam;
import org.geekpower.service.IMessageRecipientService;
import org.geekpower.utils.GsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/message")
public class MessageController {

    private static Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private IMessageRecipientService messageRecipientService;

    /**
     * 已删除列表
     * 
     * @param param
     * @return
     */
    @GetMapping("/deleted/list")
    public RpcResponse<PageResult<MessageRecipientDTO>> deletedList(PageParam param) {
        logger.info("已删除查询参数:{}", GsonUtil.toJson(param));
        try {
            return new RpcResponse<>(messageRecipientService.getDeletedMessages(param));
        }
        catch (Exception exp) {
            Tuple.Pair<Integer, String> error = ParameterValidator.onException(exp);
            return new RpcResponse<>(error.getFirst(), error.getSecond());
        }
    }

}
