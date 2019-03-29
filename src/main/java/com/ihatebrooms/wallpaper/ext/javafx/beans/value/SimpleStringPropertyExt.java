package com.ihatebrooms.wallpaper.ext.javafx.beans.value;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javafx.beans.property.SimpleStringProperty;

public class SimpleStringPropertyExt extends SimpleStringProperty implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 4064644800134767717L;

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(this.getValue());
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.set((String) in.readObject());
	}
}
