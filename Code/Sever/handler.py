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
            return False, 'Error User exists'
        self.users[username] = password
        self.save_users()
        print(f"Registered: {username}")
        return True, 'Register success'

    # xu ly dang nhap
    def Handle_Login(self, username, password):
        if username not in self.users:
            return False, 'Error User not exists'
        if self.users[username] != password:
            return False, 'Error Wrong password'
        print(f"Login: {username}")
        return True, 'Login success'

    # xu ly client
    def Handle_client(self, conn, addr):
        print(f'Connected by {addr}')
        while True:
            try:
                data = conn.recv(1024)
                if not data:
                    break
                data_str = data.decode().strip()
                data_parts = data_str.split()
                if len(data_parts) != 3: # kiem tra lech format, cam khoang trang
                    conn.sendall(b'ERROR: Username and Password cannot contain spaces (expected: COMMAND USERNAME PASSWORD)\n')
                    continue
                command = data_parts[0]
                
                # register
                if command == "REGISTER":
                    username = data_parts[1]
                    password = data_parts[2]
                    success, msg = self.Handle_Register(username, password)
                    conn.sendall((msg + '\n').encode())
                
                # login
                elif command == "LOGIN":
                    username = data_parts[1]
                    password = data_parts[2]
                    success, msg = self.Handle_Login(username, password)
                    conn.sendall((msg + '\n').encode())
                    if success:
                        # Them client vao danh sach cho
                        with self.clients_lock:
                            self.clients.append(conn)
                            while len(self.clients) >= 2:
                                p1 = self.clients.pop(0)
                                p2 = self.clients.pop(0)
                                print("Matching 2 players...")
                                
                                p1_alive = True
                                p2_alive = True
                                
                                try:
                                    p1.sendall(b'START: You are X\n')
                                except:
                                    p1_alive = False
                                    
                                try:
                                    p2.sendall(b'START: You are O\n')
                                except:
                                    p2_alive = False
                                
                                if p1_alive and p2_alive:
                                    print("Matched successfully")
                                    threading.Thread(target=self.handle_game, args=(p1, p2), daemon=True).start()
                                    break
                                else:
                                    # Neu 1 trong 2 bi huy ket noi, dua nguoi con lai vao dau hang cho
                                    if p1_alive:
                                        self.clients.insert(0, p1)
                                    if p2_alive:
                                        self.clients.insert(0, p2)
                        break
                else:
                    print(f'Unknown command from {addr}: {data_str}')
            except Exception as e:
                print(f"Error handling client {addr}: {e}")
                break

    # handle player — relay song song 2 chieu (khong bi ket khi game ket thuc)
    def handle_game(self, p1, p2):
        print("Game start giua 2 player")
        done = threading.Event()

        def relay(src, dst, name):
            try:
                while True:
                    data = src.recv(1024)
                    if not data:
                        break
                    dst.sendall(data)
            except Exception as e:
                print(f"Relay {name} error: {e}")
            finally:
                done.set()

        t1 = threading.Thread(target=relay, args=(p1, p2, "P1->P2"), daemon=True)
        t2 = threading.Thread(target=relay, args=(p2, p1, "P2->P1"), daemon=True)
        t1.start()
        t2.start()

        # Cho den khi 1 ben ngat ket noi
        done.wait()
        try: p1.close()
        except: pass
        try: p2.close()
        except: pass
        print("Game end")
