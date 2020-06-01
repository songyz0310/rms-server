package org.geekpower.repository;

import org.geekpower.entity.UserPO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserRepository extends JpaRepository<UserPO, Integer> {

	UserPO findByAccount(String account);

}