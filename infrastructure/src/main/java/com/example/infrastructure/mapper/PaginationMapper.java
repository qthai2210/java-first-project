package com.example.infrastructure.mapper;

import com.example.application.dto.PageDataDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PaginationMapper {

    private PaginationMapper() {
        // Utility class
    }

    /**
     * Chuyển đổi từ Spring Data Page<Entity> sang Domain PageDataDto<DTO>
     */
    public static <E, D> PageDataDto<D> map(Page<E> page, Function<E, D> converter) {
        List<D> data = page.getContent().stream()
                .map(converter)
                .collect(Collectors.toList());

        PageDataDto<D> pageData = new PageDataDto<>();
        pageData.setData(data);
        pageData.setPage(page.getNumber());
        pageData.setSize(page.getSize());
        pageData.setTotalElements(page.getTotalElements());
        pageData.setTotalPages(page.getTotalPages());
        pageData.setHasNext(page.hasNext());
        pageData.setHasPrevious(page.hasPrevious());

        return pageData;
    }
}
