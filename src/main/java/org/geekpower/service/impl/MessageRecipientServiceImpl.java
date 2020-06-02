package org.geekpower.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.geekpower.common.CurrentContext;
import org.geekpower.common.PageResult;
import org.geekpower.common.dto.MessageDTO;
import org.geekpower.common.dto.MessageRecipientDTO;
import org.geekpower.common.dto.UserDTO;
import org.geekpower.common.enums.Deleted;
import org.geekpower.common.enums.IsOrNo;
import org.geekpower.entity.MessagePO;
import org.geekpower.entity.MessageRecipientPO;
import org.geekpower.form.DeleteMessageParam;
import org.geekpower.form.PageParam;
import org.geekpower.repository.IMessageRecipientRepository;
import org.geekpower.repository.IMessageRepository;
import org.geekpower.repository.IUserRepository;
import org.geekpower.service.IMessageRecipientService;
import org.geekpower.utils.BeanCopier;
import org.geekpower.utils.RepositoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MessageRecipientServiceImpl implements IMessageRecipientService {

    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IMessageRepository messageRepository;
    @Autowired
    private IMessageRecipientRepository messageRecipientRepository;

    @Override
    public PageResult<MessageRecipientDTO> getRecipientMessages(PageParam param) {
        UserDTO user = CurrentContext.getUser();
        MessageRecipientPO filter = new MessageRecipientPO();
        filter.setIsDelete(Deleted.NO.getCode());
        filter.setRecipient(user.getUserId());

        // 创建匹配器，即如何使用查询条件
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("isDelete", ExampleMatcher.GenericPropertyMatchers.storeDefaultMatching())
                .withMatcher("recipient", ExampleMatcher.GenericPropertyMatchers.storeDefaultMatching())
                .withIgnorePaths("mrId", "messageId", "isRead", "isRubbish", "readTime", "updateTime");

        Example<MessageRecipientPO> example = Example.of(filter, exampleMatcher);

        Page<MessageRecipientPO> pageData = messageRecipientRepository.findAll(example,
                RepositoryUtils.initPageable(param));

        List<MessageRecipientDTO> messageList = BeanCopier.copyList(pageData.getContent(), MessageRecipientDTO.class);

        Map<Integer, MessagePO> map = messageRepository
                .findAllById(pageData.stream().map(po -> po.getMessageId()).collect(Collectors.toList())).stream()
                .collect(Collectors.toMap(po -> po.getMessageId(), po -> po));

        messageList.forEach(dto -> {
            BeanCopier.copy(map.get(dto.getMessageId()), dto);
            dto.setSendUser(BeanCopier.copy(userRepository.findById(dto.getSender()).get(), UserDTO.class));
        });

        PageResult<MessageRecipientDTO> result = new PageResult<>(param.getPageNo(), param.getPageSize(), messageList);

        // 是否需要总页数
        if (param.isNeedTotal()) {
            result.setTotal(pageData.getTotalElements());
        }

        return result;
    }

    @Override
    public PageResult<MessageDTO> getRubbishMessages(PageParam param) {
        UserDTO user = CurrentContext.getUser();
        MessageRecipientPO filter = new MessageRecipientPO();
        filter.setIsDelete(Deleted.NO.getCode());
        filter.setIsRubbish(IsOrNo.IS.getCode());
        filter.setRecipient(user.getUserId());

        // 创建匹配器，即如何使用查询条件
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("isDelete", ExampleMatcher.GenericPropertyMatchers.storeDefaultMatching())
                .withMatcher("isRubbish", ExampleMatcher.GenericPropertyMatchers.storeDefaultMatching())
                .withMatcher("recipient", ExampleMatcher.GenericPropertyMatchers.storeDefaultMatching())
                .withIgnorePaths("mrId", "messageId", "isRead", "readTime", "updateTime");

        Example<MessageRecipientPO> example = Example.of(filter, exampleMatcher);

        Page<MessageRecipientPO> pageData = messageRecipientRepository.findAll(example,
                RepositoryUtils.initPageable(param));

        List<MessagePO> list = messageRepository
                .findAllById(pageData.stream().map(po -> po.getMessageId()).collect(Collectors.toList()));

        List<MessageDTO> messageList = BeanCopier.copyList(list, MessageDTO.class);
        messageList.forEach(dto -> {
            dto.setSendUser(BeanCopier.copy(userRepository.findById(dto.getSender()).get(), UserDTO.class));
        });

        PageResult<MessageDTO> result = new PageResult<>(param.getPageNo(), param.getPageSize(), messageList);

        // 是否需要总页数
        if (param.isNeedTotal()) {
            result.setTotal(pageData.getTotalElements());
        }

        return result;
    }

    @Override
    public PageResult<MessageDTO> getDeletedMessages(PageParam param) {
        int userId = CurrentContext.getUser().getUserId();

        Pageable pageable = RepositoryUtils.initPageable(param);
        Page<Integer> pageData = messageRepository.queryDeletedIds(userId, Deleted.IS.getCode(), pageable);

        List<MessageDTO> messageList = BeanCopier.copyList(messageRepository.findAllById(pageData), MessageDTO.class);

        messageList.forEach(dto -> {
            // 查询发件人
            dto.setSendUser(BeanCopier.copy(userRepository.findById(dto.getSender()).get(), UserDTO.class));
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
    public void deleteRecipientMessage(DeleteMessageParam param) {
        List<MessageRecipientPO> pos = messageRecipientRepository.findAllById(param.getIds());
        Date now = new Date();
        pos.forEach(po -> {
            po.setIsDelete(Deleted.IS.getCode());
            po.setUpdateTime(now);
        });

        messageRecipientRepository.saveAll(pos);
    }

    @Override
    public void realDeleteRecipientMessage(DeleteMessageParam param) {
        List<MessageRecipientPO> pos = messageRecipientRepository.findAllById(param.getIds());
        Date now = new Date();
        pos.forEach(po -> {
            po.setIsDelete(Deleted.REAL.getCode());
            po.setUpdateTime(now);
        });

        messageRecipientRepository.saveAll(pos);
    }

}
