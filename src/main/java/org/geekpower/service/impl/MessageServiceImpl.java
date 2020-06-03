package org.geekpower.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.geekpower.common.CurrentContext;
import org.geekpower.common.PageResult;
import org.geekpower.common.dto.MessageDTO;
import org.geekpower.common.dto.UserDTO;
import org.geekpower.common.enums.Deleted;
import org.geekpower.common.enums.IsOrNo;
import org.geekpower.common.enums.MessageStatus;
import org.geekpower.entity.MessagePO;
import org.geekpower.entity.MessageRecipientPO;
import org.geekpower.form.DeleteMessageParam;
import org.geekpower.form.MessageParam;
import org.geekpower.form.PageParam;
import org.geekpower.form.RevertMessageParam;
import org.geekpower.repository.IMessageRecipientRepository;
import org.geekpower.repository.IMessageRepository;
import org.geekpower.repository.IUserRepository;
import org.geekpower.service.IMessageService;
import org.geekpower.utils.BeanCopier;
import org.geekpower.utils.RepositoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl implements IMessageService {

    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IMessageRepository messageRepository;
    @Autowired
    private IMessageRecipientRepository messageRecipientRepository;

    private PageResult<MessageDTO> convertResultData(PageParam param, Page<MessagePO> pageData,
            Consumer<MessageDTO> consumer) {
        List<MessageDTO> messageList = BeanCopier.copyList(pageData.getContent(), MessageDTO.class);
        if (Objects.nonNull(consumer)) {
            messageList.forEach(consumer::accept);
        }
        PageResult<MessageDTO> result = new PageResult<>(param.getPageNo(), param.getPageSize(), messageList);

        // 是否需要总页数
        if (param.isNeedTotal()) {
            result.setTotal(pageData.getTotalElements());
        }

        return result;
    }

    private MessagePO createMessagePO(MessageParam param, MessageStatus status) {
        Date now = new Date();
        UserDTO user = CurrentContext.getUser();

        MessagePO messagePO = BeanCopier.copy(param, MessagePO.class);
        messagePO.setMessageId(null);
        messagePO.setStatus(status.getCode());
        messagePO.setIsDelete(Deleted.NO.getCode());
        messagePO.setSender(user.getUserId());
        messagePO.setCreateTime(now);
        messagePO.setUpdateTime(now);
        messagePO.setSendTime(now);

        return messagePO;
    }

    private List<MessageRecipientPO> createMessageRecipientPOs(List<Integer> recipients, Integer messageId) {
        Date now = new Date();
        return recipients.stream().map(recipient -> {
            MessageRecipientPO po = new MessageRecipientPO();
            po.setMessageId(messageId);
            po.setRecipient(recipient);
            po.setIsDelete(Deleted.NO.getCode());
            po.setIsRubbish(IsOrNo.NO.getCode());
            po.setIsRead(IsOrNo.NO.getCode());
            po.setUpdateTime(now);
            return po;
        }).collect(Collectors.toList());
    }

    @Override
    public PageResult<MessageDTO> getSendedMessages(PageParam param) {
        UserDTO user = CurrentContext.getUser();
        MessagePO filter = new MessagePO();
        filter.setStatus(MessageStatus.FORMAL.getCode());
        filter.setIsDelete(Deleted.NO.getCode());
        filter.setSender(user.getUserId());

        // 创建匹配器，即如何使用查询条件
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("status", ExampleMatcher.GenericPropertyMatchers.storeDefaultMatching())
                .withMatcher("isDelete", ExampleMatcher.GenericPropertyMatchers.storeDefaultMatching())
                .withMatcher("sender", ExampleMatcher.GenericPropertyMatchers.storeDefaultMatching())
                .withIgnorePaths("messageId", "messageTitle", "richContent", "simpleContent", "createTime",
                        "updateTime", "sendTime", "refMessageId");

        Example<MessagePO> example = Example.of(filter, exampleMatcher);

        Page<MessagePO> pageData = messageRepository.findAll(example, RepositoryUtils.initPageable(param));

        return convertResultData(param, pageData, dto -> {
            // 查询收件人
            List<Integer> recipients = messageRecipientRepository.queryByMessageId(dto.getMessageId()).stream()
                    .map(po -> po.getRecipient()).collect(Collectors.toList());

            dto.setRecipientUsers(BeanCopier.copyList(userRepository.findAllById(recipients), UserDTO.class));
        });
    }

    @Override
    public PageResult<MessageDTO> getDraftMessages(PageParam param) {
        UserDTO user = CurrentContext.getUser();
        MessagePO filter = new MessagePO();
        filter.setStatus(MessageStatus.DRAFT.getCode());
        filter.setIsDelete(Deleted.NO.getCode());
        filter.setSender(user.getUserId());

        // 创建匹配器，即如何使用查询条件
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("status", ExampleMatcher.GenericPropertyMatchers.storeDefaultMatching())
                .withMatcher("isDelete", ExampleMatcher.GenericPropertyMatchers.storeDefaultMatching())
                .withMatcher("sender", ExampleMatcher.GenericPropertyMatchers.storeDefaultMatching())
                .withIgnorePaths("messageId", "messageTitle", "richContent", "simpleContent", "createTime",
                        "updateTime", "sendTime", "refMessageId");

        Example<MessagePO> example = Example.of(filter, exampleMatcher);

        Page<MessagePO> pageData = messageRepository.findAll(example, RepositoryUtils.initPageable(param));

        return convertResultData(param, pageData, dto -> {
            // 查询收件人
            List<Integer> recipients = messageRecipientRepository.queryByMessageId(dto.getMessageId()).stream()
                    .map(po -> po.getRecipient()).collect(Collectors.toList());

            dto.setRecipientUsers(BeanCopier.copyList(userRepository.findAllById(recipients), UserDTO.class));
        });
    }

    @Override
    public Integer createFormalMessage(MessageParam param) {

        MessagePO messagePO = createMessagePO(param, MessageStatus.FORMAL);

        messageRepository.save(messagePO);

        messageRecipientRepository.saveAll(createMessageRecipientPOs(param.getRecipients(), messagePO.getMessageId()));

        return messagePO.getMessageId();
    }

    @Override
    public Integer createDraftMessage(MessageParam param) {
        MessagePO messagePO = createMessagePO(param, MessageStatus.DRAFT);

        messageRepository.save(messagePO);

        messageRecipientRepository.saveAll(createMessageRecipientPOs(param.getRecipients(), messagePO.getMessageId()));

        return messagePO.getMessageId();
    }

    @Override
    public void deleteMessage(DeleteMessageParam param) {
        List<MessagePO> pos = messageRepository.findAllById(param.getIds());
        Date now = new Date();
        pos.forEach(po -> {
            po.setIsDelete(Deleted.IS.getCode());
            po.setUpdateTime(now);
        });

        messageRepository.saveAll(pos);
    }

    @Override
    public void realDeleteMessage(DeleteMessageParam param) {
        List<MessagePO> pos = messageRepository.findAllById(param.getIds());
        Date now = new Date();
        pos.forEach(po -> {
            po.setIsDelete(Deleted.REAL.getCode());
            po.setUpdateTime(now);
        });

        messageRepository.saveAll(pos);
    }

    @Override
    public void revertMessage(RevertMessageParam param) {
        List<MessagePO> pos = messageRepository.findAllById(param.getIds());
        Date now = new Date();
        pos.forEach(po -> {
            po.setIsDelete(Deleted.NO.getCode());
            po.setUpdateTime(now);
        });

        messageRepository.saveAll(pos);
    }

}
