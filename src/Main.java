import java.awt.FlowLayout;
import java.io.BufferedReader;
import java.io.FileReader;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;


public class Main {
	private static final int minBuf = 4;
	private static final int maxBuf = 6;
	private static int availableBandwidth;
	
	public static void main (String args[]) {
		read("log.log");
		
		ImageIcon img = new ImageIcon("img/2Q.png");
		JLabel imgLabel = new JLabel(img);
		
		JFrame frame = new JFrame("HFS player");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new FlowLayout());
		frame.setVisible(true);
		frame.add(imgLabel);
		frame.pack();
		
	}
	
	public static void read(String fileName) {
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new FileReader(fileName));
			String s;
			int count = 1;
			while((s = br.readLine()) != null) {
				String[] data = s.split(" ");
//				for (int i = 0; i < data.length; i++) {
//					System.out.print(data[i] + "---");
//				}
				int bit = Integer.parseInt(data[4])*8;
				int kbit = bit/1000;
				availableBandwidth = kbit;
				System.out.println(count++ + ": " + kbit + " Kbit/s");
			}
			
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(br != null) {
					br.close();
				}
			} catch (Exception e2) {
				e2	.printStackTrace();
			}
		}
	}

}
