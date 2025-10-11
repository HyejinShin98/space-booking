package com.hyejin.space_booking.repository;

import com.hyejin.space_booking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSnsRepository extends JpaRepository<User,String> {

}
