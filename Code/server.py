import socket
import threading
class Server:
    def __init__(self, host='localhost', port=12345):
        self.host = host
        self.port = port
        self.clients = [] #them danh sach client
        self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.socket.bind((self.host, self.port))
        self.socket.listen()
        print(f'Server is listening on {self.host}:{self.port}')
    # khoi dong server
    def Sever_Start(self):
        #while True:
            #conn, addr = self.socket.accept()
            #threading.Thread(target=self.Sever_Handle_client, args=(conn, addr)).start()# tao thread moi de xu ly client
        while True:
            conn, addr = self.socket.accept()
            print(f'Connected: {addr}')
            self.clients.append(conn)

            #neu du 2 player thi matching
            if len(self.clients) >= 2:
                player1 = self.clients.pop(0)
                player2 = self.clients.pop(0)
                print("Matching")
                #gui role x o
                player1.sendall(b"START_X\n")
                player2.sendall(b"START_O\n")

                #threading xu li 2 player
                threading.Thread(
                    target=self.handle_game,
                    args=(player1, player2)
                ).start()
    # handle player
    def handle_game(self, p1, p2):
        print("Game start giua 2 player")
        try:
            while True:
                data = p1.recv(1024)
                if not data:
                    break
                print("P1 -> P2:", data.decode().strip())
                p2.sendall(data)
        except:
            print("Game end")
        finally:
            p1.close()
            p2.close()
    # xu ly client
    def Sever_Handle_client(self, conn, addr):
        with conn:
            print(f'Connected by {addr}')
            while True:
                data = conn.recv(1024)
                if not data:
                    break
            
                data_str = data.decode().strip()
                data_parts = data_str.split()
                command = data_parts[0]
                if command == "REGISTER":
                    username = data_parts[1]
                    password = data_parts[2]
                    self.Sever_Register(username, password)
                else:
                    print(f'Unknown command from {addr}: {data_str}')
    def Sever_Stop(self):
        self.socket.close()
    #xu ly dang ky
    def Sever_Register(self, username, password):
        print(f"Registering user: {username} with password: {password}")
        pass
if __name__ == "__main__":
    server = Server()
    server.Sever_Start()