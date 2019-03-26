package com.ihatebrooms.wallpaper.application.ui;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ihatebrooms.wallpaper.data.Settings;
import com.ihatebrooms.wallpaper.event.handler.FileChoiceEventHandler;
import com.ihatebrooms.wallpaper.event.handler.RevertButtonEventHandler;
import com.ihatebrooms.wallpaper.event.handler.SaveChangesButtonEventHandler;
import com.ihatebrooms.wallpaper.event.observer.SettingsUpdateObserver;
import com.ihatebrooms.wallpaper.ext.javafx.scene.image.ImageViewExt;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

//TODO: file list manipulation - reordering, delete, preview, etc
//TODO: button to advance file
public class WallpaperUIElements {

	private static final Logger logger = LogManager.getLogger(WallpaperUIElements.class);

	protected final ResourceBundle resourceBundle = ResourceBundle.getBundle("i18n", Locale.getDefault());

	public final ToggleGroup modeRadioGroup = new ToggleGroup();
	public final Button chooseFileButton = new Button();
	public final Button saveButton = new Button("Save Changes");
	public final Button revertButton = new Button("Revert Changes");
	public final Button advanceButton = new Button("Next Wallpaper");
	public final CheckBox randomCB = new CheckBox(" Randomize List");
	public final CheckBox recurseCB = new CheckBox("Recurse Subdirectories");
	public final TextField changeDelay = new TextField("");
	public final TextField currentSelectionTextField = new TextField();
	public final ImageViewExt previewImageView = new ImageViewExt();
	public final ListView<String> fileListView = new ListView<>();

	public WallpaperUIElements(GridPane rootPane, Stage primary) throws Exception {

		int colIdx = 0;
		primary.setTitle("Wallpaper Randomizer Settings");

		RadioButton singleFileRButton = new RadioButton(resourceBundle.getString("button.radio.label.singleFileRadioButton"));
		RadioButton multiFileRButton = new RadioButton(resourceBundle.getString("button.radio.label.multiFileRadioButton"));
		RadioButton singleDirRButton = new RadioButton(resourceBundle.getString("button.radio.label.directoryRadioButton"));
		singleFileRButton.setToggleGroup(modeRadioGroup);
		singleFileRButton.setUserData(Settings.MODE_SINGLE_FILE);
		multiFileRButton.setToggleGroup(modeRadioGroup);
		multiFileRButton.setUserData(Settings.MODE_MULTI_FILE);
		singleDirRButton.setToggleGroup(modeRadioGroup);
		singleDirRButton.setUserData(Settings.MODE_SINGLE_DIR);

		HBox modeHBox = new HBox();
		VBox modeVBox = new VBox();
		modeVBox.getChildren().add(new Label(resourceBundle.getString("button.radio.group.label.fileModeRadioGroup")));
		modeVBox.getChildren().add(singleFileRButton);
		modeVBox.getChildren().add(multiFileRButton);
		modeVBox.getChildren().add(singleDirRButton);
		modeHBox.getChildren().add(modeVBox);
		modeVBox.getChildren().add(chooseFileButton);
		modeVBox.setSpacing(5);

		VBox optVBox = new VBox();
		randomCB.setAllowIndeterminate(false);

		recurseCB.setAllowIndeterminate(false);
		optVBox.getChildren().add(randomCB);
		optVBox.getChildren().add(recurseCB);
		optVBox.setSpacing(5);

		HBox delayHBox = new HBox();
		delayHBox.getChildren().add(new Label(resourceBundle.getString("text.field.label.delayChangeTextField")));
		delayHBox.getChildren().add(changeDelay);
		optVBox.getChildren().add(delayHBox);

		modeHBox.getChildren().add(optVBox);

		VBox buttonVBox = new VBox();
		saveButton.setDisable(true);
		buttonVBox.getChildren().add(saveButton);
		buttonVBox.getChildren().add(revertButton);
		buttonVBox.getChildren().add(advanceButton);
		buttonVBox.setSpacing(10);

		modeHBox.getChildren().add(buttonVBox);
		modeHBox.setSpacing(10);
		rootPane.add(modeHBox, 0, colIdx++);

		GridPane.setColumnSpan(currentSelectionTextField, 2);
		rootPane.add(currentSelectionTextField, 0, colIdx++);

		previewImageView.setPreserveRatio(true);
		GridPane.setColumnSpan(previewImageView, 2);
		rootPane.add(previewImageView, 0, colIdx);

		fileListView.setEditable(false);
		rootPane.add(fileListView, 0, colIdx++);
	}

	public void initializeState(Settings settings) {
		setSelectedRadioButton(modeRadioGroup, settings);

		randomCB.setSelected(settings.isRandomizeList());
		randomCB.setDisable(settings.getCurrentMode() == Settings.MODE_SINGLE_FILE);

		recurseCB.setSelected(settings.isRecurseSubDirs());
		recurseCB.setDisable(settings.getCurrentMode() != Settings.MODE_SINGLE_DIR);

		changeDelay.setText(Integer.toString(settings.getChangeDelay() / 1000));

		currentSelectionTextField.setEditable(false);
		if (settings.getFilePath() != null) {
			currentSelectionTextField.setText(settings.getFilePath());
		} else {
			currentSelectionTextField.setText(settings.getCurrentDir());
		}

		chooseFileButton.setText(this.getChooseButtonText(settings.getCurrentMode()));
		advanceButton.setDisable(true);
		// TODO: advance button to advance list
		// advanceButton.setDisable(settings.getCurrentMode() ==
		// Settings.MODE_SINGLE_FILE);

		previewImageView.setVisible(settings.getCurrentMode() == Settings.MODE_SINGLE_FILE || settings.getCurrentMode() == Settings.MODE_SINGLE_DIR);
		previewImageView.setImage(settings.getFilePath());

		fileListView.setVisible(settings.getCurrentMode() == Settings.MODE_MULTI_FILE);
		fileListView.getItems().addAll(settings.getFileList());
	}

	public void addEventProcessors(Stage primary, Settings unsavedSettings, Settings savedSettings) {
		randomCB.setOnAction(ae -> {
			unsavedSettings.setRandomizeList(randomCB.isSelected());
			saveButton.setDisable(false);
		});

		recurseCB.setOnAction(ae -> {
			unsavedSettings.setRecurseSubDirs(recurseCB.isSelected());
			saveButton.setDisable(false);
		});

		chooseFileButton.setOnAction(new FileChoiceEventHandler(primary, unsavedSettings, saveButton));
		saveButton.setOnAction(new SaveChangesButtonEventHandler(unsavedSettings, savedSettings));
		revertButton.setOnAction(new RevertButtonEventHandler(this, unsavedSettings, savedSettings));

		modeRadioGroup.selectedToggleProperty().addListener((x, y, newToggle) -> {
			currentSelectionTextField.setText(unsavedSettings.getFilePath());
			int newVal = ((Integer) newToggle.getUserData()).intValue();
			unsavedSettings.setCurrentMode(newVal);
			saveButton.setDisable(false);

			boolean showImage = true;
			boolean recurse = false;
			boolean random = true;
			String currentSelectionText = "";

			if (newVal == Settings.MODE_SINGLE_FILE) {
				currentSelectionText = unsavedSettings.getFilePath();
				fileListView.getItems().clear();
				unsavedSettings.setFileList(null);
				random = false;
			} else if (newVal == Settings.MODE_SINGLE_DIR) {
				currentSelectionText = unsavedSettings.getCurrentDir();
				recurse = true;
			} else if (newVal == Settings.MODE_MULTI_FILE) {
				if (CollectionUtils.isNotEmpty(unsavedSettings.getFileList())) {
					currentSelectionText = unsavedSettings.getFileList().get(0);
				}
				showImage = false;
			}

			recurseCB.setDisable(!recurse);
			randomCB.setDisable(!random);
			saveButton.setDisable(false);
			previewImageView.setImage(unsavedSettings.getFilePath());
			previewImageView.setVisible(showImage);
			fileListView.setVisible(!showImage);
			chooseFileButton.setText(this.getChooseButtonText(unsavedSettings.getCurrentMode()));
			currentSelectionTextField.setText(currentSelectionText);
		});

		changeDelay.addEventHandler(KeyEvent.KEY_RELEASED, ae -> {
			int newVal = 60 * 60;
			try {
				newVal = StringUtils.isNotEmpty(changeDelay.getText()) ? Integer.parseInt(changeDelay.getText()) : newVal;
				saveButton.setDisable(false);
			} catch (Exception e) {
				logger.error("Unable to parse value: " + changeDelay.getText());
				logger.error(e.getMessage());
			}
			unsavedSettings.setChangeDelay(1000 * newVal);
		});

		unsavedSettings.addObserver(new SettingsUpdateObserver(fileListView));
		unsavedSettings.addObserver(previewImageView);

		// TODO: better programmatic handling of spacing for image view?

		int widthSpacing = 80;
		int heightSpacing = 300;

		primary.addEventHandler(WindowEvent.ANY, x -> {
			previewImageView.setFitWidth(primary.getWidth() - widthSpacing);
			previewImageView.setFitHeight(primary.getHeight() - heightSpacing);
		});

		primary.widthProperty().addListener((x, y, z) -> {
			previewImageView.setFitWidth(primary.getWidth() - widthSpacing);
			previewImageView.setFitHeight(primary.getHeight() - heightSpacing);
		});
		primary.heightProperty().addListener((x, y, z) -> {
			previewImageView.setFitWidth(primary.getWidth() - widthSpacing);
			previewImageView.setFitHeight(primary.getHeight() - heightSpacing);
		});

	}

	private String getChooseButtonText(int currentMode) {
		String newFileButtonText = null;

		if (currentMode == Settings.MODE_SINGLE_FILE) {
			newFileButtonText = resourceBundle.getString("button.label.dynamic.chooseFileButton.singleFileMode");
		} else if (currentMode == Settings.MODE_SINGLE_DIR) {
			newFileButtonText = resourceBundle.getString("button.label.dynamic.chooseFileButton.singleDirMode");
		} else if (currentMode == Settings.MODE_MULTI_FILE) {
			newFileButtonText = resourceBundle.getString("button.label.dynamic.chooseFileButton.multiFileMode");
		}
		return newFileButtonText;
	}

	private void setSelectedRadioButton(ToggleGroup group, Settings settings) {
		for (Toggle button : group.getToggles()) {
			if (settings.getCurrentMode() == ((Integer) button.getUserData()).intValue()) {
				button.setSelected(true);
				return;
			}
		}
	}

}
