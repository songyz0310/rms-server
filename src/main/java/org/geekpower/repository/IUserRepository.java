package org.geekpower.repository;

import org.geekpower.entity.UserPO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserRepository extends JpaRepository<UserPO, Integer> {

    UserPO queryByAccount(String account);

    Page<UserPO> queryUserByUserNameLike(String name, Pageable pageable);

}