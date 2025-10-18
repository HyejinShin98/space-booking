package com.hyejin.space_booking.repository;

import com.hyejin.space_booking.entity.UserSns;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserSnsRepository extends JpaRepository<UserSns,Long> {

    /**
     * provider + providerId로 회원 조회
     * provider   : 소셜명
     * providerId : 소셜 Id (실제아이디가 아닌 숫자 형식의 id값)
     */
    @Query("""
            select distinct s
            from UserSns s
            left join fetch s.user u
            where s.provider = :provider
              and s.providerUserId = :providerUserId
            """)
    Optional<UserSns> findByProviderAndProviderUserId(
            @Param("provider") String provider,
            @Param("providerUserId") String providerUserId
    );

}
