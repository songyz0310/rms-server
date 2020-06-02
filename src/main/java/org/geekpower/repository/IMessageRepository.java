package org.geekpower.repository;

import org.geekpower.entity.MessagePO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IMessageRepository extends JpaRepository<MessagePO, Integer> {

    @Query(value = "select a.message_id from ("
            + "SELECT message_id, update_time FROM tb_message_recipient WHERE recipient = ?1 AND is_delete = ?2 "
            + "UNION " + //
            "SELECT message_id, update_time FROM tb_message WHERE sender = ?1 AND is_delete = ?2 "
            + ") as a ", nativeQuery = true)
    Page<Integer> queryDeletedIds(int userId, byte delete, Pageable pageable);

    @Query(value = "select a.message_id from ("
            + "SELECT message_id, update_time FROM tb_message_recipient WHERE recipient = ?1 AND is_rubbish=?2 AND is_delete = ?3 "
            + "UNION "
            + "SELECT message_id, update_time FROM tb_message WHERE sender = ?1 AND is_rubbish=?2 AND is_delete = ?3 "
            + ") as a ", nativeQuery = true)
    Page<Integer> queryRubbishIds(int userId, byte rubbish, byte delete, Pageable pageable);

}