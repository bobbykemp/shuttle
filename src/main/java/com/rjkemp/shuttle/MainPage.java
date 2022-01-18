package com.rjkemp.shuttle;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainPage implements ItemListener {
	JPanel cards; // a panel that uses CardLayout
	final static String BUTTONPANEL = "Card with JButtons";
	final static String TEXTPANEL = "Card with JTextField";

	public void addComponentToPane(Container pane) {
		// Put the JComboBox in a JPanel to get a nicer look.
		JPanel comboBoxPane = new JPanel(); // use FlowLayout
		String comboBoxItems[] = { BUTTONPANEL, TEXTPANEL };
		JComboBox cb = new JComboBox(comboBoxItems);
		cb.setEditable(false);
		cb.addItemListener(this);
		comboBoxPane.add(cb);

		// Create the "cards".
		JPanel card1 = new JPanel();
		card1.add(new JButton("Button 1"));
		card1.add(new JButton("Button 2"));
		card1.add(new JButton("Button 3"));

		JPanel card2 = new JPanel();
		card2.add(new JTextField("TextField", 20));

		// Create the panel that contains the "cards".
		cards = new JPanel(new CardLayout());
		cards.add(card1, BUTTONPANEL);
		cards.add(card2, TEXTPANEL);

		pane.add(comboBoxPane, BorderLayout.PAGE_START);
		pane.add(cards, BorderLayout.CENTER);
	}

	public void itemStateChanged(ItemEvent evt) {
		CardLayout cl = (CardLayout) (cards.getLayout());
		cl.show(cards, (String) evt.getItem());
		if (evt.getStateChange() == ItemEvent.SELECTED) {
			System.out.println(evt.getItem().toString());
		}
	}

	/**
	 * Create the GUI and show it. For thread safety,
	 * this method should be invoked from the
	 * event dispatch thread.
	 */
	public static void createAndShowGUI() {
		// Create and set up the window.
		JFrame frame = new JFrame("CardLayoutDemo");
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		// Create and set up the content pane.
		MainPage demo = new MainPage();
		demo.addComponentToPane(frame.getContentPane());

		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}
}
