package com.hyejin.space_booking.repository;

import com.hyejin.space_booking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,String> {

    /* 이메일 중복체크 */
    Optional<User> findByEmail(String email);

    /**
     * 아이디로 회원 조회
     *    - useYn 파라미터가 있으면 조건 적용, 없으면 조건 미적용
     *    - UserSns 연동 정보도 함꼐 조회
     */
    @Query("""
           select distinct u
           from User u
           left join fetch u.userSns s
           where u.userId = :userId
             and (:useYn is null or u.useYn = :useYn)
           """)
    Optional<User> findUser(
            @Param("userId") String userId,
            @Param("useYn") String useYn
    );


}
