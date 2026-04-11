import socket
import threading
from handler import GameHandler

class Server:
    def __init__(self, host='0.0.0.0', port=12345):
        self.host = host
        self.port = port
        self.handler = GameHandler()
        self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1) # cho phep tai su dung port
        self.socket.bind((self.host, self.port))
        self.socket.listen() 
        # Hien thi IP de may khac ket noi
        try:
            local_ip = socket.gethostbyname(socket.gethostname())
        except:
            local_ip = '127.0.0.1'
        print(f'Server is listening on {self.host}:{self.port}')
        print(f'Dia chi IP cua server: {local_ip}:{self.port}')
        print(f'Hay dung IP nay de ket noi tu may khac!')

    # khoi dong server
    def Sever_Start(self):
        while True:
            conn, addr = self.socket.accept()
            threading.Thread(target=self.handler.Handle_client, args=(conn, addr)).start() # tao thread moi de xu ly client qua handler

    def Sever_Stop(self):
        self.socket.close()

if __name__ == "__main__":
    server = Server()
    server.Sever_Start()