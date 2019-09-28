import java.io.*;
import java.net.*;
import java.util.*;

/**
 * L2Handler sends an L2Frame to layer1 and 
 *      recieves a bitString from layer1 to create L2Frame
 * @author Nikita Sietsema and Sebrina Zeleke
 * Septemeber 26 2019
 */
public class L2Handler {
    BitHandler handler;
    int macAddr;
    Layer2Listener layer2listener;


    /**
    * L2Handler default constructor with specified macAddr
    * @param macAddr - MAC address
    */
    public L2Handler(int macAddr) throws IllegalArgumentException {
        if (Integer.toString(macAddr, 2).length() > 8) {
            throw new IllegalArgumentException();
        }
        this.handler = new BitHandler();
        handler.setListener(this);
        this.macAddr = macAddr;
    }

   /**
    * L2Handler constructor
    * @param host - String of host name for bitHandler
    * @param port - port number for bitHandler
    * @param macAddr - MAC address
    */
    public L2Handler(String host, int port, int macAddr) {
        this.handler = new BitHandler(host, port);
        handler.setListener(this);
        this.macAddr = macAddr;
    }

    // Set up listener
    public void setListener(Layer2Listener l) {
		layer2listener = l;
	}

    /**
    * getter for macAddr
    * @return macAddr
    */
    public int getMacAddr() {
        return macAddr;
    }

    /**
    * return string of macAddr
    * @return macAddr string
    */
    public String toString() {
        return padWithZeros(8, Integer.toString(macAddr, 2));
    }

    /**
    * Take L2Frame and covert it to string representation
    * @return 
    */
    public void send(L2Frame frame) throws CollisionException {
        String frameBitString = frame.toString();
        while (true) {
            if (handler.isSilent()) {
                break;
            }
        }
        handler.broadcast(frameBitString);
    }

    // public void bitsReceived(BitHandler h, String bits) {
	// 	            // receiveField.setText(bits);
    // }

    /**
     * padWithZeros() util function to pad a binary number with leading zeros up to specified length
     * @param length int of needed length for bistring
     * @param binaryNum string representation of binary number to be padded with zeros
     */
    private static String padWithZeros(int length, String binaryNum) {
        int zero_num = length - binaryNum.length();
        if (zero_num > 0){
            for (int i = 0; i < zero_num; i++){
                binaryNum = "0" + binaryNum;
            }
        }
        return binaryNum;
    }
}
