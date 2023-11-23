package com.soeun.GiftFunding.dto;

import com.soeun.GiftFunding.entity.User;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class Signup {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        @NotBlank(message = "이름은 필수 항목입니다.")
        private String name;

        @NotBlank(message = "전화번호는 필수 항목입니다.")
        private String phone;

        @NotBlank(message = "이메일은 필수 항목입니다.")
        @Email(message = "이메일 형식에 맞춰야합니다.")
        private String email;

        @NotBlank(message = "비밀번호는 필수 항목입니다.")
        private String password;

        @NotBlank(message = "주소는 필수 항목입니다.")
        private String address;

        @NotBlank(message = "생일은 필수 항목입니다.")
        private String birthDay;

        public User toEntity() {
            return User.builder()
                .name(this.getName())
                .phone(this.getPhone())
                .email(this.getEmail())
                .password(this.getPassword()) //인코딩해서 넣어야함
                .address(this.getAddress())
                .birthDay(LocalDate.parse(this.getBirthDay()))
                .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private String name;
        private String message;
        public static Response toResponse(String name) {

            return Response.builder()
                .name(name)
                .message("님 회원가입이 완료되었습니다.")
                .build();
        }
    }
}