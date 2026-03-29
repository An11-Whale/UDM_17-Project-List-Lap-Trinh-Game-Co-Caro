import socket
import threading
import json
import os
import uuid

BOARD_SIZE = 15
WIN_COUNT = 5

class Server:
    def __init__(self, host='localhost', port=12345):
        self.host = host
        self.port = port
        self.clients = []  # hang doi cho match
        self.clients_lock = threading.Lock()
        self.logged_in = {}  # conn -> username
        self.users_file = 'users.json'
        self.users = self.load_users()
        self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.socket.bind((self.host, self.port))
        self.socket.listen()
        print(f'Server is listening on {self.host}:{self.port}')

    def load_users(self):
        if not os.path.exists(self.users_file):
            return {}
        with open(self.users_file, 'r', encoding='utf-8') as f:
            return json.load(f)

    def save_users(self):
        with open(self.users_file, 'w', encoding='utf-8') as f:
            json.dump(self.users, f, ensure_ascii=False, indent=4)

    def Sever_Register(self, username, password):
        if username in self.users:
            return False, 'Error User exists'
        self.users[username] = password
        self.save_users()
        print(f"Registered: {username}")
        return True, 'Register success'

    def Sever_Login(self, username, password):
        if username not in self.users:
            return False, 'Error User not exists'
        if self.users[username] != password:
            return False, 'Error Wrong password'
        print(f"Login: {username}")
        return True, 'Login success'

    def Sever_Start(self):
        while True:
            conn, addr = self.socket.accept()
            threading.Thread(target=self.Sever_Handle_client, args=(conn, addr), daemon=True).start()

    def Sever_Handle_client(self, conn, addr):
        print(f'Connected by {addr}')
        try:
            while True:
                data = conn.recv(1024)
                if not data:
                    break
                data_str = data.decode().strip()
                data_parts = data_str.split()
                if len(data_parts) < 3:
                    conn.sendall(b'ERROR: Invalid command format (expected: COMMAND USERNAME PASSWORD)\n')
                    continue
                command = data_parts[0]

                if command == "REGISTER":
                    username = data_parts[1]
                    password = data_parts[2]
                    success, msg = self.Sever_Register(username, password)
                    conn.sendall((msg + '\n').encode())

                elif command == "LOGIN":
                    username = data_parts[1]
                    password = data_parts[2]
                    success, msg = self.Sever_Login(username, password)
                    conn.sendall((msg + '\n').encode())
                    if success:
                        self.logged_in[conn] = username
                        with self.clients_lock:
                            self.clients.append(conn)
                            if len(self.clients) >= 2:
                                p1 = self.clients.pop(0)
                                p2 = self.clients.pop(0)
                                room_id = str(uuid.uuid4())[:8]
                                print(f"Matching 2 players in room {room_id}")
                                p1_name = self.logged_in.get(p1, "?")
                                p2_name = self.logged_in.get(p2, "?")
                                # Gui JSON thay vi string
                                start_msg_p1 = json.dumps({
                                    "type": "START",
                                    "role": "X",
                                    "room_id": room_id,
                                    "opponent": p2_name
                                }) + '\n'
                                start_msg_p2 = json.dumps({
                                    "type": "START",
                                    "role": "O",
                                    "room_id": room_id,
                                    "opponent": p1_name
                                }) + '\n'
                                p1.sendall(start_msg_p1.encode())
                                p2.sendall(start_msg_p2.encode())
                                threading.Thread(target=self.handle_game, args=(p1, p2, room_id), daemon=True).start()
                        break
                else:
                    conn.sendall(b'ERROR: Unknown command\n')
        except (ConnectionResetError, BrokenPipeError):
            print(f"Client {addr} disconnected unexpectedly")
        finally:
            # Xu ly disconnect: remove khoi hang doi neu chua match
            with self.clients_lock:
                if conn in self.clients:
                    self.clients.remove(conn)
                    print(f"Removed disconnected client {addr} from queue")
            if conn in self.logged_in:
                del self.logged_in[conn]

    def handle_game(self, p1, p2, room_id):
        print(f"Game start in room {room_id}")
        board = [['' for _ in range(BOARD_SIZE)] for _ in range(BOARD_SIZE)]
        current_turn = 'X'  # X di truoc
        players = {'X': p1, 'O': p2}

        def send_json(conn, data):
            try:
                conn.sendall((json.dumps(data) + '\n').encode())
                return True
            except (ConnectionResetError, BrokenPipeError, OSError):
                return False

        def check_win(board, row, col, symbol):
            directions = [(0, 1), (1, 0), (1, 1), (1, -1)]
            for dr, dc in directions:
                count = 1
                for d in [1, -1]:
                    r, c = row + dr * d, col + dc * d
                    while 0 <= r < BOARD_SIZE and 0 <= c < BOARD_SIZE and board[r][c] == symbol:
                        count += 1
                        r += dr * d
                        c += dc * d
                if count >= WIN_COUNT:
                    return True
            return False

        def is_draw(board):
            for row in board:
                for cell in row:
                    if cell == '':
                        return False
            return True

        try:
            while True:
                current_conn = players[current_turn]
                current_conn.settimeout(300)  # timeout 5 phut
                try:
                    data = current_conn.recv(1024)
                except socket.timeout:
                    # Het thoi gian -> thua
                    other = 'O' if current_turn == 'X' else 'X'
                    send_json(players[current_turn], {"type": "GAME_OVER", "result": "TIMEOUT", "winner": other})
                    send_json(players[other], {"type": "GAME_OVER", "result": "WIN", "winner": other})
                    break

                if not data:
                    # Disconnect -> doi thu thang
                    other = 'O' if current_turn == 'X' else 'X'
                    send_json(players[other], {"type": "GAME_OVER", "result": "OPPONENT_DISCONNECTED", "winner": other})
                    break

                data_str = data.decode().strip()
                try:
                    move = json.loads(data_str)
                except json.JSONDecodeError:
                    send_json(current_conn, {"type": "ERROR", "message": "Invalid JSON format"})
                    continue

                # Validate nuoc di
                row = move.get("row")
                col = move.get("col")

                if row is None or col is None:
                    send_json(current_conn, {"type": "ERROR", "message": "Missing row or col"})
                    continue

                if not (0 <= row < BOARD_SIZE and 0 <= col < BOARD_SIZE):
                    send_json(current_conn, {"type": "ERROR", "message": "Out of board"})
                    continue

                if board[row][col] != '':
                    send_json(current_conn, {"type": "ERROR", "message": "Cell already taken"})
                    continue

                # Dat nuoc di
                board[row][col] = current_turn

                move_msg = {
                    "type": "MOVE",
                    "row": row,
                    "col": col,
                    "symbol": current_turn
                }

                # Gui cho ca 2 player
                send_json(p1, move_msg)
                send_json(p2, move_msg)

                # Check thang
                if check_win(board, row, col, current_turn):
                    game_over_msg = {"type": "GAME_OVER", "result": "WIN", "winner": current_turn}
                    send_json(p1, game_over_msg)
                    send_json(p2, game_over_msg)
                    print(f"Room {room_id}: {current_turn} wins!")
                    break

                # Check hoa
                if is_draw(board):
                    draw_msg = {"type": "GAME_OVER", "result": "DRAW"}
                    send_json(p1, draw_msg)
                    send_json(p2, draw_msg)
                    print(f"Room {room_id}: Draw!")
                    break

                # Doi luot
                current_turn = 'O' if current_turn == 'X' else 'X'

        except Exception as e:
            print(f"Game error in room {room_id}: {e}")
        finally:
            p1.close()
            p2.close()
            print(f"Game end in room {room_id}")

    def Sever_Stop(self):
        self.socket.close()

if __name__ == "__main__":
    server = Server()
    server.Sever_Start()
