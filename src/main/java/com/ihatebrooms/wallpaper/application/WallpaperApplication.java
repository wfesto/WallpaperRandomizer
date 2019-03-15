package com.ihatebrooms.wallpaper.application;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ihatebrooms.wallpaper.data.Settings;
import com.ihatebrooms.wallpaper.data.SettingsReaderWriter;
import com.ihatebrooms.wallpaper.event.handler.ApplyChangesEventHandler;
import com.ihatebrooms.wallpaper.event.handler.FileChoiceEventHandler;
import com.ihatebrooms.wallpaper.event.listener.ModeChangedListener;
import com.ihatebrooms.wallpaper.event.observer.SettingsUpdateObserver;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class WallpaperApplication extends Application {

	private static final Logger logger = LogManager.getLogger(WallpaperApplication.class);

	protected ImageView previewImageView = new ImageView();
	protected Settings settings;
	protected Stage primary;

	public static void main(String[] args) {
		launch(args);
	}

	private void showStage() {
		if (primary != null) {
			primary.show();
			primary.toFront();
		}
	}

	private void addAppToTray() {
		try {
			java.awt.Toolkit.getDefaultToolkit();
			if (!java.awt.SystemTray.isSupported()) {
				System.out.println("No system tray support, application exiting.");
				Platform.exit();
			}

			// set up a system tray icon.
			java.awt.SystemTray tray = java.awt.SystemTray.getSystemTray();
			URL imageLoc = new URL("file:///E:\\Pictures\\Other\\untitled1.gif");
			java.awt.Image image = ImageIO.read(imageLoc);
			java.awt.TrayIcon trayIcon = new java.awt.TrayIcon(image);

			// if the user double-clicks on the tray icon, show the main app
			// stage.
			trayIcon.addActionListener(event -> Platform.runLater(this::showStage));

			// java.awt.MenuItem openItem = new java.awt.MenuItem("hello,
			// world");
			// openItem.addActionListener(event ->
			// Platform.runLater(this::showStage));

			// java.awt.Font defaultFont = java.awt.Font.decode(null);
			// java.awt.Font boldFont =
			// defaultFont.deriveFont(java.awt.Font.BOLD);
			// openItem.setFont(boldFont);

			java.awt.MenuItem exitItem = new java.awt.MenuItem("Exit");
			exitItem.addActionListener(event -> {
				Platform.exit();
				tray.remove(trayIcon);
			});

			final java.awt.PopupMenu popup = new java.awt.PopupMenu();
			// popup.add(openItem);
			// popup.addSeparator();
			popup.add(exitItem);
			trayIcon.setPopupMenu(popup);

			// add the application tray icon to the system tray.
			tray.add(trayIcon);
		} catch (java.awt.AWTException | IOException e) {
			System.out.println("Unable to init system tray");
			e.printStackTrace();
		}
	}

	@Override
	public void start(Stage primary) throws Exception {
		Platform.setImplicitExit(false);
		this.primary = primary;
		SwingUtilities.invokeLater(this::addAppToTray);

		GridPane rootPane = new GridPane();
		rootPane.setAlignment(Pos.TOP_LEFT);
		rootPane.setHgap(10);
		rootPane.setVgap(10);
		rootPane.setPadding(new Insets(25, 25, 25, 25));

		Scene scene = new Scene(rootPane, 600, 480);
		this.createContent(rootPane, primary);
		primary.setScene(scene);
		EventHandler<WindowEvent> handler = new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				settings.forceUpdate();
			}
		};

		ChangeListener<Number> changeListener = new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> Observable, Number oldValue, Number newValue) {
				settings.forceUpdate();
			}
		};

		primary.addEventHandler(WindowEvent.ANY, handler);
		primary.widthProperty().addListener(changeListener);
		primary.heightProperty().addListener(changeListener);

	}

	private void createContent(GridPane rootPane, Stage primary) throws Exception {
		String activeProfile = SettingsReaderWriter.getActiveProfile();
		Map<String, Settings> settingsMap = SettingsReaderWriter.readSettings();
		settings = settingsMap.get(activeProfile);

		logger.trace("Full settings map:\n" + settingsMap.toString());
		logger.debug("Application loading settings:\n" + settings.toString());

		int colIdx = 0;

		primary.setTitle("Wallpaper Randomizer Settings");

		ToggleGroup modeRadioGroup = new ToggleGroup();
		RadioButton singleFileRButton = new RadioButton("Static File");
		RadioButton multiFileRButton = new RadioButton("Custom List");
		RadioButton singleDirRButton = new RadioButton("Directory");
		singleFileRButton.setToggleGroup(modeRadioGroup);
		singleFileRButton.setUserData(Settings.MODE_SINGLE_FILE);
		multiFileRButton.setToggleGroup(modeRadioGroup);
		multiFileRButton.setUserData(Settings.MODE_MULTI_FILE);
		singleDirRButton.setToggleGroup(modeRadioGroup);
		singleDirRButton.setUserData(Settings.MODE_SINGLE_DIR);
		getSelectedRadioButton(modeRadioGroup, settings);

		HBox modeHBox = new HBox();
		VBox modeVBox = new VBox();
		modeVBox.getChildren().add(singleFileRButton);
		modeVBox.getChildren().add(multiFileRButton);
		modeVBox.getChildren().add(singleDirRButton);
		modeHBox.getChildren().add(new Label("Mode "));
		modeHBox.getChildren().add(modeVBox);

		rootPane.add(modeHBox, 0, colIdx);

		Button chooseFileButton = new Button("Choose File");
		Button applyChangesButton = new Button("Apply Changes");

		VBox buttonsVBox = new VBox();
		buttonsVBox.setSpacing(10);
		buttonsVBox.getChildren().add(chooseFileButton);
		buttonsVBox.getChildren().add(applyChangesButton);
		rootPane.add(buttonsVBox, 1, colIdx++);

		TextField currentSelectionField = new TextField();
		currentSelectionField.setEditable(false);
		if (settings.getFilePath() != null) {
			currentSelectionField.setText(settings.getFilePath());
		} else {
			currentSelectionField.setText(settings.getCurrentDir());
		}
		GridPane.setColumnSpan(currentSelectionField, 2);
		rootPane.add(currentSelectionField, 0, colIdx++);

		previewImageView.setPreserveRatio(true);
		GridPane.setColumnSpan(previewImageView, 2);
		rootPane.add(previewImageView, 0, colIdx);

		ListView<String> fileListView = new ListView<>();
		fileListView.setVisible(false);
		fileListView.setEditable(false);
		rootPane.add(fileListView, 0, colIdx++);

		modeRadioGroup.selectedToggleProperty().addListener(new ModeChangedListener(settings, previewImageView, currentSelectionField, fileListView));
		chooseFileButton.setOnAction(new FileChoiceEventHandler(primary, settings, currentSelectionField, fileListView));
		applyChangesButton.setOnAction(new ApplyChangesEventHandler(settings));
		settings.addObserver(new SettingsUpdateObserver(primary, previewImageView));
	}

	private static void getSelectedRadioButton(ToggleGroup group, Settings settings) {
		for (Toggle button : group.getToggles()) {
			if (settings.getCurrentMode() == ((Integer) button.getUserData()).intValue()) {
				button.setSelected(true);
			}
		}
	}
}
