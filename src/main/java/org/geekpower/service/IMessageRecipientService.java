package org.geekpower.service;

import org.geekpower.common.PageResult;
import org.geekpower.common.dto.MessageRecipientDTO;
import org.geekpower.form.DeleteMessageParam;
import org.geekpower.form.MarkMessageParam;
import org.geekpower.form.PageParam;
import org.geekpower.form.RevertMessageParam;

public interface IMessageRecipientService {

    PageResult<MessageRecipientDTO> getRecipientMessages(PageParam param);

    PageResult<MessageRecipientDTO> getRubbishMessages(PageParam param);

    PageResult<MessageRecipientDTO> getDeletedMessages(PageParam param);

    void deleteRecipientMessage(DeleteMessageParam param);

    void realDeleteRecipientMessage(DeleteMessageParam param);

    void markMessage(MarkMessageParam param);

    void revertMessage(RevertMessageParam param);
}
