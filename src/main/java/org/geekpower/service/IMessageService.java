package org.geekpower.service;

import org.geekpower.common.PageResult;
import org.geekpower.common.dto.MessageDTO;
import org.geekpower.form.MessageParam;
import org.geekpower.form.PageParam;

public interface IMessageService {

    PageResult<MessageDTO> getRecipientMessages(PageParam param);

    PageResult<MessageDTO> getSendedMessages(PageParam param);
    
    PageResult<MessageDTO> getDraftMessages(PageParam param);

    Integer createFormalMessage(MessageParam param);

    Integer createDraftMessage(MessageParam param);


}
