# Server to recieve files and write to given destination file
# Authors: Sebrina Zeleke and Nikita Sietsema

import socket
import select
import argparse
import sys
from struct import *
import StringIO

#################################### Constants ####################################
PAYLOAD_SIZE = 1450
HEADER_SIZE = 13 # 4 bytes connectionID + 4 bytes fileSize + 4 bytes currentPacketID + 1 byte ACK_flag
ACK_MESSAGE = "_ACK_"
PACKET_SIZE = PAYLOAD_SIZE + HEADER_SIZE

PACKET_FORMAT = "IIIB1450s" # I = 4 byte int, B = 1 byte int, 1450s = data size
ACK_FORMAT = "II5s"         # I = 4 byte int, 5s = s byte string

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
        packet = unpack(PACKET_FORMAT, data)
        connectionId = packet[0]
        fileSize = packet[1]
        currentPacketId = packet[2]
        ACK_flag = packet[3]
        print("currentPacketId", currentPacketId)

        # Extract packet payload
        payload = packet[4]
        payloadSize = len(payload)

        # Set currentConnectionId if this is the first packet and we do not have current connection
        if (currentPacketId == 0 and currentConnectionId == None):
            currentConnectionId = connectionId

        # if packet belongs to current connection
        if (currentConnectionId == connectionId):

            # If recieved packet is the next consecutive packet
            if (lastRecievedPacketId + 1 == currentPacketId):

                # Update last recieved 
                lastRecievedPacketId += 1
                if (ACK_flag):
                    # Send ACK
                    ###################################################################################################
                    ###################################################################################################
                    ###################################################################################################
                    ###################################################################################################
                    ###################################################################################################
                    ###################################################################################################
                    if not ((totalRecievedBytes + payloadSize) >= fileSize): # TODO: REMOVE THIS IF!!!!!! ONLY TO CHECK LOST FINAL ACK LOGIC!!!
                        print("Got inside not last")
                        serverSocket.sendto(pack(ACK_FORMAT, connectionId, currentPacketId, ACK_MESSAGE), addr)

                # End connection when packet is smaller 
                # than total file size
                totalRecievedBytes += payloadSize
                if totalRecievedBytes >= fileSize:
                    # Remove extra bytes from payload
                    actualDataSize = PAYLOAD_SIZE - (totalRecievedBytes - fileSize)
                    payloadAsIO = StringIO.StringIO(payload)

                    # Write actual data to file
                    f.write(payloadAsIO.read(actualDataSize))
                    break

                # write all data to a file
                else:
                    f.write(payload)

            # Lost or duplicate packet
            else:
                # Send ACK for last successfully recieved packet
                serverSocket.sendto(pack(ACK_FORMAT, connectionId, lastRecievedPacketId, ACK_MESSAGE), addr)

                if args.verbose:
                    print("Skipped packet(s), dropping current (ID = " , currentPacketId ,")...")
                    print("Send ACK for" , lastRecievedPacketId)

# f.close()
# serverSocket.close()
print('Connection closed, wrote file')