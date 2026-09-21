package vn.iotstar.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import vn.iotstar.service.EmailService;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendOtp(String email, String otp, String subject) {
        log.info("========== [OTP SENDER] GỬI MÃ OTP TỚI {}: {} ==========", email, otp);
        System.out.println("==================================================");
        System.out.println(">>> [DEV CONSOLE] EMAIL: " + email);
        System.out.println(">>> [DEV CONSOLE] SUBJECT: " + subject);
        System.out.println(">>> [DEV CONSOLE] MÃ OTP CỦA BẠN LÀ: " + otp);
        System.out.println("==================================================");

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject(subject);
            message.setText("Mã xác thực OTP của bạn là: " + otp + "\nMã này có hiệu lực trong 5 phút. Vui lòng không chia sẻ mã này với ai.");
            mailSender.send(message);
        } catch (Exception e) {
            log.warn("Không thể gửi email thực tế qua SMTP (kiểm tra lại cấu hình .env MAIL_USERNAME/PASSWORD). Mã OTP đã hiển thị ở console log trên: {}", e.getMessage());
        }
    }
}
