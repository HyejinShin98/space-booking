package com.hyejin.space_booking.util;

import com.hyejin.space_booking.api.response.PageResponse;
import com.hyejin.space_booking.entity.PaginatableRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class PageUtils {
    private PageUtils() {}

    // 전역 기본값
    public static final int DEFAULT_PAGE  = 0;
    public static final int DEFAULT_SIZE  = 20;
    public static final int MAX_SIZE      = 100;

    /** null/음수/과대값 방어하고 Pageable 리턴 */
    public static Pageable resolvePageable(PaginatableRequest req, Sort fallbackSort) {
        int page = (req.page() == null || req.page() < 0) ? DEFAULT_PAGE : req.page();
        int size = (req.size() == null || req.size() <= 0) ? DEFAULT_SIZE : Math.min(req.size(), MAX_SIZE);

        // 네이티브 쿼리에서 정렬은 SQL CASE로 처리한다면 Sort.unsorted()로 두면 됨.
        Sort sort = (fallbackSort == null) ? Sort.unsorted() : fallbackSort;
        return PageRequest.of(page, size, sort);
    }

    /** MyBatis/JdbcTemplate 등에서 쓰는 offset/limit 계산 */
    public static int offset(PaginatableRequest req) {
        int page = (req.page() == null || req.page() < 0) ? DEFAULT_PAGE : req.page();
        int size = (req.size() == null || req.size() <= 0) ? DEFAULT_SIZE : Math.min(req.size(), MAX_SIZE);
        return Math.max(page, 0) * size;
    }

    public static int limit(PaginatableRequest req) {
        int size = (req.size() == null || req.size() <= 0) ? DEFAULT_SIZE : req.size();
        return Math.min(size, MAX_SIZE);
    }

    public static <T> PageResponse<T> toResponse(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}
