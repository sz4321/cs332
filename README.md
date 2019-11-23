# RCMP - Reliable Calvin Message Protocol over UDP
### Build protocol to transfer files in packets over UDP

Packets have headers with:
- Connection Id: 4 bytes
- Number of bytes total: 4 bytes
- Packet number: 4 bytes

### Use positive ACKs system between server and client

ACKs have headers with:
- Connection Id: 4 bytes
- Packet number: 4 bytes


## Running client and server:
(1) Start server: `python server.py -p <port> -f <savefile> -v`

(2) Start client: `python client.py -p <port> -f <fileToSend> -s <serverIP> -v`

# clientChat
Build client to chat through terminal with another client using server.

# lab2
Build layer 2 protocol
