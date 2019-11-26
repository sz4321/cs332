# Server to recieve files and write to given destination file
# Editors: Sebrina and Nikita

import socket
import select
import argparse
import sys
import json # Used to send array as message

#################################### Constants ####################################
PAYLOAD_SIZE = 1450
HEADER_SIZE = 12
JSON_CONST =  81
ACK_MESSAGE = "_ACK_"
PACKET_SIZE = PAYLOAD_SIZE + HEADER_SIZE + JSON_CONST

#################################### Arguments ####################################
parser = argparse.ArgumentParser(description="Server to recieve files")

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

#################################### Main Logic ####################################
# Define variables
port = args.port
serverSocket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
host = args.hostname

# Start server listening
serverSocket.bind((host, port))
print('Server listening....')

if not args.verbose:
    print('Use -v to turn on verbose')


# Open file to write to
totalRecievedBytes = 0
currentConnectionId = None
lastRecievedPacketId  = -1
with open(args.fileDest, 'wb') as f:
    while True:
        # Recieve data
        data, addr = serverSocket.recvfrom(PACKET_SIZE)

        if args.verbose:
            print('Recieving data from', addr)
        
        # Extract header fields
        packet = json.loads(data)
        connectionId = packet[0]
        fileSize = packet[1]
        currentPacketId = packet[2]
        ACK_flag = packet[3]
        print("packet", currentPacketId, "ACK_flag", ACK_flag)

        # Extract packet payload
        payload = packet[4]
        payloadSize = len(payload)
        
        # Set currentPacketId if this is the first packet and we do not have current connection
        if (currentPacketId == 0 and currentConnectionId == None):
            currentConnectionId = connectionId
        
        # if packet belongs to current connection
        if (currentConnectionId == connectionId):
            if (lastRecievedPacketId + 1 == currentPacketId):
                # Update last recieved 
                lastRecievedPacketId += 1

                if (ACK_flag):
                    # Build ACK message
                    ackPacket = [connectionId, currentPacketId, ACK_MESSAGE]

                    # Send ACK
                    serverSocket.sendto(json.dumps(ackPacket), addr)

                if args.verbose:
                    # TODO: delete this print stmt
                    print("connectionId: ", packet[0], 
                          "fileSize: ", fileSize, 
                          "totalRecievedBytes", totalRecievedBytes,
                          "currentPacketID: ", packet[2], 
                          "Data: ", packet[3], 
                          "Size: ", payloadSize)

                # write data to a file
                f.write(payload)

                # End connection when packet is smasller 
                # than total file size
                totalRecievedBytes += payloadSize
                if totalRecievedBytes == fileSize:
                    break
            else:
                # Build ACK message with last packet ID
                ackPacket = [connectionId, lastRecievedPacketId, ACK_MESSAGE ]

                # Send ACK
                serverSocket.sendto(json.dumps(ackPacket), addr)

                if args.verbose:
                    print("Skipped packet(s), dropping current (ID = " , currentPacketId ,")...")
                    print("Send ACK for" , lastRecievedPacketId)

f.close()
serverSocket.close()
print('Connection closed, wrote file')