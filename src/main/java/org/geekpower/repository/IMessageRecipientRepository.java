package org.geekpower.repository;

import java.util.List;

import org.geekpower.entity.MessageRecipientPO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IMessageRecipientRepository extends JpaRepository<MessageRecipientPO, Integer> {

    List<MessageRecipientPO> queryByMessageId(int messageId);

}