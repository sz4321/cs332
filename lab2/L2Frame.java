import java.io.*;
import java.net.*;
import java.util.*;

/**
 * L2Frame stores values of L2Frame
 * @author Nikita Sietsema and Sebrina Zeleke
 * Septemeber 26 2019
 */
public class L2Frame {
    private int destAddress, srcAddress, type, vlanId, payloadLength;
    private int checksum; // Even parity bit
    private String payloadData;
    public static int BCAST_ADDR = 0b1111;

    /**
    * L2Frame constructor
    * @param bitString - string containing leading 0, checksum, 4 bit destAddr, 4 bit srcAddr, 2 bit type, 2 bit vlanId, and %8 bit payloadData
    */
    public L2Frame(String bitString) {
        try {
            // System.out.println("bitString passed to L2Frame: " + bitString);
            // System.out.println("bitString length: " + bitString.length());
            // System.out.println("bitString error check: " + computeErrorCheck(bitString.substring(2)) + "==" + Integer.parseInt(bitString.substring(1, 2), 2));

            if (bitString.charAt(0) != '0'
                || (bitString.length() - 14) % 8 != 0 
                || computeErrorCheck(bitString.substring(2)) != Integer.parseInt(bitString.substring(1, 2), 2)
                ) {
                    throw new IllegalArgumentException();
                }
                this.destAddress = Integer.parseInt(bitString.substring(2, 6), 2);
                this.srcAddress = Integer.parseInt(bitString.substring(6, 10), 2);
                this.type = Integer.parseInt(bitString.substring(10, 12), 2);
                this.vlanId = Integer.parseInt(bitString.substring(12, 14), 2);
                this.payloadData = bitString.substring(14);
                this.payloadLength = payloadData.length() / 8;
                
        } catch (IllegalArgumentException e) {
            System.out.println("Come on man, follow protocol (L2Frame bitstring constructor)");
        }
    }

   /**
    * L2Frame constructor
    * @param destAddress - 4 bits representing destination address
    * @param srcAddress - 4 bits representing source address
    * @param type - the payload type
    * @param vlanId - Id for VLAN
    * @param payloadData - String of bits representing data
    */
    public L2Frame(int destAddress, int srcAddress, int type, int vlanId, String payloadData) {
        try {
            String myDestAddress = Integer.toBinaryString(destAddress);
            String mySrcAddress = Integer.toBinaryString(srcAddress);
            String myType = Integer.toBinaryString(type);
            String myVlandId = Integer.toBinaryString(vlanId);

            if (
                myDestAddress.length() > 4 
                || mySrcAddress.length() > 4 
                || myType.length() > 2 
                || myVlandId.length() > 2 
                || payloadData.length() % 8 != 0
                ) {
                    throw new IllegalArgumentException();
            } else {
                this.payloadLength = payloadData.length() / 8;
                this.destAddress = destAddress;
                this.srcAddress = srcAddress;
                this.type = type;
                this.vlanId = vlanId;
                this.payloadData = payloadData;
                
                // Get total payload
                String totalPayload = padWithZeros(4, myDestAddress) + padWithZeros(4, mySrcAddress) + padWithZeros(4, myType) + padWithZeros(4, myVlandId) + this.payloadData;

                // Calculate error check on totalPayload
                this.checksum =  computeErrorCheck(totalPayload);
        
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Come on man, follow protocol (L2Frame explicit constructor)");
        }
    }
    
    /**
     * toString method converts L2Frame into string with prepended 0
     * @return string representation of L2Frame in binary
     */
    public String toString() {
        return "0" 
               + checksum
               + toBinary(destAddress, 4)
               + toBinary(srcAddress, 4)
               + toBinary(type, 2)
               + toBinary(vlanId, 2)
               + payloadData;
    }

    /**
     * getter for payloadLength
     * @return payloadLength in bytes
     */
    public int getPayloadLength() {
        return payloadLength;
    }

    /**
     * getter for destAddress, returns in decimal
     * @return destAddress
     */
    public int getDestAddress() {
        return destAddress;
    }

    /**
     * getter for srcAddress, returns in decimal
     * @return srcAddress
     */
    public int getSrcAddress() {
        return srcAddress;
    }

    /**
     * getter for type, returns in decimal
     * @return type
     */
    public int getType() {
        return type;
    }

    /** 
     * getter for vlanID, returns in decimal
     * @return vlanID
     */
    public int getVlanId() {
        return vlanId;
    }

    /**
     * getter for checkSum
    * @return checkSum
    */
    public int getChecksum() {
        return checksum;
    }

    /**
     * getter for payloadData
     * @return payloadData
     */
    public String getPayloadData() {
        return payloadData;
    }

    
   /**
     * Changes the value to binary and adds 0 infront when needed
     * @return binaryNum
     * @param value
     * @param length
     */
     public static String toBinary( int value, int length){
        String binaryNum = Integer.toBinaryString(value);
        return padWithZeros(length, binaryNum);
    }

    /**
     * padWithZeros() util function to pad a binary number with leading zeros up to specified length
     * @param length int of needed length for bistring
     * @param binaryNum string representation of binary number to be padded with zeros
     * @return binaryNum string
     */
    private static String padWithZeros(int length, String binaryNum) {
        int num_zeros = length - binaryNum.length();
        if(num_zeros > 0){
            for(int i = 0; i < num_zeros; i++){
                binaryNum = "0" + binaryNum;
            }
        }
        return binaryNum;
    }
    
    /** 
     * Computes the error checking value using even parity
     * @param totalPayload
     * @return checksum
     */
     public static Integer computeErrorCheck(String totalPayload){
       int myCheckSum;

        // Loop over payload to determine number of ones
        int numOnes = 0;
        for(int i = 0; i < totalPayload.length(); i++) {
            if (totalPayload.charAt(i) == '1') {
                numOnes++;
            }
        }

        // Save computed checksum value
        if (numOnes % 2 == 0 ) {
            myCheckSum = 0;
        } else {
            myCheckSum = 1;
        }

        return myCheckSum;
    }

}
