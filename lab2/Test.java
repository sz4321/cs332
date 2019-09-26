public class Test {
	public static void main(String[] args) {
		LightSystem system = new LightSystem();
		LightDisplay d1 = new LightDisplay(new LightPanel());
		LightDisplay d2 = new LightDisplay(new LightPanel());

		BitDisplay b = new BitDisplay(new BitHandler());
		BitDisplay b2 = new BitDisplay(new BitHandler());

		// Test layer 2 frame
		L2Frame dllFrame = new L2Frame(0b0010, 0b0001, 0b0000, 0b11, "00111100");
		assert dllFrame.getPayloadData() == "00111100" : "payload failed!";
		assert dllFrame.getPayloadLength() == 1 : "payload length failed!"; //<-- unit is bytes
		assert dllFrame.getDestAddress() == 0b0010 : "dest address failed!";
		assert dllFrame.getSrcAddress() == 0b0001 : "src address failed!";
		assert dllFrame.getType() == 0000 : "type failed failed!";
		assert dllFrame.getChecksum() == 0 : "CheckSum failed!";
		assert dllFrame.getVlanId() ==  0b11 : "vlanId failed!";
	
		//test toBinary()
		assert dllFrame.toBinary(8,8) ==  "00001000" : "toBinary failed";
		assert dllFrame.toBinary(5,5) ==  "00101" : "toBinary failed";
		assert dllFrame.toBinary(17,5) ==  "10001" : "toBinary failed";
		assert dllFrame.toBinary(8,8) ==  "00001000" : "toBinary failed";
		System.out.println("All Tests passed!");
	}
}