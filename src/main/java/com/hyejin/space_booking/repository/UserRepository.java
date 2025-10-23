package com.hyejin.space_booking.repository;

import com.hyejin.space_booking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    /** 이메일로 회원 조회 */
    Optional<User> findByEmail(String email);
    /** 회원아이디 존재여부 */
    boolean existsByUserId(String userId);
    /** 회원이메일 존재여부 */
    boolean existsByEmail(String email);

    /**
     * key로 회원 조회
     *  - UserSns 연동 정보도 함께 조회
     */
    @Query("""
           select distinct u
           from User u
           left join fetch u.userSns s
           where u.id = :userKey
           """)
    Optional<User> findUserByKey(@Param("userKey") Long userKey);

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

    /**
     * 이메일로 회원 소셜연동 정보 조회
     *      - useYn 'Y'일 경우
     *      - 소셜연동 정보 있는 회원만 조회
     */
    @Query("""
          select distinct u
          from User u
          join fetch u.userSns s
          where u.useYn = 'Y'
            and u.email is not null
            and u.email = :email
        """)
    Optional<User> findActiveWithSnsByEmail(@Param("email") String email);


}
