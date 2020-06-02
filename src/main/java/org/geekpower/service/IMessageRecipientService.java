package org.geekpower.service;

import org.geekpower.common.PageResult;
import org.geekpower.common.dto.MessageDTO;
import org.geekpower.common.dto.MessageRecipientDTO;
import org.geekpower.form.DeleteMessageParam;
import org.geekpower.form.PageParam;

public interface IMessageRecipientService {

    PageResult<MessageRecipientDTO> getRecipientMessages(PageParam param);

    PageResult<MessageDTO> getRubbishMessages(PageParam param);

    PageResult<MessageDTO> getDeletedMessages(PageParam param);

    void deleteRecipientMessage(DeleteMessageParam param);

    void realDeleteRecipientMessage(DeleteMessageParam param);
}
