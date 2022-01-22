package com.rjkemp.shuttle;

import javax.swing.*;

public class ErrorModal {
	public static void show(String errorMessage) {
		JOptionPane.showConfirmDialog(null,
				errorMessage,
				"Error",
				JOptionPane.DEFAULT_OPTION,
				JOptionPane.ERROR_MESSAGE);
	}
}
