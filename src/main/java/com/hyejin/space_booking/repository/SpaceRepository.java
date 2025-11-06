package com.hyejin.space_booking.repository;

import com.hyejin.space_booking.api.response.SpaceInfoResponse;
import com.hyejin.space_booking.entity.Space;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SpaceRepository extends JpaRepository<Space,Long> {

    interface Row {
        Long getSpaceId();
        String getTitle();
        String getAddress();
        String getImageUrl();
        Integer getCapacity();
        Integer getMinPriceNumeric();
        String getMinPrice();
        String getRegDate();
    }

    /**
     * 검색조건으로 공간 목록 조회
     * 결과 - 공간id, 공간제목, 공간이미지, 공간주소, 수용인원, 공간최소가격
     * 1. keyword : title, description, address
     * 2. option : A:전체, T:제목, D:설명, R:주소
     * 3. day_of_week : 해당 요일을 운영하는 공간이면 출력
     * 4. time : 검색한 시간이 시작시간과 종료시간 사이에 있을 경우 출력
     */
    @Query(value = """
      SELECT *
      FROM (
          SELECT
              s.space_id    AS spaceId,
              s.title       AS title,
              s.address     AS address,
              s.image_url   AS imageUrl,
              s.capacity    AS capacity,
              MIN(ss.price) AS minPriceNumeric,
              COALESCE(CONCAT(FORMAT(MIN(ss.price),0),'원'),'0원') AS minPrice,
              COALESCE(DATE_FORMAT(MAX(s.reg_date), '%Y년 %m월 %d일'), '-') AS regDate
          FROM space s
          JOIN space_slot ss
            ON ss.space_id = s.space_id
          WHERE
              s.use_yn = 'Y'
              AND ( :dayOfWeek IS NULL OR :dayOfWeek = '' OR ss.day_of_week = :dayOfWeek )
              AND ( :time IS NULL OR :time = '' OR :time BETWEEN ss.start_time AND ss.end_time )
              AND ( :capacity IS NULL OR :capacity = 0 OR s.capacity >= :capacity )
              AND (
                  :keyword IS NULL OR :keyword = '' OR
                  CASE :option
                      WHEN 'T' THEN LOWER(COALESCE(s.title, ''))
                      WHEN 'D' THEN LOWER(COALESCE(s.description, ''))
                      WHEN 'R' THEN LOWER(COALESCE(s.address, ''))
                      ELSE LOWER(CONCAT(
                          COALESCE(s.title, ''), ' ',
                          COALESCE(s.description, ''), ' ',
                          COALESCE(s.address, '')
                      ))
                  END LIKE CONCAT('%', LOWER(:keyword), '%')
              )
          GROUP BY s.space_id, s.title, s.address, s.image_url, s.capacity
      ) x
      ORDER BY
         CASE WHEN :sort = 'price_asc'  THEN x.minPriceNumeric END ASC,
         CASE WHEN :sort = 'price_desc' THEN x.minPriceNumeric END DESC,
         CASE WHEN :sort = 'recent'     THEN x.regDate  END DESC,
         x.spaceId ASC
      """,
      countQuery = """
      SELECT COUNT(DISTINCT s.space_id)
      FROM space s
      JOIN space_slot ss ON ss.space_id = s.space_id
      WHERE
          s.use_yn = 'Y'
          AND ( :dayOfWeek IS NULL OR :dayOfWeek = '' OR ss.day_of_week = :dayOfWeek )
          AND ( :time IS NULL OR :time = '' OR :time BETWEEN ss.start_time AND ss.end_time )
          AND ( :capacity IS NULL OR :capacity = 0 OR s.capacity >= :capacity )
          AND (
              :keyword IS NULL OR :keyword = '' OR
              CASE :option
                  WHEN 'T' THEN LOWER(COALESCE(s.title, ''))
                  WHEN 'D' THEN LOWER(COALESCE(s.description, ''))
                  WHEN 'R' THEN LOWER(COALESCE(s.address, ''))
                  ELSE LOWER(CONCAT(
                      COALESCE(s.title, ''), ' ',
                      COALESCE(s.description, ''), ' ',
                      COALESCE(s.address, '')
                  ))
              END LIKE CONCAT('%', LOWER(:keyword), '%')
          )
      """,
    nativeQuery = true)
    Page<Row> findSpacesBySearchCondition(
            @Param("keyword") String keyword,
            @Param("option") String option,
            @Param("dayOfWeek") String dayOfWeek,
            @Param("time") String time,
            @Param("capacity") Integer capacity,
            @Param("sort") String sort,
            Pageable pageable
    );


    interface DetailRow {
        Long getSpaceId();
        String getTitle();
        String getDescription();
        Integer getCapacity();
        String getContact();
        String getAddress();
        String getImageUrl();
        String getTime();
        String getRegDate();
        Integer getMinPriceNumeric();
        String getMinPrice();
    }

    /**
     * spaceId로 공간 상세 조회
     */
    @Query(value = """
         SELECT
              s.space_id                                         AS spaceId,
              s.title                                            AS title,
              s.description                                      AS description,
              s.capacity                                         AS capacity,
              s.contact                                          AS contact,
              s.address                                          AS address,
              s.image_url                                        AS imageUrl,
              CONCAT(CONCAT(s.open_time, ':00 ~ '), CONCAT(s.close_time, ':00')) as time,
              DATE_FORMAT(s.reg_date,  '%Y년 %m월 %d일')          AS regDate,
              CAST(COALESCE(MIN(ss.price), 0) AS SIGNED)       AS minPriceNumeric,
              COALESCE(CONCAT(FORMAT(MIN(ss.price), 0), '원'), '0원') AS minPrice
          FROM space s
          LEFT JOIN space_slot ss
            ON ss.space_id = s.space_id
          WHERE 1=1
            AND s.space_id = :spaceId
          GROUP BY
              s.space_id, s.title, s.description, s.capacity, s.contact,
              s.address, s.image_url, s.open_time, s.close_time, s.reg_date
           """, nativeQuery = true)
    Optional<DetailRow> findSpace(@Param("spaceId") Long spaceId);

}
