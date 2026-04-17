
import argparse
import random
import socket
import threading
import time

# Thông số mặc định
DEFAULT_HOST = 'localhost'
DEFAULT_PORT = 12345
DEFAULT_NUM_CLIENTS = 500
DEFAULT_DELAY = 0.1
DEFAULT_HOLD = 2.0
DEFAULT_MOVES = 10

lock = threading.Lock()
results = {
    'connected': 0,
    'errors': 0,
    'total_time': 0.0,
    'latencies': [],
}


def safe_print(message):
    with lock:
        print(message)


def mock_client(client_id, args):
    start_time = time.perf_counter()
    sock = None
    try:
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.settimeout(5)
        sock.connect((args.host, args.port))
        safe_print(f"Client {client_id} connected")

        username = f"TestUser{client_id}"
        sock.send(f"LOGIN {username}\n".encode())
        time.sleep(0.2)

        sock.send("CHALLENGE random\n".encode())
        time.sleep(0.2)

        for _ in range(args.moves):
            row = random.randint(0, 14)
            col = random.randint(0, 14)
            sock.send(f"MOVE {row} {col}\n".encode())
            time.sleep(args.delay)

        if args.hold > 0:
            time.sleep(args.hold)

        safe_print(f"Client {client_id} disconnected")
        with lock:
            results['connected'] += 1
    except Exception as e:
        safe_print(f"Client {client_id} error: {e}")
        with lock:
            results['errors'] += 1
    finally:
        if sock:
            try:
                sock.close()
            except Exception:
                pass
        elapsed = time.perf_counter() - start_time
        with lock:
            results['total_time'] += elapsed
            results['latencies'].append(elapsed)


def parse_args():
    parser = argparse.ArgumentParser(description='Stress test và performance test server Caro')
    parser.add_argument('--host', default=DEFAULT_HOST, help='Địa chỉ server')
    parser.add_argument('--port', type=int, default=DEFAULT_PORT, help='Port server')
    parser.add_argument('--clients', type=int, default=DEFAULT_NUM_CLIENTS, help='Số client giả lập')
    parser.add_argument('--delay', type=float, default=DEFAULT_DELAY, help='Delay giữa các hành động (giây)')
    parser.add_argument('--hold', type=float, default=DEFAULT_HOLD, help='Thời gian giữ kết nối sau khi gửi các lệnh')
    parser.add_argument('--moves', type=int, default=DEFAULT_MOVES, help='Số lệnh MOVE gửi mỗi client')
    parser.add_argument('--start-delay', type=float, default=0.05, help='Delay giữa khởi tạo các kết nối client')
    return parser.parse_args()


def main():
    args = parse_args()
    threads = []
    test_start = time.perf_counter()

    for i in range(args.clients):
        t = threading.Thread(target=mock_client, args=(i, args))
        threads.append(t)
        t.start()
        time.sleep(args.start_delay)

    for t in threads:
        t.join()

    test_end = time.perf_counter()
    duration = test_end - test_start
    connected = results['connected']
    errors = results['errors']
    total = connected + errors
    avg_latency = sum(results['latencies']) / total if total else 0.0
    safe_print('\n=== Performance test summary ===')
    safe_print(f'Total clients attempted: {total}')
    safe_print(f'Successful clients: {connected}')
    safe_print(f'Failed clients: {errors}')
    safe_print(f'Total time elapsed: {duration:.2f} seconds')
    safe_print(f'Average client latency: {avg_latency:.2f} seconds')
    safe_print(f'Throughput: {total / duration:.2f} clients/sec')
    safe_print('===============================')


if __name__ == '__main__':
    main()
