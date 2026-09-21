package vn.iotstar.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
public class CustomErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object uri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        Throwable exception = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);

        String detail = "Đã xảy ra lỗi hoặc bạn không có quyền truy cập trang này.";
        if (exception != null && exception.getMessage() != null && !exception.getMessage().isBlank()) {
            detail = exception.getMessage();
        } else if (message != null && !message.toString().isBlank()) {
            detail = message.toString();
        }

        log.error(">>> [ERROR OCCURRED] Status: {}, URI: {}, Message: {}", status, uri, detail);
        if (exception != null) {
            log.error(">>> Stacktrace:", exception);
        }

        model.addAttribute("status", status != null ? status : 500);
        model.addAttribute("message", detail);
        model.addAttribute("uri", uri);
        return "error";
    }
}
