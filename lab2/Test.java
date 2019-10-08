public class Test {
	public static void main(String[] args) {
		try {
			LightSystem system = new LightSystem();
			LightDisplay d1 = new LightDisplay(new LightPanel());
			LightDisplay d2 = new LightDisplay(new LightPanel());

			BitDisplay b = new BitDisplay(new BitHandler());
			BitDisplay b2 = new BitDisplay(new BitHandler());

			// Test layer 2 frame
			L2Frame dllFrame = new L2Frame(0b0010, 0b0001, 0b00, 0b11, "00111100");
			assert dllFrame.getPayloadData() == "00111100" : "payload failed!";
			assert dllFrame.getPayloadLength() == 1 : "payload length failed!"; //<-- unit is bytes
			assert dllFrame.getDestAddress() == 0b0010 : "dest address failed!";
			assert dllFrame.getSrcAddress() == 0b0001 : "src address failed!";
			assert dllFrame.getType() == 0000 : "type failed failed!";
			assert dllFrame.getChecksum() == 0 : "CheckSum failed!";
			assert dllFrame.getVlanId() ==  0b11 : "vlanId failed!";
		
			//test toBinary()
			// assert dllFrame.toBinary(8,8) ==  "00001000" : "toBinary failed 1";
			// assert dllFrame.toBinary(5,5) ==  "00101" : "toBinary failed 2";
			// assert dllFrame.toBinary(17,5) ==  "10001" : "toBinary failed 3";
			// assert dllFrame.toBinary(8,8) ==  "00001000" : "toBinary failed 4";

			System.out.println("frame: " + dllFrame.toString());

			// Test L2Handler
			L2Handler dllHandler = new L2Handler("localhost", LightSystem.DEFAULT_PORT, 0b1101);
			assert dllHandler.getMacAddr() == 0b1101 : "getMacAddr 1 failed!";
			System.out.println("macAddr: " + dllHandler.toString());

			L2Handler dllHandler2 = new L2Handler("localhost", LightSystem.DEFAULT_PORT, 0b01001);
			assert dllHandler2.getMacAddr() == 0b1001 : "getMacAddr 2 failed!";
			System.out.println("macAddr: " + dllHandler2.toString());

			// Test error check computes correctly
			assert dllFrame.computeErrorCheck("101") == 0 : "computeErrorCheck failed!";
			assert dllFrame.computeErrorCheck("111") == 1 : "computeErrorCheck failed!";
			assert dllFrame.computeErrorCheck("011") == 0 : "computeErrorCheck failed!";

			System.out.println("All Tests passed!");

			Layer2Display layer2Dispaly = new Layer2Display(dllHandler);
			Layer2Display secondLayer2Display = new Layer2Display(dllHandler2);
			L2Handler dllHandler3 = new L2Handler("localhost", LightSystem.DEFAULT_PORT, 0b01011);
			Layer2Display thirdLayer2Display = new Layer2Display(dllHandler3);

		} catch (Exception e) {
			System.out.println("We got an exception");
		}
		
	}
}