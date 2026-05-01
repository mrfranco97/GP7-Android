package com.example.xplorenow_android.data.network;

import com.example.xplorenow_android.data.model.Experience;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ExperienceResponse {
    @SerializedName("items")
    private List<Experience> items;
    
    @SerializedName("pagination")
    private Pagination pagination;

    public List<Experience> getItems() {
        return items;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public static class Pagination {
        @SerializedName("page")
        private int page;
        @SerializedName("limit")
        private int limit;
        @SerializedName("total_items")
        private int totalItems;
        @SerializedName("total_pages")
        private int totalPages;
        @SerializedName("has_next_page")
        private boolean hasNextPage;
        @SerializedName("has_previous_page")
        private boolean hasPreviousPage;

        public int getPage() { return page; }
        public int getLimit() { return limit; }
        public int getTotalItems() { return totalItems; }
        public int getTotalPages() { return totalPages; }
        public boolean hasNextPage() { return hasNextPage; }
        public boolean hasPreviousPage() { return hasPreviousPage; }
    }
}
