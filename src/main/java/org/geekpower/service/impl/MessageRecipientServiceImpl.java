package org.geekpower.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.geekpower.common.CurrentContext;
import org.geekpower.common.PageResult;
import org.geekpower.common.dto.MessageRecipientDTO;
import org.geekpower.common.dto.UserDTO;
import org.geekpower.common.enums.Deleted;
import org.geekpower.common.enums.IsOrNo;
import org.geekpower.entity.MessagePO;
import org.geekpower.entity.MessageRecipientPO;
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

    private PageResult<MessageRecipientDTO> convertResult(PageParam param, Page<?> pageData,
            List<MessageRecipientDTO> listData) {

        PageResult<MessageRecipientDTO> result = new PageResult<>(param.getPageNo(), param.getPageSize(), listData);

        // 是否需要总页数
        if (param.isNeedTotal()) {
            result.setTotal(pageData.getTotalElements());
        }

        return result;
    }

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

        List<MessageRecipientDTO> listData = BeanCopier.copyList(pageData.getContent(), MessageRecipientDTO.class);

        Map<Integer, MessagePO> messageMap = messageRepository
                .findAllById(pageData.stream().map(po -> po.getMessageId()).collect(Collectors.toSet())).stream()
                .collect(Collectors.toMap(po -> po.getMessageId(), po -> po));

        listData.forEach(dto -> {
            BeanCopier.copy(messageMap.get(dto.getMessageId()), dto);
            dto.setSendUser(BeanCopier.copy(userRepository.findById(dto.getSender()).get(), UserDTO.class));
        });

        return convertResult(param, pageData, listData);
    }

    @Override
    public PageResult<MessageRecipientDTO> getRubbishMessages(PageParam param) {
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

        Page<MessageRecipientPO> pageData = messageRecipientRepository.findAll(Example.of(filter, exampleMatcher),
                RepositoryUtils.initPageable(param));

        List<MessageRecipientDTO> listData = BeanCopier.copyList(pageData.getContent(), MessageRecipientDTO.class);

        Map<Integer, MessagePO> map = messageRepository
                .findAllById(pageData.stream().map(po -> po.getMessageId()).collect(Collectors.toSet())).stream()
                .collect(Collectors.toMap(po -> po.getMessageId(), po -> po));

        listData.forEach(dto -> {
            BeanCopier.copy(map.get(dto.getMessageId()), dto);
            dto.setSendUser(BeanCopier.copy(userRepository.findById(dto.getSender()).get(), UserDTO.class));
        });

        return convertResult(param, pageData, listData);
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

            if (dto.getSender() > 0) {
                // 查询发件人
                dto.setSendUser(BeanCopier.copy(userRepository.findById(dto.getSender()).get(), UserDTO.class));

            }
            // 查询收件人
            List<Integer> recipients = messageRecipientRepository.queryByMessageId(dto.getMessageId()).stream()
                    .map(po -> po.getRecipient()).collect(Collectors.toList());

            dto.setRecipientUsers(BeanCopier.copyList(userRepository.findAllById(recipients), UserDTO.class));
        });

        return convertResult(param, pageData, listData);
    }

    @Override
    public void batchUpdate(Consumer<MessageRecipientPO> action, List<Integer> ids) {
        List<MessageRecipientPO> pos = messageRecipientRepository.findAllById(ids);

        Date now = new Date();
        pos.forEach(action.andThen(po -> po.setUpdateTime(now)));

        messageRecipientRepository.saveAll(pos);
    }

}
