# Client to handle I/O messages on client end
# Edited by: Nikita Sietsema and Sebrina Zeleke
# Date: 10.22.19

import select
import socket
import sys
import argparse
import os, errno
import curses

# set up commands for user
parser = argparse.ArgumentParser(description="A prattle client")

parser.add_argument("-n", "--name", dest="name", help="name to be prepended in messages (default: machine name)")
parser.add_argument("-s", "--server", dest="server", default="127.0.0.1",
                    help="server hostname or IP address (default: 127.0.0.1)")
parser.add_argument("-p", "--port", dest="port", type=int, default=12345,
                    help="TCP port the server is listening on (default 12345)")
parser.add_argument("-v", "--verbose", action="store_true", dest="verbose",
                    help="turn verbose output on")
args = parser.parse_args()

try:
    # Create socket
    clientSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    if args.verbose:
        print '\tverbose: created clientSocket'

    # Connect to server
    clientSocket.connect((args.server, args.port))
    if args.verbose:
        print '\tverbose: connected to server on port', args.port

    print "Houston we are live!"

    # Set up infinite loop to listen for messages and talk
    while True: 
        try:
            # Make input source be clientSocket
            inputs, outputs, errors = select.select([clientSocket, sys.stdin], [sys.stdout], [])

            for i in inputs:
                # Handle keyboard input
                if i == sys.stdin:
                    if args.verbose:
                        print '\tverbose: Just recieved input from keyboard and sent a message'
                    message = args.name + "> " + sys.stdin.readline()
                    clientSocket.send(message.encode())
                    print ''

                # Handle message recieved
                elif i == clientSocket:
                    if args.verbose:
                        print '\tverbose: Just recieved a message from socket'
                    text = clientSocket.recv(100)
                    if text == "":
                        raise RuntimeError
                    print text

        # Handle errors gracefully
        except KeyboardInterrupt:
            print "Thanks for chatting, goodbye"
            clientSocket.close()
            break
        except RuntimeError:
            print "Houston we have a problem: \n\tLost connection to server, goodbye"
            clientSocket.close()
            break
        except errno:
            print os.strerror(errno.errocode)
            clientSocket.close()
            break
        except:
            print "Unknown error"
            clientSocket.close()
            break
except:
    print "Could not connect to server."
    clientSocket.close()