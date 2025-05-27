package com.htv.flashcard.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminSetSummaryDTO {
    private Long id;
    private String title;
    private int savedCount;    // số người đã lưu
    private int cardCount;     // số thẻ trong set
    private String ownerName; // tên tác giả
    private boolean visible;   // trạng thái ẩn/hiện
    private Double averageRating;
}
