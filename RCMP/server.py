# Server to recieve files and write to given destination file
# Editors: Sebrina and Nikita

import socket
import select
import argparse
import sys

parser = argparse.ArgumentParser(description="A prattle server")

parser.add_argument("-p", "--port", dest="port", type=int, default=12345,
                    help="UDP port the server is listening on (default 12345)")
parser.add_argument("-f", "--fileDest", dest="fileDest", default='result.txt',
                    help="filename include extension to write results to")

# optional for feedback
parser.add_argument("-v", "--verbose", action="store_true", dest="verbose",
                    help="turn verbose output on")

# optional specified host
parser.add_argument("-host", "--hostname", dest="hostname", default="127.0.0.1",
                    help="server hostname or IP address (default: 127.0.0.1)")

args = parser.parse_args()

####################################  Main Logic ####################################
# Store variables
PACKET_SIZE = 10
port = args.port
serverSocket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
host = args.hostname

# Start server listening
serverSocket.bind((host, port))
print('Server listening....')

if not args.verbose:
    print('Use -v to turn on verbose')


# Open file to write to
with open(args.fileDest, 'wb') as f:
    while True:
        # Recieve data
        data, addr = serverSocket.recvfrom(PACKET_SIZE)
        if args.verbose:
            print('Recieving data from', addr)
        dataSize = len(data)

        # Print info if verbose is true
        if args.verbose:
            print('Data: ', (data), "Size: ", dataSize)

        # write data to a file
        f.write(data)

        # End connection when packet is smaller 
        # than packet size
        if dataSize < PACKET_SIZE:
            print('what is happening')
            break

f.close()
serverSocket.close()
print('Connection closed, wrote file')