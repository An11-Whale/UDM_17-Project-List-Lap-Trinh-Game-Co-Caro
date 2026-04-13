
import json
import os
import threading

class GameHandler:
    def __init__(self):
        self.clients = [] # them danh sach client cho vao match
        self.clients_lock = threading.Lock()
        self.users_file = 'users.json' # file luu thong tin user
        self.users = self.load_users()

    # luu thong tin user vao file
    def load_users(self):
        if not os.path.exists(self.users_file):
            return {}
        with open(self.users_file, 'r', encoding='utf-8') as f:
            return json.load(f)

    def save_users(self):
        with open(self.users_file, 'w', encoding='utf-8') as f:
            json.dump(self.users, f, ensure_ascii=False, indent=4)

    # xu ly dang ky
    def Handle_Register(self, username, password):
        if username in self.users:
            return False, 'REGISTER_ERROR user_exists'
        self.users[username] = password
        self.save_users()
        print(f"Registered: {username}")
        return True, 'REGISTER_SUCCESS'

    # xu ly dang nhap
    def Handle_Login(self, username, password):
        if username not in self.users:
            return False, 'LOGIN_ERROR user_not_found'
        if self.users[username] != password:
           return False, 'LOGIN_ERROR wrong_password'
        print(f"Login: {username}")
        return True, 'LOGIN_SUCCESS'

    # xu ly client
    def Handle_client(self, conn, addr):
        print(f'Connected by {addr}')
        buffer = ""

        while True:
            try:
                data = conn.recv(1024)
                if not data:
                    break

                buffer += data.decode()

                while "\n" in buffer:
                    line, buffer = buffer.split("\n", 1)
                    self.process_command(conn, line.strip(), addr)

            except Exception as e:
                print(f"Error handling client {addr}: {e}")
                break
            finally:
                with self.clients_lock:
                    if conn in self.clients:
                        self.clients.remove(conn)

                try:
                    conn.close()
                except:
                    pass
    
    def process_command(self, conn, data_str, addr):
        if not data_str:
            return

        parts = data_str.split()
        command = parts[0]

        # REGISTER
        if command == "REGISTER":
            if len(parts) != 3:
                conn.sendall(b'REGISTER_ERROR invalid_format\n')
                return

            username = parts[1]
            password = parts[2]

            success, msg = self.Handle_Register(username, password)
            if success:
                conn.sendall(b'REGISTER_SUCCESS\n')
            else:
                conn.sendall((msg + '\n').encode())

        # LOGIN
        elif command == "LOGIN":
            if len(parts) != 3:
                conn.sendall(b'LOGIN_ERROR invalid_format\n')
                return

            username = parts[1]
            password = parts[2]

            success, msg = self.Handle_Login(username, password)

            if success:
                conn.sendall(b'LOGIN_SUCCESS\n')

                # matchmaking
                with self.clients_lock:
                    self.clients.append(conn)

                    while len(self.clients) >= 2:
                        p1 = self.clients.pop(0)
                        p2 = self.clients.pop(0)

                        print("Matching 2 players...")

                        try:
                            p1.sendall(b'START 1\n')
                            p2.sendall(b'START 2\n')

                            print("Matched successfully")

                            threading.Thread(
                                target=self.handle_game,
                                args=(p1, p2),
                                daemon=True
                            ).start()

                            break

                        except:
                            print("Match failed, retrying...")

            else:
                conn.sendall((msg + '\n').encode())

        else:
            print(f'Unknown command from {addr}: {data_str}')

    # handle player — relay song song 2 chieu (khong bi ket khi game ket thuc)
    def handle_game(self, p1, p2):
        print("Game start giua 2 player")
        done = threading.Event()

        def relay(src, dst, player_id):
            try:
                while True:
                    data = src.recv(1024)
                    if not data:
                        break

                    msg = data.decode().strip()

                    if msg.startswith("MOVE"):
                        parts = msg.split()
                        #check format
                        if len(parts) != 3:
                            continue

                        try:
                            row = int(parts[1])
                            col = int(parts[2])
                        except:
                            continue

                        # check boundary (15x15)
                        if row < 0 or row >= 15 or col < 0 or col >= 15:
                            continue

                        new_msg = f"MOVE {row} {col} {player_id}\n"

                        # gửi cho cả 2 player
                        try:
                            src.sendall(new_msg.encode())
                            dst.sendall(new_msg.encode())
                        except:
                            break

            except Exception as e:
                print(f"Relay error: {e}")
            finally:
                done.set()

        t1 = threading.Thread(target=relay, args=(p1, p2, 1))
        t2 = threading.Thread(target=relay, args=(p2, p1, 2))
        t1.start()
        t2.start()

        # Cho den khi 1 ben ngat ket noi
        done.wait()
        try: p1.close()
        except: pass
        try: p2.close()
        except: pass
        print("Game end")
