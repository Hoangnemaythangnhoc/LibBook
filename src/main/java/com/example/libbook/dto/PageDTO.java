package com.example.libbook.dto;

import java.util.List;

public class PageDTO<T> {
    private List<T> data;
    private int currentPage;
    private int totalPages;

    public PageDTO(List<T> data, int currentPage, int totalPages) {
        this.data = data;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
    }

    // Getter Setter
    public List<T> getData() { return data; }
    public void setData(List<T> data) { this.data = data; }
    public int getCurrentPage() { return currentPage; }
    public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }
    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
}
