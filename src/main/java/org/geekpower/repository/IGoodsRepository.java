package org.geekpower.repository;

import org.geekpower.entity.GoodsPO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IGoodsRepository extends JpaRepository<GoodsPO, Integer> {

}