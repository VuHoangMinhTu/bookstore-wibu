# Wibu BookStore

## Giới thiệu
Wibu BookStore là một ứng dụng webapp được xây dựng với chủ đề là một thương hiệu bán lẻ sách Nhật Bản. Hệ thống cung cấp các tính năng cần thiết trong một nền tảng bán lẻ, bao gồm: Đăng nhập, Đăng ký,Chat, Cổng thanh toán online, Quản lý sản phẩm, Quản lý đơn hàng, Quản lý khiếu nại, Thống kê doanh thu,  ... Hệ thống được thiết kế theo kiến trúc tách biệt giữa Client và Server.


## Kiến trúc hệ thống

### Tổng quan
Hệ thống tuân theo kiến trúc tách biệt (Decoupled Architecture), trong đó frontend và backend được triển khai riêng biệt và giao tiếp với nhau thông qua RESTful APIs. Hệ thống sử dụng Docker để có thể giao tiếp với với các containers như Redis, FastAPI, Elasticsearch, Logstash và sử dụng Tunel Cloudflare để tạo  1 một đường hầm bảo mật để kết nối trực tiếp từ localhost lên hệ thống của Cloudflare, giúp có thể demo được web


## Thành phần hệ thống

### Frontend
- **Công nghệ sử dụng**: React
- **Tên miền**: `https://wibuonline.com`
  
### Backend
- **Công nghệ sử dụng**: Spring Boot, MySQL, Redis
- **Tên miền**: `https://api.wibuonline.com`
- **Cơ sở dữ liệu**: MySQL để lưu trữ dữ liệu, Redis dùng để lưu trữ dữ liệu thô của Cart và toàn bộ cuộc hội thoại của User với AI cũng như là với Admin

### Docker
- Tạo image FastAPI và mô hình LightFm, giúp backend có thể nhận được kết quả dự đoán những cuốn sách phù hợp cho mỗi User nhờ mô hình LightFm
- Tạo image Redis để lưu trữ dữ liệu thô Cart (giúp tốc độ phản hồi giỏ hàng của User nhanh chóng và mượt mà hơn so với lưu lập tức vào MySQL), và dữ liệu chat của User, AI và admin, giúp AI có thể biết được context chat của User để phản hồi cho User tốt hơn
## Test Website
- Ngoài trang https://front-end.minhtu.site/ là giao diện bên phía Khách hàng, thì trang `https://admin.wibuonline.com` là trang của admin dùng để quản lý, mọi người có thể test bằng tk, mk như trang https://front-end.minhtu.site/
