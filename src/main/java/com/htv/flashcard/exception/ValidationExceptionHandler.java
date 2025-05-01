package com.htv.flashcard.exception;

import java.util.HashMap;
import java.util.Map;
import org.springframework.lang.NonNull;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

// Bạn có thể đang dùng @ControllerAdvice + @ResponseBody, nhưng @RestControllerAdvice gộp luôn
@RestControllerAdvice
public class ValidationExceptionHandler extends ResponseEntityExceptionHandler {

//     /**
//      * Bắt tất cả lỗi validation do @Valid gây ra và trả về message rõ ràng.
//      */
//     @ExceptionHandler(MethodArgumentNotValidException.class)
//     public ResponseEntity<String> handleValidation(MethodArgumentNotValidException ex) {
//         // Lấy kết quả binding chứa danh sách lỗi
//         BindingResult bindingResult = ex.getBindingResult();
//         String errMsg;

//         // Ưu tiên lỗi của field đầu tiên nếu có
//         if (!bindingResult.getFieldErrors().isEmpty()) {
//             errMsg = bindingResult.getFieldErrors()
//                        .get(0)
//                        .getDefaultMessage();
//         }
//         // Nếu không có field error, lấy lỗi global đầu tiên
//         else if (!bindingResult.getGlobalErrors().isEmpty()) {
//             errMsg = bindingResult.getGlobalErrors()
//                        .get(0)
//                        .getDefaultMessage();
//         }
//         // Dự phòng khi không xác định được lỗi cụ thể
//         else {
//             errMsg = "Dữ liệu đầu vào không hợp lệ";
//         }

//         // Trả về HTTP 400 với message lỗi
//         return ResponseEntity
//                 .status(HttpStatus.BAD_REQUEST)
//                 .body(errMsg);
//     }
// }
// --- Xử lý validation của @Valid, @NotBlank, v.v. ---
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
         @NonNull MethodArgumentNotValidException ex, 
         @NonNull HttpHeaders headers, 
         @NonNull HttpStatusCode status, 
         @NonNull WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        // Trả về 400 Bad Request kèm map field->message
        return ResponseEntity.badRequest().body(errors);
    }

    // --- Thêm handler cho BadCredentialsException ---
    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            org.springframework.security.authentication.BadCredentialsException ex) {

        // Tự tạo DTO lỗi trả về
        ErrorResponse err = new ErrorResponse(
            HttpStatus.UNAUTHORIZED.value(),
            "Email hoặc mật khẩu không đúng"
        );
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(err);
    }

    // --- (Nếu cần) Bắt luôn AccessDeniedException, JwtException, v.v. ---
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            org.springframework.security.access.AccessDeniedException ex) {
        ErrorResponse err = new ErrorResponse(
            HttpStatus.FORBIDDEN.value(),
            "Bạn không có quyền truy cập tài nguyên này"
        );
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(err);
    }

    // DTO trả lỗi chung
    public static class ErrorResponse {
        private int status;
        private String message;

        public ErrorResponse(int status, String message) {
            this.status = status;
            this.message = message;
        }
        public int getStatus() { return status; }
        public String getMessage() { return message; }
    }
}