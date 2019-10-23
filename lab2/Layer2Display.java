import java.awt.event.*;
import javax.swing.*;

/**
 * Layer2Display create a frame and send it
 * @author Prof. Norman
 * Edited by: Nikita Sietsema and Sebrina Zeleke
 * Septemeber 26 2019
 */
public class Layer2Display implements ActionListener, Layer2Listener
{
	private L2Handler handler;
	private JTextField vlanIdField;
	private JTextField displayField;
	private JTextField srcAddrField;
	private JTextField destAddrField;
	private JTextField payloadField;

	public Layer2Display(L2Handler handler)
	{
		this.handler = handler;
		handler.setListener(this);

		JFrame frame = new JFrame(handler.toString());
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(),
								BoxLayout.PAGE_AXIS));

		displayField = new JTextField(20);
		displayField.setEditable(false);
		frame.getContentPane().add(displayField);

		frame.getContentPane().add(new JLabel("Destination Address:"));

		destAddrField = new JTextField(20);
		destAddrField.addActionListener(this);
		frame.getContentPane().add(destAddrField);

		frame.getContentPane().add(new JLabel("Source Address:"));

		srcAddrField = new JTextField(20);
		srcAddrField.addActionListener(this);
		frame.getContentPane().add(srcAddrField);

		frame.getContentPane().add(new JLabel("VLAN Id:"));

		vlanIdField = new JTextField(20);
		vlanIdField.addActionListener(this);
		frame.getContentPane().add(vlanIdField);

		frame.getContentPane().add(new JLabel("Payload:"));

		payloadField = new JTextField(20);
		payloadField.addActionListener(this);
		frame.getContentPane().add(payloadField);

		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * frameRecieved() takes a layer two frame and displays it in the text field
	 * @param handler a L2Handler to handle bits recieved
	 * @param frame the frame used to set the text on
	 */
	public void frameRecieved(L2Handler handler, L2Frame frame) {
		displayField.setText(frame.getPayloadData());
	}
	
    public void actionPerformed(ActionEvent e) 
    {
		displayField.setText("Sending...");
		new Thread()
		{
			public void run() {
				int destAddr = Integer.parseInt(destAddrField.getText());
				int srcAddr = Integer.parseInt(srcAddrField.getText());
				int vlanId = Integer.parseInt(vlanIdField.getText());
				String payload = payloadField.getText();
				handler.send(new L2Frame(destAddr, srcAddr, 0, vlanId, payload));
				displayField.setText("Sent.");
			}
		}.start();
    }

}