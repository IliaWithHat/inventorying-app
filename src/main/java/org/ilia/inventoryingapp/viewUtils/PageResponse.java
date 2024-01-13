package org.ilia.inventoryingapp.viewUtils;

import lombok.Value;
import org.springframework.data.domain.Page;

import java.util.List;

@Value
public class PageResponse<T> {

    List<T> content;
    Metadata metadata;

    public static <T> PageResponse<T> of(Page<T> page) {
        int firstPage;
        int lastPage;
        int currentPage = page.getNumber();
        int totalPages = page.getTotalPages() - 1;  //zero based
        if (totalPages < 9) {
            firstPage = 0;
            lastPage = totalPages;
        } else if (currentPage < 5) {
            firstPage = 0;
            lastPage = 8;
        } else if (totalPages - currentPage < 5) {
            firstPage = totalPages - 8;
            lastPage = totalPages;
        } else {
            firstPage = currentPage - 4;
            lastPage = currentPage + 4;
        }
        Metadata metadata = new Metadata(page.getNumber(), page.getTotalPages(), page.getSize(), page.getTotalElements(), firstPage, lastPage);
        return new PageResponse<>(page.getContent(), metadata);
    }

    @Value
    public static class Metadata {
        int page;
        int totalPages;
        int size;
        long totalElements;
        int firstPage;
        int lastPage;
    }
}
