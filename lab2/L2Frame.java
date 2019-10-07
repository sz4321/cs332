import java.io.*;
import java.net.*;
import java.util.*;

/**
 * L2Frame stores values of L2Frame
 * Nikita Sietsema and Sebrina Zeleke
 * Septemeber 26 2019
 */
public class L2Frame {
    private int destAddress, srcAddress, type, vlanId, payloadLength;
    private int checksum; // Even parity bit
    private String payloadData;
    public static int BCAST_ADDR = 0b1111;

    /**
    * L2Frame constructor
    * @param bitString - string containing 4 bit destAddr, 4 bit srcAddr, 2 bit type, 2 bit vlanId, and %8 bit payloadData
    */
    public L2Frame(String bitString) {
        try {
            if (
                bitString.substring(0, 0) != "0"
                || (bitString.substring(1, -1).length() - 12) % 8 != 0
                || computeErrorCheck(bitString) != Integer.parseInt(bitString.substring(1, 1), 2)
                ) {
                    throw new IllegalArgumentException();
                }
                this.payloadLength = bitString.substring(12, -1).length() / 8;
                this.destAddress = Integer.parseInt(bitString.substring(0, 4), 2);
                this.srcAddress = Integer.parseInt(bitString.substring(4, 8), 2);
                this.type = Integer.parseInt(bitString.substring(8, 10), 2);
                this.vlanId = Integer.parseInt(bitString.substring(10, 12), 2);
                this.payloadData = bitString.substring(12, -1);
        } catch (IllegalArgumentException e) {
            System.out.println("Come on man, follow protocol");
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
            if (
                Integer.toString(destAddress, 2).length() > 4 
                || Integer.toString(srcAddress, 2).length() > 4 
                || Integer.toString(type, 2).length() > 2 
                || Integer.toString(vlanId, 2).length() > 2 
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
                
                //---determine checksum---//
                // Get total payload
                String totalPayload = Integer.toString(destAddress, 2) + Integer.toString(srcAddress, 2) + Integer.toString(type, 2) + Integer.toString(vlanId, 2) + payloadData;
    
                this.checksum =  computeErrorCheck(totalPayload);
        
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Come on man, follow protocol");
        }
    }
    
    /**
     * toString method converts L2Frame into string with prepended 0
     * @return string representation of L2Frame
     */
    public String toString() {
        return "0" 
               + this.checksum
               + padWithZeros(4, Integer.toString(destAddress, 2)) 
               + padWithZeros(4, Integer.toString(srcAddress, 2))
               + padWithZeros(2, Integer.toString(type, 2)) 
               + padWithZeros(2, Integer.toString(vlanId, 2))
               + payloadData;
    }

    /**
     * getter for payloadLength
     * @return payloadLength
     */
    public int getPayloadLength() {
        return payloadLength;
    }

    /**
     * getter for destAddress
     * @return destAddress
     */
    public int getDestAddress() {
        return destAddress;
    }

    /**
     * getter for srcAddress
     * @return srcAddress
     */
    public int getSrcAddress() {
        return srcAddress;
    }

    /**
     * getter for type
     * @return type
     */
    public int getType() {
        return type;
    }

    /** 
     * getter for vlanID
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
