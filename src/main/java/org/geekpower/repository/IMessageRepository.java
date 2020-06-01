package org.geekpower.repository;

import org.geekpower.entity.MessagePO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IMessageRepository extends JpaRepository<MessagePO, Integer> {

}