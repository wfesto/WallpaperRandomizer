package com.ihatebrooms.wallpaper.application;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ihatebrooms.wallpaper.data.Settings;
import com.ihatebrooms.wallpaper.data.SettingsReaderWriter;
import com.ihatebrooms.wallpaper.event.handler.FileChoiceEventHandler;
import com.ihatebrooms.wallpaper.event.handler.SaveChangesButtonEventHandler;
import com.ihatebrooms.wallpaper.event.observer.SettingsUpdateObserver;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
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

			trayIcon.addActionListener(event -> Platform.runLater(this::showStage));

			java.awt.MenuItem exitItem = new java.awt.MenuItem("Exit");
			exitItem.addActionListener(event -> {
				Platform.exit();
				tray.remove(trayIcon);
			});

			final java.awt.PopupMenu popup = new java.awt.PopupMenu();
			popup.add(exitItem);
			trayIcon.setPopupMenu(popup);

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

		int widthSpacing = 80;
		int heightSpacing = 300;

		primary.addEventHandler(WindowEvent.ANY, x -> settings.forceUpdate());
		primary.widthProperty().addListener((x, y, z) -> {
			previewImageView.setFitWidth(primary.getWidth() - widthSpacing);
			previewImageView.setFitHeight(primary.getHeight() - heightSpacing);
		});
		primary.heightProperty().addListener((x, y, z) -> {
			previewImageView.setFitWidth(primary.getWidth() - widthSpacing);
			previewImageView.setFitHeight(primary.getHeight() - heightSpacing);
		});

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
		setSelectedRadioButton(modeRadioGroup, settings);

		HBox modeHBox = new HBox();
		VBox modeVBox = new VBox();
		modeVBox.getChildren().add(new Label("Mode"));
		modeVBox.getChildren().add(singleFileRButton);
		modeVBox.getChildren().add(multiFileRButton);
		modeVBox.getChildren().add(singleDirRButton);
		modeHBox.getChildren().add(modeVBox);
		Button chooseFileButton = new Button("Choose File");
		modeVBox.getChildren().add(chooseFileButton);
		modeVBox.setSpacing(5);

		VBox optVBox = new VBox();

		CheckBox randomCB = new CheckBox(" Randomize List");
		randomCB.setAllowIndeterminate(false);
		randomCB.setSelected(settings.isRandomizeList());

		CheckBox recurseCB = new CheckBox("Recurse Subdirectories");
		recurseCB.setAllowIndeterminate(false);
		recurseCB.setSelected(settings.isRecurseSubDirs());
		optVBox.getChildren().add(randomCB);
		optVBox.getChildren().add(recurseCB);
		optVBox.setSpacing(5);

		TextField changeDelay = new TextField("");
		changeDelay.setText(Integer.toString(settings.getChangeDelay() / 1000));

		HBox delayHBox = new HBox();
		delayHBox.getChildren().add(new Label("Change delay (seconds) "));
		delayHBox.getChildren().add(changeDelay);
		optVBox.getChildren().add(delayHBox);

		modeHBox.getChildren().add(optVBox);

		VBox buttonVBox = new VBox();
		Button saveButton = new Button("Save Changes");
		buttonVBox.getChildren().add(saveButton);
		Button revertButton = new Button("Revert Changes");
		buttonVBox.getChildren().add(revertButton);
		buttonVBox.setSpacing(10);

		modeHBox.getChildren().add(buttonVBox);
		modeHBox.setSpacing(10);
		rootPane.add(modeHBox, 0, colIdx++);

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
		previewImageView.setVisible(settings.getCurrentMode() == Settings.MODE_SINGLE_FILE || settings.getCurrentMode() == Settings.MODE_SINGLE_DIR);
		GridPane.setColumnSpan(previewImageView, 2);
		rootPane.add(previewImageView, 0, colIdx);

		ListView<String> fileListView = new ListView<>();
		fileListView.setVisible(settings.getCurrentMode() == Settings.MODE_MULTI_FILE);
		fileListView.setEditable(false);
		fileListView.getItems().addAll(settings.getFileList());
		rootPane.add(fileListView, 0, colIdx++);

		randomCB.setOnAction(ae -> settings.setRandomizeList(randomCB.isSelected()));
		recurseCB.setOnAction(ae -> settings.setRecurseSubDirs(recurseCB.isSelected()));

		chooseFileButton.setOnAction(new FileChoiceEventHandler(primary, settings));
		saveButton.setOnAction(new SaveChangesButtonEventHandler(settings));
		settings.addObserver(new SettingsUpdateObserver(primary, previewImageView, currentSelectionField, fileListView));

		modeRadioGroup.selectedToggleProperty().addListener((x, y, newToggle) -> {
			int newVal = ((Integer) newToggle.getUserData()).intValue();
			settings.setCurrentMode(newVal);
			boolean hasList = true;
			recurseCB.setDisable(true);

			if (newVal == Settings.MODE_SINGLE_FILE) {
				hasList = false;
			} else if (newVal == Settings.MODE_SINGLE_DIR) {
				recurseCB.setDisable(false);
			} else if (newVal == Settings.MODE_MULTI_FILE) {

			}

			randomCB.setDisable(hasList);

		});

		changeDelay.addEventHandler(KeyEvent.KEY_RELEASED, ae -> {
			int newVal = 60 * 60;
			try {
				newVal = StringUtils.isNotEmpty(changeDelay.getText()) ? Integer.parseInt(changeDelay.getText()) : newVal;
			} catch (Exception e) {
				logger.error("Unable to parse value: " + changeDelay.getText());
				logger.error(e.getMessage());
			}

			settings.setChangeDelay(1000 * newVal);
		});

		if (!(((Integer) modeRadioGroup.getSelectedToggle().getUserData()).intValue() == Settings.MODE_SINGLE_FILE)) {
			saveButton.fire();
		}

	}

	private static void setSelectedRadioButton(ToggleGroup group, Settings settings) {
		for (Toggle button : group.getToggles()) {
			if (settings.getCurrentMode() == ((Integer) button.getUserData()).intValue()) {
				button.setSelected(true);
			}
		}
	}
}
