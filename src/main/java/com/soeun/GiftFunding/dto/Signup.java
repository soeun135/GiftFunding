package com.soeun.GiftFunding.dto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

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
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {

    }
}
