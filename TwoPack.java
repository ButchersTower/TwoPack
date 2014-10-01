package twoPack;

import javax.swing.JFrame;

public class TwoPack extends JFrame {
	public TwoPack() {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(new PanelNew());
		frame.pack();
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setTitle("TwoPack");
	}

	public static void main(String[] args) {
		new TwoPack();
	}
}
