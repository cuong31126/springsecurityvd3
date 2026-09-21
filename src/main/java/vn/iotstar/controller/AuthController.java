package vn.iotstar.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.iotstar.dto.*;
import vn.iotstar.service.AuthService;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {
        if (error != null) {
            model.addAttribute("error", "Sai username/email hoặc mật khẩu!");
        }
        if (logout != null) {
            model.addAttribute("message", "Bạn đã đăng xuất thành công.");
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("registerDTO", new RegisterDTO());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterDTO dto,
                           BindingResult result,
                           RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "auth/register";
        }
        try {
            authService.register(dto);
            redirect.addFlashAttribute("email", dto.getEmail());
            redirect.addFlashAttribute("success", "Đăng ký thành công. Vui lòng nhập mã OTP gửi tới email.");
            return "redirect:/verify-otp?email=" + dto.getEmail();
        } catch (IllegalArgumentException e) {
            result.reject("register.error", e.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/verify-otp")
    public String verifyOtp(@RequestParam(value = "email", defaultValue = "") String email, Model model) {
        VerifyOtpDTO dto = new VerifyOtpDTO();
        dto.setEmail(email);
        model.addAttribute("verifyOtpDTO", dto);
        return "auth/verify-otp";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@Valid @ModelAttribute VerifyOtpDTO dto,
                            BindingResult result,
                            RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "auth/verify-otp";
        }
        if (!authService.verifyRegister(dto.getEmail(), dto.getOtp())) {
            result.reject("otp.error", "OTP không hợp lệ, đã hết hạn hoặc quá số lần thử.");
            return "auth/verify-otp";
        }
        redirect.addFlashAttribute("success", "Kích hoạt tài khoản thành công! Bạn có thể đăng nhập ngay bây giờ.");
        return "redirect:/login";
    }

    @PostMapping("/resend-register-otp")
    public String resendRegisterOtp(@RequestParam String email, RedirectAttributes redirect) {
        authService.sendRegisterOtp(email);
        redirect.addFlashAttribute("success", "Đã gửi lại mã OTP tới email " + email);
        return "redirect:/verify-otp?email=" + email;
    }

    @GetMapping("/forgot-password")
    public String forgotPassword(Model model) {
        model.addAttribute("forgotPasswordDTO", new ForgotPasswordDTO());
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@Valid @ModelAttribute ForgotPasswordDTO dto,
                                 BindingResult result,
                                 RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "auth/forgot-password";
        }
        try {
            authService.forgotPassword(dto.getEmail());
            redirect.addFlashAttribute("email", dto.getEmail());
            redirect.addFlashAttribute("success", "Mã OTP đặt lại mật khẩu đã được gửi tới email.");
            return "redirect:/reset-password?email=" + dto.getEmail();
        } catch (IllegalArgumentException e) {
            result.reject("forgot.error", e.getMessage());
            return "auth/forgot-password";
        }
    }

    @GetMapping("/reset-password")
    public String resetPassword(@RequestParam(value = "email", defaultValue = "") String email, Model model) {
        ResetPasswordDTO dto = new ResetPasswordDTO();
        dto.setEmail(email);
        model.addAttribute("resetPasswordDTO", dto);
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@Valid @ModelAttribute ResetPasswordDTO dto,
                                BindingResult result,
                                @RequestParam String otp,
                                RedirectAttributes redirect) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            result.reject("password.error", "Mật khẩu xác nhận không khớp.");
        }
        if (result.hasErrors()) {
            return "auth/reset-password";
        }
        if (!authService.verifyResetOtp(dto.getEmail(), otp)) {
            result.reject("otp.error", "Mã OTP không hợp lệ hoặc đã hết hạn.");
            return "auth/reset-password";
        }
        authService.resetPassword(dto.getEmail(), dto.getPassword());
        redirect.addFlashAttribute("success", "Đặt lại mật khẩu thành công! Mời bạn đăng nhập.");
        return "redirect:/login";
    }
}
