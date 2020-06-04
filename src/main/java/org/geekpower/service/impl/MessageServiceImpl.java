package org.geekpower.service.impl;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.geekpower.common.CurrentContext;
import org.geekpower.common.PageResult;
import org.geekpower.common.dto.MessageDTO;
import org.geekpower.common.dto.MessageRecipientDTO;
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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl implements IMessageService {

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IMessageRepository messageRepository;
    @Autowired
    private IMessageRecipientRepository messageRecipientRepository;

    @Override
    public PageResult<MessageDTO> getSendedMessages(PageParam param, byte status) {
        UserDTO user = CurrentContext.getUser();
        MessagePO filter = new MessagePO();

        filter.setStatus(status);
        filter.setSender(user.getUserId());
        filter.setIsDelete(Deleted.NO.getCode());

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
        messageList.forEach(dto -> {
            // 查询收件人
            List<Integer> recipients = messageRecipientRepository.queryByMessageId(dto.getMessageId()).stream()
                    .map(po -> po.getRecipient()).collect(Collectors.toList());

            dto.setRecipientUsers(BeanCopier.copyList(userRepository.findAllById(recipients), UserDTO.class));
        });
        PageResult<MessageDTO> result = new PageResult<>(param.getPageNo(), param.getPageSize(), messageList);

        // 是否需要总页数
        if (param.isNeedTotal()) {
            result.setTotal(pageData.getTotalElements());
        }

        return result;
    }

    @Override
    public PageResult<MessageRecipientDTO> getRecipientMessages(PageParam param, byte isRubbish) {
        UserDTO user = CurrentContext.getUser();

        @SuppressWarnings("unchecked")
        List<MessageRecipientPO> pageData = entityManager.createNativeQuery(
                "SELECT tmr.* FROM tb_message_recipient tmr INNER JOIN tb_message tm ON tmr.message_id=tm.message_id "
                        + "where tmr.recipient = :recipient and tmr.is_delete = :is_delete and tmr.is_rubbish = :is_rubbish and tm.status = :status"
                        + RepositoryUtils.getLimit(param),
                MessageRecipientPO.class)//
                .setParameter("is_rubbish", isRubbish)//
                .setParameter("recipient", user.getUserId())//
                .setParameter("is_delete", Deleted.NO.getCode())//
                .setParameter("status", MessageStatus.FORMAL.getCode())//
                .getResultList();

        List<MessageRecipientDTO> listData = BeanCopier.copyList(pageData, MessageRecipientDTO.class);

        Map<Integer, MessagePO> messageMap = messageRepository
                .findAllById(listData.stream().map(po -> po.getMessageId()).collect(Collectors.toSet())).stream()
                .collect(Collectors.toMap(po -> po.getMessageId(), po -> po));

        listData.forEach(dto -> {
            BeanCopier.copy(messageMap.get(dto.getMessageId()), dto);

            // 查询发件人
            dto.setSendUser(BeanCopier.copy(userRepository.findById(dto.getSender()).get(), UserDTO.class));
        });

        PageResult<MessageRecipientDTO> result = new PageResult<>(param.getPageNo(), param.getPageSize(), listData);

        // 是否需要总页数
        if (param.isNeedTotal()) {
            BigInteger total = (BigInteger) entityManager.createNativeQuery(
                    "SELECT count(tmr.mr_id) FROM tb_message_recipient tmr INNER JOIN tb_message tm ON tmr.message_id=tm.message_id "
                            + "where tmr.recipient = :recipient and tmr.is_delete = :is_delete and tmr.is_rubbish = :is_rubbish and tm.status = :status")//
                    .setParameter("is_rubbish", isRubbish)//
                    .setParameter("recipient", user.getUserId())//
                    .setParameter("is_delete", Deleted.NO.getCode())//
                    .setParameter("status", MessageStatus.FORMAL.getCode())//
                    .getSingleResult();
            result.setTotal(total.longValue());
        }

        return result;
    }

    @Override
    public PageResult<MessageRecipientDTO> getDeletedMessages(PageParam param) {
        int userId = CurrentContext.getUser().getUserId();

        Pageable pageable = RepositoryUtils.initPageable(param);
        Page<Map<String, Object>> pageData = messageRecipientRepository.queryDeletedIds(userId, Deleted.IS.getCode(),
                pageable);

        Map<Integer, MessagePO> map = messageRepository
                .findAllById(pageData.stream().map(item -> Integer.parseInt(item.get("messageId").toString()))
                        .collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(po -> po.getMessageId(), po -> po));

        List<MessageRecipientDTO> listData = pageData.stream().map(item -> {
            MessageRecipientDTO dto = new MessageRecipientDTO();
            dto.setMrId(Integer.parseInt(item.get("mrId").toString()));
            dto.setMessageId(Integer.parseInt(item.get("messageId").toString()));
            return dto;
        }).collect(Collectors.toList());

        listData.forEach(dto -> {
            BeanCopier.copy(map.get(dto.getMessageId()), dto);

            // 查询发件人
            if (dto.getSender() > 0) {
                dto.setSendUser(BeanCopier.copy(userRepository.findById(dto.getSender()).get(), UserDTO.class));
            }
            // 查询收件人
            List<Integer> recipients = messageRecipientRepository.queryByMessageId(dto.getMessageId()).stream()
                    .map(po -> po.getRecipient()).collect(Collectors.toList());

            dto.setRecipientUsers(BeanCopier.copyList(userRepository.findAllById(recipients), UserDTO.class));
        });

        PageResult<MessageRecipientDTO> result = new PageResult<>(param.getPageNo(), param.getPageSize(), listData);

        // 是否需要总页数
        if (param.isNeedTotal()) {
            result.setTotal(pageData.getTotalElements());
        }

        return result;
    }

    @Override
    public MessageDTO getMessagesDetail(int messageId) {
        return BeanCopier.copy(messageRepository.findById(messageId).get(), MessageDTO.class);
    }

    @Override
    @Transactional
    public int createMessage(MessageParam param) {
        Date now = new Date();
        UserDTO user = CurrentContext.getUser();

        MessagePO messagePO = BeanCopier.copy(param, MessagePO.class);
        messagePO.setMessageId(null);
        messagePO.setIsDelete(Deleted.NO.getCode());
        messagePO.setSender(user.getUserId());
        messagePO.setCreateTime(now);
        messagePO.setUpdateTime(now);

        if (Objects.equals(param.getStatus(), MessageStatus.FORMAL.getCode())) {
            messagePO.setSendTime(now);
        }

        messageRepository.save(messagePO);

        messageRecipientRepository.saveAll(param.getRecipients().stream().map(recipient -> {
            MessageRecipientPO po = new MessageRecipientPO();
            po.setMessageId(messagePO.getMessageId());
            po.setRecipient(recipient);
            po.setIsDelete(Deleted.NO.getCode());
            po.setIsRubbish(IsOrNo.NO.getCode());
            po.setIsRead(IsOrNo.NO.getCode());
            po.setUpdateTime(now);
            return po;
        }).collect(Collectors.toList()));

        return messagePO.getMessageId();
    }

    @Override
    @Transactional
    public int updateMessage(MessageParam param) {
        Date now = new Date();
        UserDTO user = CurrentContext.getUser();

        MessagePO messagePO = BeanCopier.copy(param, MessagePO.class);
        messagePO.setIsDelete(Deleted.NO.getCode());
        messagePO.setSender(user.getUserId());
        messagePO.setCreateTime(now);
        messagePO.setUpdateTime(now);
        if (Objects.equals(param.getStatus(), MessageStatus.FORMAL.getCode())) {
            messagePO.setSendTime(now);
        }

        messageRepository.save(messagePO);

        messageRecipientRepository.deleteByMessageId(param.getMessageId());

        messageRecipientRepository.saveAll(param.getRecipients().stream().map(recipient -> {
            MessageRecipientPO po = new MessageRecipientPO();
            po.setMessageId(messagePO.getMessageId());
            po.setRecipient(recipient);
            po.setIsDelete(Deleted.NO.getCode());
            po.setIsRubbish(IsOrNo.NO.getCode());
            po.setIsRead(IsOrNo.NO.getCode());
            po.setUpdateTime(now);
            return po;
        }).collect(Collectors.toList()));

        return messagePO.getMessageId();
    }

    @Override
    @Transactional
    public void batchUpdateMessage(Consumer<MessagePO> action, List<Integer> ids) {
        List<MessagePO> pos = messageRepository.findAllById(ids);

        Date now = new Date();
        pos.forEach(action.andThen(po -> po.setUpdateTime(now)));

        messageRepository.saveAll(pos);
    }

    @Override
    @Transactional
    public void batchUpdateRecipient(Consumer<MessageRecipientPO> action, List<Integer> ids) {
        List<MessageRecipientPO> pos = messageRecipientRepository.findAllById(ids);

        Date now = new Date();
        pos.forEach(action.andThen(po -> po.setUpdateTime(now)));

        messageRecipientRepository.saveAll(pos);
    }

}
