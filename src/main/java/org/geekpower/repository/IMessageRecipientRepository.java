package org.geekpower.repository;

import java.util.List;
import java.util.Map;

import org.geekpower.entity.MessageRecipientPO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IMessageRecipientRepository extends JpaRepository<MessageRecipientPO, Integer> {

    List<MessageRecipientPO> queryByMessageId(int messageId);

    @Query(value = "select a.message_id messageId,a.mr_id mrId from ("
            + "SELECT message_id,mr_id, update_time FROM tb_message_recipient WHERE recipient = ?1 AND is_delete = ?2 "
            + "UNION " + //
            "SELECT message_id,0, update_time FROM tb_message WHERE sender = ?1 AND is_delete = ?2 "
            + ") as a order by update_time desc", nativeQuery = true)
    Page<Map<String, Object>> queryDeletedIds(int userId, byte delete, Pageable pageable);

}