package com.cmc.zenefitserver.domain.user.dao;

import com.cmc.zenefitserver.domain.user.domain.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDetailRepository extends JpaRepository<UserDetail,Long> {
}
