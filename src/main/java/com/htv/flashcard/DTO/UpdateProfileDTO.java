package com.htv.flashcard.DTO;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class UpdateProfileDTO {
    private String fullName;
    private MultipartFile avatar;      // có thể null nếu user không đổi ảnh
}
