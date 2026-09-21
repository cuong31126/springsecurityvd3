# Dự án Ví Dụ 3: Spring Boot 4 + Spring Security + OtpToken + Cloudinary + Product & User Management

Dự án mẫu triển khai theo tài liệu **"HƯỚNG DẪN SPRING BOOT + SECURITY"** (55 trang), sử dụng kiến trúc MVC chuẩn với đầy đủ tính năng xác thực OTP, phân quyền, quản lý sản phẩm với Cloudinary, quản lý người dùng với phân trang & tìm kiếm.

---

## 1. Công nghệ Sử dụng

- **Java Version:** 21 (Tương thích tốt với JDK 22)
- **Framework:** Spring Boot 4.1.1 & Spring Security 7.x
- **Database:** Microsoft SQL Server (Database riêng: `webst3`)
- **ORM:** Spring Data JPA / Hibernate 7
- **Template Engine:** Thymeleaf + Thymeleaf Layout Dialect (`nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect`)
- **Mapper:** MapStruct 1.6.3
- **Cloud Storage:** Cloudinary SDK (`cloudinary-http5:2.0.0`)
- **Mail Service:** Spring Boot Starter Mail (Tích hợp cơ chế log console mã OTP khi chạy local)
- **Port:** `8082` (không trùng với VD1 8080 và VD2 8081)

---

## 2. Tính năng Chính

1. **Xác thực & Bảo mật (Authentication & Security):**
   - **Đăng ký (Register):** Tự động tạo tài khoản với trạng thái `INACTIVE` và gửi mã OTP 6 số qua email.
   - **Xác thực OTP (Verify OTP):** Nhập mã OTP để kích hoạt tài khoản sang `ACTIVE`. Hỗ trợ nút gửi lại OTP (`Resend OTP`).
   - **Đăng nhập (Login):** Lưu phiên làm việc (Session), hỗ trợ đăng nhập bằng username hoặc email.
   - **Quên mật khẩu (Forgot Password):** Gửi OTP qua email để xác thực và đổi mật khẩu mới.
   - **Đổi mật khẩu (Reset Password):** Xác nhận mật khẩu mới kết hợp OTP.
2. **Quản lý Sản phẩm (Product Management):**
   - CRUD sản phẩm (Thêm, sửa, xóa, xem danh sách).
   - Tìm kiếm theo tên sản phẩm và mô tả.
   - Phân trang dữ liệu.
   - Upload hình ảnh sản phẩm trực tiếp lên Cloudinary.
   - Mỗi sản phẩm gắn liền với người dùng tạo ra (Quan hệ 1-N).
3. **Quản lý Người dùng (User Management - Dành cho ROLE_ADMIN):**
   - Xem danh sách người dùng với phân trang và tìm kiếm.
   - Đếm tổng số người dùng và đếm số lượng sản phẩm của từng người dùng.
   - Thêm, sửa thông tin người dùng và phân quyền `ROLE_ADMIN` / `ROLE_USER`.
   - Bật/tắt trạng thái kích hoạt tài khoản (`enabled`).

---

## 3. Cấu hình Môi trường & Cơ sở Dữ liệu

### 3.1. Cơ sở dữ liệu SQL Server
Dự án sử dụng database `webst3` trên SQL Server. 
Database và các bảng (`users`, `roles`, `products`, `otp_tokens`) đã được thiết lập tự động bởi Hibernate `ddl-auto=update`.

### 3.2. Cấu hình `.env`
Tạo file `.env` tại thư mục gốc của dự án (đã có sẵn file mẫu [`.env.example`](.env.example)):
```properties
# DATABASE
DB_URL=jdbc:sqlserver://localhost:1433;databaseName=webst3;encrypt=false;trustServerCertificate=true;sslProtocol=TLSv1.2;characterEncoding=UTF-8
DB_USERNAME=sa
DB_PASSWORD=your_password

# SERVER
SERVER_PORT=8082

# MAIL SMTP (Tùy chọn)
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_app_password

# CLOUDINARY (Tùy chọn)
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret
```

> **Lưu ý bảo mật:** File `.env` chứa mật khẩu đã được đưa vào `.gitignore` để tránh bị lộ khi push lên GitHub.

---

## 4. Tài khoản Mặc định Hệ thống

Hệ thống tự động khởi tạo dữ liệu ban đầu khi chạy ứng dụng lần đầu:
- **Tài khoản Admin:**
  - Username: `admin`
  - Password: `admin123`
  - Role: `ROLE_ADMIN`
  - Trạng thái: `ACTIVE`

---

## 5. Hướng dẫn Khởi chạy & Kiểm thử

### Cách 1: Chạy bằng Maven Wrapper
```bash
./mvnw spring-boot:run
```

### Cách 2: Chạy trong Spring Tool Suite (STS) / Eclipse
- Import dự án dạng **Existing Maven Projects**.
- Nhấp chuột phải vào dự án -> **Run As** -> **Spring Boot App**.

### Truy cập ứng dụng:
- Trang chủ: [http://localhost:8082/](http://localhost:8082/)
- Đăng nhập: [http://localhost:8082/login](http://localhost:8082/login)
- Đăng ký: [http://localhost:8082/register](http://localhost:8082/register)
- Quản lý sản phẩm: [http://localhost:8082/products](http://localhost:8082/products)
- Quản lý người dùng: [http://localhost:8082/users](http://localhost:8082/users) (Yêu cầu đăng nhập tài khoản `admin`)
