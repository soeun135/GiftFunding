package com.soeun.GiftFunding.dto;

import com.soeun.GiftFunding.entity.User;
import com.soeun.GiftFunding.type.Authority;
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
        private String userName;

        @NotBlank(message = "전화번호는 필수 항목입니다.")
        private String phone;

        @Email(message = "이메일 형식에 맞춰야합니다.")
        private String email;

        @NotBlank(message = "비밀번호는 필수 항목입니다.")
        private String password;

        @NotBlank(message = "주소는 필수 항목입니다.")
        private String address;

        @NotBlank(message = "생일은 필수 항목입니다.")
        private String birthDay;

        public boolean validBirthDay() {
            String format = "yyyyMMdd";
            if (this.birthDay.length() != 8) {
                return false;
            }
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                sdf.setLenient(false);
                sdf.parse(this.birthDay);
            } catch (ParseException e) {
                return false;
            }
            return true;
        }

        public User toEntity() {
            return User.builder()
                .userName(this.getUserName())
                .phone(this.getPhone())
                .email(this.getEmail())
                .password(this.getPassword()) //인코딩해서 넣어야함
                .address(this.getAddress())
                .birthDay(LocalDate.parse(this.getBirthDay()))
                .role(Authority.ROLE_USER)
                .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private String userName;
        private String message;
        public static Response toResponse(String userName) {

            return Response.builder()
                .userName(userName)
                .message("님 회원가입이 완료되었습니다.")
                .build();
        }
    }
}
