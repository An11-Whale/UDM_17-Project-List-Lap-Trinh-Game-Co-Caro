import json
import os
import threading
from datetime import datetime

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
DATA_DIR = os.path.abspath(os.path.join(BASE_DIR, '..', 'data'))

if not os.path.exists(DATA_DIR):
    os.makedirs(DATA_DIR)

class GameHandler:
    def __init__(self):
        self.clients = []  # danh sach client
        self.clients_lock = threading.Lock()
        self.users_file = os.path.join(DATA_DIR, 'users.json')  # file luu thong tin user
        self.history_file = os.path.join(DATA_DIR, 'match_history.json')  # file luu lich su tran dau
        self.users = self.load_users()
        self.match_history = self.load_history()

        # Tracking online users
        self.online_users = {}
        self.online_lock = threading.Lock()

        # Tracking pending challenges
        self.pending_challenges = {}
        self.challenge_lock = threading.Lock()

        # Tracking active games
        self.active_games = {}
        self.game_lock = threading.Lock()

        # Mapping conn -> username
        self.conn_to_user = {}

    #  FILE I/O 

    def load_users(self):
        if not os.path.exists(self.users_file):
            return {}
        try:
            with open(self.users_file, 'r', encoding='utf-8') as f:
                data = json.load(f)
                return data if isinstance(data, dict) else {}
        except (json.JSONDecodeError, ValueError):
            return {}

    def save_users(self):
        with open(self.users_file, 'w', encoding='utf-8') as f:
            json.dump(self.users, f, ensure_ascii=False, indent=4)

    def load_history(self):
        if not os.path.exists(self.history_file):
            return []
        try:
            with open(self.history_file, 'r', encoding='utf-8') as f:
                data = json.load(f)
                return data if isinstance(data, list) else []
        except (json.JSONDecodeError, ValueError):
            return []

    def save_history(self):
        with open(self.history_file, 'w', encoding='utf-8') as f:
            json.dump(self.match_history, f, ensure_ascii=False, indent=4)

    #  REGISTER / LOGIN 

    def Handle_Register(self, username, password):
        if username in self.users:
            return False, 'REGISTER_ERROR user_exists'
        self.users[username] = password
        self.save_users()
        print(f"Registered: {username}")
        return True, 'REGISTER_SUCCESS'

    def Handle_Login(self, username, password):
        if username not in self.users:
            return False, 'LOGIN_ERROR user_not_found'
        if self.users[username] != password:
           return False, 'LOGIN_ERROR wrong_password'
        if username in self.online_users:
            return False, 'LOGIN_ERROR Tài khoản này đã đăng nhập ở nơi khác'
        print(f"Login: {username}")
        return True, 'LOGIN_SUCCESS'

    #  CLIENT HANDLER 

    def Handle_client(self, conn, addr):
        print(f'Connected by {addr}')
        buffer = ""
        username = None

        try:
            while True:
                data = conn.recv(1024)
                if not data:
                    break

                buffer += data.decode()

                while "\n" in buffer:
                    line, buffer = buffer.split("\n", 1)
                    result = self.process_command(conn, line.strip(), addr)
                    if result:
                        username = result  # luu username khi login thanh cong

        except Exception as e:
            print(f"Error handling client {addr}: {e}")
        finally:
            # Cleanup khi disconnect
            if username:
                with self.online_lock:
                    if username in self.online_users:
                        del self.online_users[username]
                        print(f"{username} went offline")

                with self.challenge_lock:
                    # Xoa cac challenge lien quan
                    to_remove = [k for k, v in self.pending_challenges.items() if k == username or v == username]
                    for k in to_remove:
                        del self.pending_challenges[k]

                # Neu mat ket noi dang trong tran
                with self.game_lock:
                    game = self.active_games.get(username)
                    if game:
                        try:
                            # Bao cho doi thu biet de thang
                            game["opponent_conn"].sendall(b"OPPONENT_SURRENDERED\n")
                            # Tu dong ghi thua
                            self.process_command(conn, f"GAME_RESULT {game['opponent']} {username} disconnect", addr)
                        except:
                            pass
                        if game["opponent"] in self.active_games:
                            del self.active_games[game["opponent"]]
                        del self.active_games[username]

            if conn in self.conn_to_user:
                del self.conn_to_user[conn]

            with self.clients_lock:
                if conn in self.clients:
                    self.clients.remove(conn)

            try:
                conn.close()
            except:
                pass

    #  COMMAND PROCESSOR 

    def process_command(self, conn, data_str, addr):
        if not data_str:
            return None

        parts = data_str.split()
        command = parts[0]

        #  REGISTER 
        if command == "REGISTER":
            if len(parts) != 3:
                conn.sendall(b'REGISTER_ERROR invalid_format\n')
                return None

            username = parts[1]
            password = parts[2]

            success, msg = self.Handle_Register(username, password)
            if success:
                conn.sendall(b'REGISTER_SUCCESS\n')
            else:
                conn.sendall((msg + '\n').encode())

        #  LOGIN 
        elif command == "LOGIN":
            if len(parts) != 3:
                conn.sendall(b'LOGIN_ERROR invalid_format\n')
                return None

            username = parts[1]
            password = parts[2]

            success, msg = self.Handle_Login(username, password)

            if success:
                conn.sendall(b'LOGIN_SUCCESS\n')

                # Luu vao online users
                with self.online_lock:
                    self.online_users[username] = conn
                self.conn_to_user[conn] = username

                return username  # tra ve username de tracking
            else:
                conn.sendall((msg + '\n').encode())

        #  GET_PLAYERS 
        elif command == "GET_PLAYERS":
            requesting_user = self.conn_to_user.get(conn, "")
            with self.online_lock:
                player_list = [u for u in self.online_users.keys() if u != requesting_user]

            if player_list:
                msg = "PLAYERS_LIST " + ",".join(player_list) + "\n"
            else:
                msg = "PLAYERS_LIST EMPTY\n"
            conn.sendall(msg.encode())

        #  CHALLENGE 
        elif command == "CHALLENGE":
            if len(parts) != 2:
                conn.sendall(b'CHALLENGE_ERROR invalid_format\n')
                return None

            target_user = parts[1]
            from_user = self.conn_to_user.get(conn, "")
            #  CHẶN người gửi nếu đang chơi
            with self.game_lock:
                if from_user in self.active_games:
                    conn.sendall('CHALLENGE_ERROR Bạn đang trong trận đấu\n'.encode())
                    return None
            if not from_user:
                conn.sendall(b'CHALLENGE_ERROR not_logged_in\n')
                return None

            with self.online_lock:
                target_conn = self.online_users.get(target_user)

            if not target_conn:
                conn.sendall('CHALLENGE_ERROR Người chơi ngoại tuyến\n'.encode())
                return None
            #  CHẶN nếu đối thủ đang chơi
            with self.game_lock:
                if target_user in self.active_games:
                    conn.sendall('CHALLENGE_ERROR Người chơi đang trong trận đấu\n'.encode())
                    return None
            # Luu pending challenge
            with self.challenge_lock:
                self.pending_challenges[target_user] = from_user

            # Gui thong bao den target
            try:
                target_conn.sendall(f"CHALLENGE_FROM {from_user}\n".encode())
                print(f"{from_user} challenged {target_user}")
            except:
                conn.sendall(b'CHALLENGE_ERROR send_failed\n')

        #  CANCEL_CHALLENGE 
        elif command == "CANCEL_CHALLENGE":
            if len(parts) != 2:
                return None

            target_user = parts[1]
            from_user = self.conn_to_user.get(conn, "")

            if not from_user:
                return None

            with self.challenge_lock:
                # Xoa challenge neu ton tai
                if target_user in self.pending_challenges and self.pending_challenges[target_user] == from_user:
                    del self.pending_challenges[target_user]
                    print(f"{from_user} cancelled challenge to {target_user}")

                    # Thong bao cho nguoi bi thach dau
                    with self.online_lock:
                        target_conn = self.online_users.get(target_user)
                    if target_conn:
                        try:
                            target_conn.sendall(f"CHALLENGE_CANCELLED {from_user}\n".encode())
                        except:
                            pass

        #  ACCEPT_CHALLENGE 
        elif command == "ACCEPT_CHALLENGE":
            if len(parts) != 2:
                return None

            from_user = parts[1]
            target_user = self.conn_to_user.get(conn, "")

            with self.challenge_lock:
                if target_user not in self.pending_challenges:
                    conn.sendall('CHALLENGE_ERROR Lời mời đã hết hạn\n'.encode())
                    return None
                del self.pending_challenges[target_user]

            with self.online_lock:
                from_conn = self.online_users.get(from_user)

            if not from_conn:
                conn.sendall('CHALLENGE_ERROR Người chơi ngoại tuyến\n'.encode())
                return None

            # CANCEL ALL OTHER PENDING CHALLENGES FOR BOTH PLAYERS
            print("Cancelling other pending challenges...")
            with self.challenge_lock:
                to_remove = []
                for t in list(self.pending_challenges.keys()):
                    c = self.pending_challenges.get(t)
                    if c == from_user or t == target_user:
                        to_remove.append(t)
                        # Notify challenger
                        ch_conn = self.online_users.get(c)
                        if ch_conn:
                            try:
                                ch_conn.sendall(f"CHALLENGE_DECLINED {t}\n".encode())
                            except:
                                pass
                        # Notify target
                        t_conn = self.online_users.get(t)
                        if t_conn:
                            try:
                                t_conn.sendall(f"CHALLENGE_DECLINED {c}\n".encode())
                            except:
                                pass
                        print(f"Cancelled {c} -> {t}")

                for t in to_remove:
                    self.pending_challenges.pop(t, None)

            # Bat dau game session
            print(f"Challenge accepted: {from_user} vs {target_user}")
            try:
                from_conn.sendall(b'CHALLENGE_ACCEPTED\n')
                from_conn.sendall(f'START 1 {target_user}\n'.encode())
                conn.sendall(f'START 2 {from_user}\n'.encode())

                with self.game_lock:
                    self.active_games[from_user] = {"opponent_conn": conn, "my_id": 1, "opponent": target_user}
                    self.active_games[target_user] = {"opponent_conn": from_conn, "my_id": 2, "opponent": from_user}

            except Exception as e:
                print(f"Error starting challenge game: {e}")

        #  DECLINE_CHALLENGE 
        elif command == "DECLINE_CHALLENGE":
            if len(parts) != 2:
                return None

            from_user = parts[1]
            target_user = self.conn_to_user.get(conn, "")

            with self.challenge_lock:
                if target_user in self.pending_challenges:
                    del self.pending_challenges[target_user]

            with self.online_lock:
                from_conn = self.online_users.get(from_user)

            if from_conn:
                try:
                    from_conn.sendall(f"CHALLENGE_DECLINED {target_user}\n".encode())
                    print(f"{target_user} declined challenge from {from_user}")
                except:
                    pass

        #  MOVE 
        elif command == "MOVE":
            username = self.conn_to_user.get(conn, "")
            with self.game_lock:
                game = self.active_games.get(username)
            
            if game:
                try:
                    row = int(parts[1])
                    col = int(parts[2])
                    player_id = game["my_id"]
                    
                    # check hoat dong binh thuong
                    
                    if 0 <= row < 15 and 0 <= col < 15:
                        msg_to_send = f"MOVE {row} {col} {player_id}\n"
                        # Gui cho ca 2 player de update UI dong bo
                        conn.sendall(msg_to_send.encode())
                        game["opponent_conn"].sendall(msg_to_send.encode())
                except Exception as e:
                    print(f"Lỗi khi gửi lệnh MOVE: {e}")

        #  SURRENDER 
        elif command == "SURRENDER":
            username = self.conn_to_user.get(conn, "")
            with self.game_lock:
                game = self.active_games.get(username)
            if game:
                # Gui thong bao cho doi thu
                try:
                    game["opponent_conn"].sendall(b"OPPONENT_SURRENDERED\n")
                except:
                    pass
                self.process_command(conn, f"GAME_RESULT {game['opponent']} {username} surrender", addr)

        #  GAME_RESULT 
        elif command == "GAME_RESULT":
            # Format: GAME_RESULT winner loser reason
            if len(parts) >= 3:
                winner = parts[1]
                loser = parts[2]
                reason = parts[3] if len(parts) > 3 else "normal"

                record = {
                    "winner": winner,
                    "loser": loser,
                    "reason": reason,
                    "date": datetime.now().strftime("%d/%m/%Y %H:%M")
                }
                
                self.match_history.append(record)
                self.save_history()
                print(f"Game result saved: {winner} beat {loser} ({reason})")

                # Xoa khoi danh sach dang choi
                with self.game_lock:
                    if winner in self.active_games:
                        del self.active_games[winner]
                    if loser in self.active_games:
                        del self.active_games[loser]

        #  GET_HISTORY 
        elif command == "GET_HISTORY":
            requesting_user = self.conn_to_user.get(conn, "")
            user_history = []
            for record in self.match_history:
                if record["winner"] == requesting_user or record["loser"] == requesting_user:
                    is_win = record["winner"] == requesting_user
                    opponent = record["loser"] if is_win else record["winner"]
                    result = "win" if is_win else "loss"
                    reason = record.get("reason", "normal")
                    date = record.get("date", "")
                    user_history.append(f"{opponent}|{result}|{date}|{reason}")

            if user_history:
                msg = "HISTORY_DATA " + ";".join(user_history) + "\n"
            else:
                msg = "HISTORY_DATA EMPTY\n"
            conn.sendall(msg.encode())

        else:
            print(f'Unknown command from {addr}: {data_str}')

        return None