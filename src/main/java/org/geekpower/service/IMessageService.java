package org.geekpower.service;

import java.util.List;
import java.util.function.Consumer;

import org.geekpower.common.PageResult;
import org.geekpower.common.dto.MessageDTO;
import org.geekpower.common.dto.MessageRecipientDTO;
import org.geekpower.entity.MessagePO;
import org.geekpower.entity.MessageRecipientPO;
import org.geekpower.form.MessageParam;
import org.geekpower.form.PageParam;

public interface IMessageService {

    PageResult<MessageDTO> getSendedMessages(PageParam param, byte status);

    PageResult<MessageRecipientDTO> getRecipientMessages(PageParam param, byte isRubbish);

    PageResult<MessageRecipientDTO> getDeletedMessages(PageParam param);

    MessageDTO getMessagesDetail(int messageId);

    int createMessage(MessageParam param);

    int updateMessage(MessageParam param);

    void batchUpdateMessage(Consumer<MessagePO> action, List<Integer> ids);

    void batchUpdateRecipient(Consumer<MessageRecipientPO> action, List<Integer> ids);

}
