package sini.grapher;

import javax.swing.JFrame;

public class Frame extends JFrame {
	private static final long serialVersionUID = 3395687923968706523L;

	public Frame() {
		setTitle("Grapher");
		setSize(1000, 800);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		
		Display d = new Display();
		add(d);
		
		setVisible(true);
		d.centerViewpoint(new PointDouble(2, 0));
	}
}
