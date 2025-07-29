package com.example.libbook.dto;

import java.util.List;

public class PagedResponse<T> {
    private List<T> data;
    private int currentPage;
    private int totalPages;

    public PagedResponse(List<T> data, int currentPage, int totalPages) {
        this.data = data;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
    }

    public List<T> getData() {
        return data;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }
}
