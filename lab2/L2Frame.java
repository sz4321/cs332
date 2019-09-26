import java.io.*;
import java.net.*;
import java.util.*;

public class L2Frame {
    private int destAddress, srcAddress, type, vlanId, payloadLength;
    private int checksum; // Even parity bit
    private String payloadData;
    public static int BCAST_ADDR = 0b1111;

   /**
    * L2Frame constructor
    * @param destAddress - 4 bits representing destination address
    * @param srcAddress - 4 bits representing source address
    * @param type - the payload type
    * @param vlanId - Id for VLAN
    * @param payloadData - String of bits representing data
    */
    public L2Frame(int destAddress, int srcAddress, int type, int vlanId, String payloadData) {
        
        if (
            Integer.toString(destAddress, 2).length() > 4 
            || Integer.toString(srcAddress, 2).length() > 4 
            || Integer.toString(type, 2).length() > 2 
            || Integer.toString(vlanId, 2).length() > 2 
            || payloadData.length() % 8 != 0
            ) {
            System.out.println("THROW ERROR HERE, INVALID LENGTH");
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

            // Loop over payload to determine number of ones
            int numOnes = 0;
            for(int i = 0; i < totalPayload.length(); i++) {
                if (totalPayload.charAt(i) == '1') {
                    numOnes++;
                }
            }

            // Save computed checksum value
            if (numOnes % 2 == 0 ) {
                this.checksum = 0;
            } else {
                this.checksum = 1;
            }
        }
    }
    
    /**
     * toString method converts L2Frame into string
     * @return string representation of L2Frame
     */
    public String toString() {
        return padWithZeros(4, Integer.toString(destAddress, 2)) 
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

    public static String toBinary( int value, int length){
        String reverseBinary = "";
        String binaryNum = "";
        while(value > 0){
            reverseBinary = reverseBinary + String.valueOf(value % 2);
            value = value /2;
        }
        
        //reverse what we got 
        for (int i = reverseBinary.length() -1; i >= 0; i--){
            binaryNum = binaryNum + String.valueOf(reverseBinary.charAt(i));
        }

        return padWithZeros(length, binaryNum);
    }


    /**
     * padWithZeros() util function to pad a binary number with leading zeros up to specified length
     * @param length int of needed length for bistring
     * @param binaryNum string representation of binary number to be padded with zeros
     */
    private static String padWithZeros(int length, String binaryNum) {
        int zero_num = length - binaryNum.length();
        if(zero_num > 0){
            for(int i = 0; i < zero_num; i++){
                binaryNum = "0" + binaryNum;
            }
        }
        return binaryNum;
    }
}
