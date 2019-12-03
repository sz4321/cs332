# Client to send file to another client user server
# Authors: Nikita Sietsema and Sebrina Zeleke
# Date: 11.19.19

import select
import socket
import sys
import argparse
import os, errno
import random
from struct import *
from threading import Timer

#################################### Constants ####################################
PAYLOAD_SIZE = 1450
ACK_HEADER_SIZE = 8 # 4 bytes connectionID + 4 bytes packetID
ACK_MESSAGE = "_ACK_"
ACK_SIZE = ACK_HEADER_SIZE + 5 # 5 is ACK message size

PACKET_FORMAT = "IIIB1450s" # I = 4 byte int, B = 1 byte int, 1450s = data size
ACK_FORMAT = "II5s"         # I = 4 byte int, 5s = s byte string

#################################### Arguments ####################################
# set up commands for user
parser = argparse.ArgumentParser(description="Client to send files")

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


#################################### Main Logic ####################################
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
    filename = args.filename
    fileSrc = open(filename,'rb')

    # Read data into packets
    data = fileSrc.read(PAYLOAD_SIZE)

    # get total size of file to send
    fileSize = os.path.getsize(filename)

    # create connection ID
    connectionId = random.randint(0, 2147483646)

    ################## Define Variables ##################
    notDone = True # Used to ensure we wait for final ACK before closing
    finalPrintMessage = "" # Based on success of file transfer
    currentPacketID = 0
    ackSpace = 0
    numPacketsSinceLastAck = 0
    ACK_flag = 1
    packetOfLastConfirmedACK = 0
    nextPacketToConfirmACK = 0
    numConsecutiveTimeouts = 0 # Used only for last packet to account for lost final ACK
    
    clientSocket.settimeout(1) # Set timeout to be 1 second before giving up on ACK

    ################## Begin Creating Packets ##################
    while (notDone):
        # Determine if this is last packet
        dataSize = len(data)
        lastPacket = dataSize < PAYLOAD_SIZE

        ################## Calculate ACK_flag ##################
        # Always send ack for last packet
        if lastPacket:
            if args.verbose:
                print("Got last packet, ID: ", currentPacketID)
            ACK_flag = 1
            nextPacketToConfirmACK = currentPacketID
            numPacketsSinceLastAck = 0
            ackSpace = 0
            
        # Increment space between acks
        elif (ackSpace == numPacketsSinceLastAck):
            ACK_flag = 1
            nextPacketToConfirmACK = currentPacketID
            ackSpace += 1
            numPacketsSinceLastAck = 0

        # Count packets since last ack
        # until we reach ack space
        else:
            ACK_flag = 0
            numPacketsSinceLastAck += 1


        ################## Create Packet ##################
        # Send packet to server
        clientSocket.send(pack(PACKET_FORMAT, connectionId, fileSize, currentPacketID, ACK_flag, data))

        # Increment currentPacketID for next packet
        currentPacketID += 1

        ################## Wait for ACK ##################
        if (ACK_flag):
            while True:
                if args.verbose:
                    print("waiting for ACK - current ackSpace: ", ackSpace)

                # Catch timeout except to stop waiting for ack
                # and start resending packets
                try:
                    # Retrieve ACK packet (if we get here)
                    data, addr = clientSocket.recvfrom(ACK_SIZE)

                    # Extract ACK fields and message
                    ackPacket = unpack(ACK_FORMAT, data)
                    ackConnectionId = ackPacket[0]
                    ackPacketNumber = ackPacket[1]
                    packetOfLastConfirmedACK = ackPacketNumber
                    ackMessage = ackPacket[2]

                    # Determine if ACK is for us, and for correct packet
                    if (addr[0] == args.server and ackConnectionId == connectionId and ackMessage == ACK_MESSAGE and nextPacketToConfirmACK == ackPacketNumber):
                        if args.verbose:
                            print("recieved ACK for packet", ackPacketNumber)

                        if lastPacket:
                            finalPrintMessage = "File successfuly sent!"
                            notDone = False

                        break # Recieved ACK, so stop waiting

                # Did not recieve ACK in timeout
                except socket.timeout:
                    if args.verbose:
                        print("Timer has run out, sending packets again!")
                    print("currentPacket", currentPacketID, "lastPacket?", lastPacket)
                    if lastPacket: pass
                        # notDone = False
                    #     # Check num of timeouts
                    #     # If == 5, give up on recieving ACK
                    #     if numConsecutiveTimeouts == 5:
                    #         finalPrintMessage = "File transfer success unknown."
                    #         notDone = False
                    #         break # Give up on final ACK and stop waiting

                    #     # Count num timeouts for last packet
                    #     # To avoid waiting for lost final ACK
                    #     numConsecutiveTimeouts += 1

                    # Reset file pointer to last successfully sent byte
                    fileSrc.seek((1450*packetOfLastConfirmedACK), os.SEEK_SET)

                    # Reset packet ID to packet of last successful ACK
                    currentPacketID = packetOfLastConfirmedACK

                    # Reset ACK counters to beginning values
                    ACK_flag = 1
                    numPacketsSinceLastAck = 0
                    ackSpace = 0

                    # Did not recieve ACK in allotted time
                    # Break out of loop that waits for ACK
                    # Begin resending
                    break

        # close file and break sending loop
        # lastPacket and done
        print("before check for last packet and done")
        if lastPacket and not notDone:  
            print("inside last packet and done")  
            fileSrc.close()
            break

        print("Getting next packet")
        # Get next packet
        data = fileSrc.read(PAYLOAD_SIZE)

    print("ENDDDDDDDDD||||||||||")  
    print(finalPrintMessage)
    clientSocket.close()

except Exception as e:
    print("Could not connect to server.", e)
    clientSocket.close()