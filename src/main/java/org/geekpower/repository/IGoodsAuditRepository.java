package org.geekpower.repository;

import org.geekpower.entity.GoodsAuditPO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IGoodsAuditRepository extends JpaRepository<GoodsAuditPO, Integer> {

}