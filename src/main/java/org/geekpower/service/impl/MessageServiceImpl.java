package org.geekpower.service.impl;

import java.util.Date;
import java.util.List;
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
import org.geekpower.form.MessageParam;
import org.geekpower.form.PageParam;
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

    @Override
    public PageResult<MessageDTO> getRecipientMessages(PageParam param) {
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

        List<MessageDTO> messageList = BeanCopier.copyList(pageData.getContent(), MessageDTO.class);
        messageList.forEach(message -> {
            message.setSendUser(BeanCopier.copy(userRepository.findById(message.getSender()).get(), UserDTO.class));
        });
        PageResult<MessageDTO> result = new PageResult<>(param.getPageNo(), param.getPageSize(), messageList);

        // 是否需要总页数
        if (param.isNeedTotal()) {
            result.setTotal(pageData.getTotalElements());
        }

        return result;
    }

    @Override
    public PageResult<MessageDTO> getSendMessages(PageParam param) {
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

        List<MessageDTO> messageList = BeanCopier.copyList(pageData.getContent(), MessageDTO.class);
        messageList.forEach(message -> {
            // 查询收件人
            List<Integer> recipients = messageRecipientRepository.queryByMessageId(message.getMessageId()).stream()
                    .map(po -> po.getRecipient()).collect(Collectors.toList());

            message.setRecipientUsers(BeanCopier.copyList(userRepository.findAllById(recipients), UserDTO.class));

        });

        PageResult<MessageDTO> result = new PageResult<>(param.getPageNo(), param.getPageSize(), messageList);

        // 是否需要总页数
        if (param.isNeedTotal()) {
            result.setTotal(pageData.getTotalElements());
        }

        return result;
    }

    @Override
    public Integer createFormalMessage(MessageParam param) {

        Date now = new Date();
        UserDTO user = CurrentContext.getUser();

        MessagePO messagePO = BeanCopier.copy(param, MessagePO.class);
        messagePO.setMessageId(null);
        messagePO.setStatus(MessageStatus.FORMAL.getCode());
        messagePO.setIsDelete(Deleted.NO.getCode());
        messagePO.setSender(user.getUserId());
        messagePO.setCreateTime(now);
        messagePO.setUpdateTime(now);
        messagePO.setSendTime(now);

        messageRepository.save(messagePO);

        List<MessageRecipientPO> pos = param.getRecipients().stream().map(recipient -> {
            MessageRecipientPO po = new MessageRecipientPO();
            po.setMessageId(messagePO.getMessageId());
            po.setRecipient(recipient);
            po.setIsDelete(Deleted.NO.getCode());
            po.setIsRubbish(IsOrNo.NO.getCode());
            po.setIsRead(IsOrNo.NO.getCode());
            po.setUpdateTime(now);
            return po;
        }).collect(Collectors.toList());

        messageRecipientRepository.saveAll(pos);

        return messagePO.getMessageId();
    }

    @Override
    public Integer createDraftMessage(MessageParam param) {
        // TODO Auto-generated method stub
        return null;
    }

}
