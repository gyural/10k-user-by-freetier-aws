package org.example.honorsparkingbe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

public class PaginationResponse {
    private long currentPage;
    private long totalPages;
    private long pageSize;
    private long totalItems;
}
