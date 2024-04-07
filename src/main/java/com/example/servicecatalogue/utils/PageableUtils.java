package com.example.servicecatalogue.utils;


import com.example.servicecatalogue.dtos.pagination.PaginateRequestDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableUtils {
    private PageableUtils () {
        // NOP
    }

    public static Pageable convert(PaginateRequestDTO paginateRequest) {
        // Récupérer les blogs paginés à partir du repository
        Pageable pageable;
        if(paginateRequest.sort() == null || paginateRequest.sort().isEmpty() || paginateRequest.sortDirection() == null){
            pageable = PageRequest.of(paginateRequest.page(), paginateRequest.limit());
        }
        else {
            Sort sort = Sort.by(paginateRequest.sort());
            if(paginateRequest.sortDirection() == Sort.Direction.ASC)
                sort = sort.ascending();
            else
                sort = sort.descending();
            pageable = PageRequest.of(paginateRequest.page(), paginateRequest.limit(), sort);
        }
        return pageable;
    }
}

