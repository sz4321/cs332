# Client to send file to another client user server
# Edited by: Nikita Sietsema and Sebrina Zeleke
# Date: 11.19.19

import select
import socket
import sys
import argparse
import os, errno

# set up commands for user
parser = argparse.ArgumentParser(description="A prattle client")

parser.add_argument("-s", "--server", dest="server", default="127.0.0.1",
                    help="server hostname or IP address (default: 127.0.0.1)")
parser.add_argument("-p", "--port", dest="port", type=int, default=12345,
                    help="UDP port the server is listening on (default 12345)")
parser.add_argument("-f", "--filename", dest="filename", default='text.txt',
                    help="write filename include extension to send to other client")

# Optional verbose for more info
parser.add_argument("-v", "--verbose", action="store_true", dest="verbose",
                    help="turn verbose output on")
args = parser.parse_args()


####################################  Main Logic ####################################
PAYLOAD_SIZE = 10
ACK_SIZE = 5
ACK_MESSAGE = "_ACK_"

if not args.verbose:
    print('Use -v to turn on verbose')

try:
    # Create socket
    clientSocket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    if args.verbose:
        print('Created clientSocket')

    # Connect to server
    clientSocket.connect((args.server, args.port))
    if args.verbose:
        print('Connected to server on port', args.port)

    # Open file to be sent
    filename=args.filename
    fileSrc = open(filename,'rb')

    # Read data into packets
    data = fileSrc.read(PAYLOAD_SIZE)
    while (data):
        # Send packet to server
        clientSocket.send(data)
        dataSize = len(data)

        if args.verbose:
            print('Data: ', repr(data), "Size: ", dataSize)
        
        ## Wait for ACK
        while True:
            data, addr = clientSocket.recvfrom(ACK_SIZE)
            if (addr[0] == args.server and data == ACK_MESSAGE):
                if args.verbose:
                    print(ACK_MESSAGE)
                break
            # else:
                # add to timeout time
                # if timeout time is  > MAX_TIMEOUT
                # resend packet
                


        # If packet is less than PACKET_SIZE then 
        # close file and break sending loop
        if dataSize < PAYLOAD_SIZE:
            fileSrc.close()
            break

        # Get next packet
        data = fileSrc.read(PAYLOAD_SIZE)

    print('Done sending')
    clientSocket.close()
    

#     # Set up infinite loop to listen for messages and talk
#     while True: 
#         try:
#             # Make input source be clientSocket
#             inputs, outputs, errors = select.select([clientSocket, sys.stdin], [sys.stdout], [])

#             for i in inputs:
#                 # Handle keyboard input
#                 if i == sys.stdin:
#                     if args.verbose:
#                         print('\tverbose: Just recieved input from keyboard and sent a message')
#                     message = args.name + "> " + sys.stdin.readline()
#                     clientSocket.send(message.encode())
#                     print('')

#                 # Handle message recieved
#                 elif i == clientSocket:
#                     if args.verbose:
#                         print('\tverbose: Just recieved a message from socket')
#                     text = clientSocket.recv(100)
#                     if text == "":
#                         raise RuntimeError
#                     print(text)

#         # Handle errors gracefully
#         except KeyboardInterrupt:
#             print("Thanks for chatting, goodbye")
#             clientSocket.close()
#             break
#         except RuntimeError:
#             print("Houston we have a problem: \n\tLost connection to server, goodbye")
#             clientSocket.close()
#             break
#         except errno:
#             print(os.strerror(errno.errocode))
#             clientSocket.close()
#             break
#         except:
#             print("Unknown error")
#             clientSocket.close()
#             break
except:
    print("Could not connect to server.")
    clientSocket.close()