package org.geekpower.service;

import java.util.List;
import java.util.function.Consumer;

import org.geekpower.common.PageResult;
import org.geekpower.common.dto.MessageRecipientDTO;
import org.geekpower.entity.MessageRecipientPO;
import org.geekpower.form.PageParam;

public interface IMessageRecipientService {

    PageResult<MessageRecipientDTO> getRecipientMessages(PageParam param);

    PageResult<MessageRecipientDTO> getRubbishMessages(PageParam param);

    PageResult<MessageRecipientDTO> getDeletedMessages(PageParam param);

    void batchUpdate(Consumer<MessageRecipientPO> action, List<Integer> ids);
}
