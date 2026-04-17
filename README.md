# Game Cờ Caro Online (Client - Server)

Đây là dự án lập trình ứng dụng Game Cờ Caro nhiều người chơi (Multiplayer) theo mô hình Client-Server. Người chơi có thể tạo tài khoản, đăng nhập, xem danh sách người chơi trực tuyến, thách đấu và chơi cờ với nhau thông qua mạng.

##  Các tính năng chính

* **Quản lý tài khoản**: Đăng ký và đăng nhập tài khoản. Thông tin người dùng được lưu trữ an toàn trên server.
* **Sảnh chờ (Lobby)**: 
  * Hiển thị danh sách các người chơi đang online.
  * Hiển thị trạng thái của từng người chơi (Online, Đang trong trận, Đang được mời).
* **Thách đấu**: 
  * Gửi lời mời thách đấu đến người chơi khác.
  * Nhận, chấp nhận hoặc từ chối lời mời thách đấu.
* **Gameplay**:
  * Bàn cờ Caro truyền thống (15x15).
  * Đồng hồ đếm ngược thời gian suy nghĩ cho mỗi lượt đi.
  * Có chức năng "Đầu hàng" trong trận đấu.
  * Tự động xử lý thắng thua khi có người chơi ngắt kết nối (Disconnect).
* **Lịch sử đấu**: Xem lại lịch sử các trận đấu đã diễn ra (thắng/thua, thời gian, lý do kết thúc trận).

##  Công nghệ sử dụng

* **Client**:
  * Ngôn ngữ: Java
  * Giao diện: Java Swing (Sử dụng NetBeans GUI Builder)
  * Mạng: `java.net.Socket` (TCP Sockets)
* **Server**:
  * Ngôn ngữ: Python 3
  * Mạng: Thư viện `socket`, xử lý đa luồng với `threading`
  * Lưu trữ dữ liệu: JSON (`users.json` cho tài khoản, `match_history.json` cho lịch sử đấu)

##  Cấu trúc thư mục

```text
UDM_17-Project-List-Lap-Trinh-Game-Co-Caro/
├── Code/
│   ├── Client/             # Mã nguồn Java cho Client (GUI, Mạng, Logic Game)
│   │   ├── gui/            # Giao diện người dùng (LoginUI, LobbyUI, BoardUI, TimerUI...)
│   │   ├── Network/        # Xử lý kết nối Socket phía Client
│   │   ├── Game/           # Logic bàn cờ và quản lý trò chơi
│   │   ├── challenge/      # UI và logic thách đấu
│   │   └── history/        # UI và logic hiển thị lịch sử
│   ├── Server/             # Mã nguồn Python cho Server
│   │   ├── server.py       # File khởi chạy Server chính
│   │   └── handler.py      # Xử lý logic các luồng, command từ Client
│   └── data/               # Thư mục chứa dữ liệu JSON (được tạo tự động bởi Server)
├── DOCX/                   # Tài liệu báo cáo dự án
├── PPTX/                   # Slide thuyết trình
└── README.md               # File thông tin dự án
```

##  Hướng dẫn cài đặt và chạy ứng dụng

### 1. Chạy Server (Python)
1. Cần cài đặt **Python 3.x** trên máy tính.
2. Mở Terminal/Command Prompt, di chuyển đến thư mục `Code/Server/`.
3. Chạy lệnh:
   ```bash
   python server.py
   ```
4. Server sẽ hiển thị địa chỉ IP và Port (mặc định: 12345). Hãy ghi nhớ địa chỉ IP này để Client kết nối.

### 2. Chạy Client (Java)
1. Mở Project Client trong IDE hỗ trợ Java (như NetBeans, IntelliJ IDEA, hoặc Eclipse).
2. Tìm `gui/LoginUI.java` và chạy file `LoginUI.java`.
3. Đảm bảo cấu hình IP kết nối tới Server trong mã nguồn Client khớp với IP của máy chủ đang chạy `server.py`.
4. Run/Compile Client. Bạn có thể chạy nhiều Client cùng lúc để test tính năng chơi 2 người.

##  Lưu ý
* Game thiết kế dựa trên các luật cơ bản của Cờ Caro.
* Khi một người chơi thoát đột ngột (tắt app), server sẽ tự động thông báo đối thủ đầu hàng và ghi nhận kết quả.
* Project này được tạo dưới dạng bài tập/đồ án môn học, có các file tài liệu đính kèm (DOCX, PPTX) hỗ trợ quá trình báo cáo.
