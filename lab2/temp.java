import java.io.*;
import java.net.*;
import java.util.*;

public class L2Frame {
    private int destAddress, srcAddress, type, vlanId, payloadLength;
    private int checksum; // Even parity bit
    private String payloadData;

   /**
    * L2Frame constructor
    * @param destAddress - 4 bits representing destination address
    * @param srcAddress - 4 bits representing source address
    * @param type - the payload type
    * @param vlanId - Id for VLAN
    * @param payloadData - String of bits representing data
    */
    public L2Frame(int destAddress, int srcAddress, int type, int vlanId, String payloadData) {
        
        if (payloadData.length() % 8 != 0) {
            // invalid payload length, bytes not of size 8
            //THROW ERROR HERE
        } else {
            this.payloadLength = payloadData.length() / 8;
            this.destAddress = destAddress;
            this.srcAddress = srcAddress;
            this.type = type;
            this.vlanId = vlanId;
            this.payloadData = payloadData;

            // Get total payload
            String totalPayload = Integer.toString(destAddress, 2) + Integer.toString(srcAddress, 2) + Integer.toString(type, 2) + Integer.toString(vlanId, 2) + payloadData;

            
        }
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

        int zero_num = length - binaryNum.length();
        if(zero_num > 0){
            for(int i = 0; i < zero_num; i++){
                binaryNum = "0" + binaryNum;
            }
        }

        return binaryNum;
    }

    /** 
     * Computes the error checking value 
     * @param totalPayload
     * @return checksum
     */
    public static Integer computeErrorCheck(String totalPayload){
       
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

        return this.checkSum;
    }

 
}
