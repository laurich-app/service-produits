package com.example.servicecatalogue.dtos.pagination;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;

@Validated
public record PaginateRequestDTO(@DefaultValue("0") int page, @DefaultValue("10") @Max(50) int limit, @Nullable String sort, @DefaultValue("ASC") Sort.Direction sortDirection) {
}
