import socket
import threading
import json
import os

class Server:
    def __init__(self, host='localhost', port=12345):
        self.host = host
        self.port = port
        self.clients = [] #them danh sach client
        self.clients_lock = threading.Lock()
        self.users_file = 'users.json' #file luu thong tin user
        self.users = self.load_users() 
        self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.socket.bind((self.host, self.port))
        self.socket.listen() 
        print(f'Server is listening on {self.host}:{self.port}')
    #luu thong tin user vao file
    def load_users(self):
        if not os.path.exists(self.users_file):
            return {}
        with open(self.users_file, 'r', encoding='utf-8') as f:
            return json.load(f)
    def save_users(self):
        with open(self.users_file, 'w', encoding='utf-8') as f:
            json.dump(self.users, f, ensure_ascii=False, indent=4)
    #xu ly dang ky
    def Sever_Register(self, username, password):
        if username in self.users:
            return False, 'Error User exists'
        self.users[username] = password
        self.save_users()
        print(f"Registered: {username}")
        return True, 'Register success'
    #xu ly dang nhap
    def Sever_Login(self, username, password):
        if username not in self.users:
            return False, 'Error User not exists'
        if self.users[username] != password:
            return False, 'Error Wrong password'
        print(f"Login: {username}")
        return True, 'Login success'
    # khoi dong server
    def Sever_Start(self):
        while True:
            conn, addr = self.socket.accept()
            threading.Thread(target=self.Sever_Handle_client, args=(conn, addr)).start()# tao thread moi de xu ly client
    # xu ly client
    def Sever_Handle_client(self, conn, addr):
            print(f'Connected by {addr}')
            while True:
                data = conn.recv(1024)
                if not data:
                    break
                data_str = data.decode().strip()
                data_parts = data_str.split()
                if len(data_parts) < 3: # kiem tra dinh dang lenh
                    conn.sendall(b'ERROR: Invalid command format (expected: COMMAND USERNAME PASSWORD)\n')
                    continue
                command = data_parts[0]
                #register
                if command == "REGISTER":
                    username = data_parts[1]
                    password = data_parts[2]
                    success, msg = self.Sever_Register(username, password)
                    conn.sendall((msg + '\n').encode())
                #login
                elif command == "LOGIN":
                    username = data_parts[1]
                    password = data_parts[2]
                    success, msg = self.Sever_Login(username, password)
                    conn.sendall((msg + '\n').encode())
                    if success:
                        # Them client vao danh sach cho
                        with self.clients_lock:
                            self.clients.append(conn)
                            if len(self.clients) >= 2:
                                p1 = self.clients.pop(0)
                                p2 = self.clients.pop(0)
                                print("Matching 2 players")
                                p1.sendall(b'START: You are X\n')
                                p2.sendall(b'START: You are O\n')
                                threading.Thread(target=self.handle_game, args=(p1, p2), daemon=True).start()
                        break
                else:
                    print(f'Unknown command from {addr}: {data_str}')
    # handle player
    def handle_game(self, p1, p2):
        print("Game start giua 2 player")
        try:
            while True:
                data = p1.recv(1024)
                if not data: break
                p2.sendall(data)
                data = p2.recv(1024)
                if not data: break
                p1.sendall(data)
        except Exception as e:
            print(f"Game error: {e}")
        finally:
            p1.close()
            p2.close()
            print("Game end")
    def Sever_Stop(self):
        self.socket.close()
if __name__ == "__main__":
    server = Server()
    server.Sever_Start()