import java.awt.FlowLayout;
import java.io.BufferedReader;
import java.io.FileReader;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;


public class Main {

	public static void main (String args[]) {
		read("log.log");
		
	}
	
	public static void read(String fileName) {
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new FileReader(fileName));
			String s;
			while((s = br.readLine()) != null) {
				String[] data = s.split(" ");
				for (int i = 0; i < data.length; i++) {
					System.out.print(data[i] + "---");
				}
				System.out.println();
			}
			
			// Hahah nu är det inte samma igen!
			// Haha jo det är det lol ava
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
