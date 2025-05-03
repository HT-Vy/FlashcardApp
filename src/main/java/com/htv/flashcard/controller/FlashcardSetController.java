package com.htv.flashcard.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.htv.flashcard.DTO.FlashcardBatchDTO;
import com.htv.flashcard.DTO.FlashcardDTO;
import com.htv.flashcard.DTO.FlashcardSetDTO;
import com.htv.flashcard.DTO.FlashcardSetDetailDTO;
import com.htv.flashcard.model.FlashcardSet;
import com.htv.flashcard.model.User;
import com.htv.flashcard.repository.FlashcardSetRepository;
import com.htv.flashcard.service.FlashcardSetService;
import com.htv.flashcard.service.UserService;

@RestController
@RequestMapping("/api/sets")
public class FlashcardSetController {
    @Autowired
    private FlashcardSetService flashcardSetService;


    @Autowired
    private UserService userService;

    @Autowired
    private FlashcardSetRepository flashcardSetRepository;

    /**
     * Tạo bộ flashcard mới
     */
    @PostMapping
    public ResponseEntity<?> createSet(@RequestBody FlashcardSetDTO dto,
                                       @AuthenticationPrincipal UserDetails ud) {
        User u = userService.findByEmail(ud.getUsername()).orElseThrow();
        FlashcardSet s = new FlashcardSet(); s.setTitle(dto.getTitle()); s.setDescription(dto.getDescription());
        return ResponseEntity.ok(flashcardSetService.createSet(s, u));
    }


    /**
     * Tìm kiếm bộ flashcard theo từ khóa
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchSets(@RequestParam String keyword) {
        return ResponseEntity.ok(flashcardSetService.searchSets(keyword));
    }

    /**
     * Cập nhật bộ flashcard
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSet(@PathVariable Long id, @RequestBody FlashcardSetDTO dto, @AuthenticationPrincipal UserDetails ud) {
        // 1. Lấy user hiện tại
        User currentUser = userService.findByEmail(ud.getUsername())
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        // 2. Lấy bộ flashcard cần update
        FlashcardSet existingSet = flashcardSetService.getSetById(id);

        // 3. Kiểm tra quyền: chỉ creator mới được phép
        if (!existingSet.getUser().getId().equals(currentUser.getId())) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Bạn không có quyền cập nhật bộ flashcard này");
        }
        return ResponseEntity.ok(flashcardSetService.updateSet(id, dto));
    }

    /**
     * Xóa bộ flashcard
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSet(@PathVariable Long id, @AuthenticationPrincipal UserDetails ud) {
        // 1. Lấy user hiện tại
        User currentUser = userService.findByEmail(ud.getUsername())
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        // 2. Lấy bộ flashcard cần xóa
        FlashcardSet existingSet = flashcardSetService.getSetById(id);

        // 3. Kiểm tra quyền: chỉ creator mới được phép
        if (!existingSet.getUser().getId().equals(currentUser.getId())) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Bạn không có quyền xóa bộ flashcard này");
        }
        flashcardSetService.deleteSet(id);
        return ResponseEntity.ok("Xóa flashcard set thành công");
    }

    /**
     * GET /api/sets
     * Lấy danh sách tất cả bộ FlashcardSet của user hiện tại
     */
    @GetMapping
    public ResponseEntity<List<FlashcardSetDTO>> getAllSets(@AuthenticationPrincipal UserDetails ud) {
        User u = userService.findByEmail(ud.getUsername()).orElseThrow();
        List<FlashcardSetDTO> list = u.getFlashcardSets().stream().map(fs -> {
            FlashcardSetDTO d = new FlashcardSetDTO();
            d.setTitle(fs.getTitle()); d.setDescription(fs.getDescription()); return d;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    /**
     * GET /api/sets/{id}
     * Lấy chi tiết một bộ FlashcardSet (nếu cần)
     */
    @GetMapping("/{id}")
    public ResponseEntity<FlashcardSetDetailDTO> getSetById(@PathVariable Long id) {
        FlashcardSet s = flashcardSetService.getSetById(id);
        FlashcardSetDetailDTO dto = new FlashcardSetDetailDTO();
        dto.setId(s.getId()); dto.setTitle(s.getTitle()); dto.setDescription(s.getDescription());
        dto.setFlashcards(s.getFlashcards().stream().map(f -> {
            FlashcardDTO fd = new FlashcardDTO(); fd.setFrontContent(f.getFrontContent()); fd.setBackContent(f.getBackContent()); return fd;
        }).collect(Collectors.toList()));
        return ResponseEntity.ok(dto);
    }

     @PostMapping("/{setId}/flashcards/batch")
    public ResponseEntity<?> addFlashcardsBatch(
            @PathVariable Long setId,
            @RequestBody FlashcardBatchDTO batchDto,
            @AuthenticationPrincipal UserDetails ud    // lấy user đã login
    ) {
        // 1. Lấy entity User từ email trong token
        User user = userService.findByEmail(ud.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user"));

        // 2. Load FlashcardSet để kiểm tra quyền
        FlashcardSet set = flashcardSetRepository.findById(setId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy bộ flashcard"));

        // 3. Nếu không phải owner thì trả 403
        if (!set.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Bạn không có quyền thêm flashcard vào bộ này");
        }

        // 4. Gọi service để xử lý lưu batch
        flashcardSetService.addFlashcardsBatch(setId, batchDto.getFlashcards());

        return ResponseEntity.ok("Thêm flashcards thành công");
    }
}

