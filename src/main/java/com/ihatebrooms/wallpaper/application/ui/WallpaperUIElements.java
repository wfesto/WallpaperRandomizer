package com.ihatebrooms.wallpaper.application.ui;

import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ihatebrooms.wallpaper.application.ui.controls.FileListButtonController;
import com.ihatebrooms.wallpaper.data.Settings;
import com.ihatebrooms.wallpaper.event.handler.FileChoiceEventHandler;
import com.ihatebrooms.wallpaper.event.handler.SaveChangesButtonEventHandler;
import com.ihatebrooms.wallpaper.ext.javafx.scene.image.ImageViewExt;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
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
//TODO: consider using quartz to allow more flexible scheduling options (once per day, specific time, etc)
public class WallpaperUIElements {

	private static final Logger logger = LogManager.getLogger(WallpaperUIElements.class);

	protected final ResourceBundle resourceBundle;
	public final ToggleGroup modeRadioGroup;
	public final Button chooseFileButton;
	public final Button saveButton;
	public final Button revertButton;
	public final Button advanceButton;
	public final CheckBox randomCB;
	public final CheckBox recurseCB;
	public final CheckBox duplicateCB;
	public final TextField changeDelay;
	public final ToggleGroup changeDelayModeGroup;
	public final TextField currentSelectionTextField;
	public final ImageViewExt previewImageView;
	public final ListView<String> fileListView;
	public final Button listUpButton;
	public final Button listDownButton;
	public final Button deleteListEntryButton;

	public WallpaperUIElements(ResourceBundle resourceBundle, GridPane rootPane, Stage primary) throws Exception {
		this.resourceBundle = resourceBundle;

		modeRadioGroup = new ToggleGroup();
		chooseFileButton = new Button();
		saveButton = new Button(resourceBundle.getString("button.label.saveChangesButton"));
		revertButton = new Button(resourceBundle.getString("button.label.revertChangesButton"));
		advanceButton = new Button(resourceBundle.getString("button.label.advanceWallpaperButton"));
		randomCB = new CheckBox(resourceBundle.getString("checkbox.label.randomizeListCB"));
		recurseCB = new CheckBox(resourceBundle.getString("checkbox.label.recurseSubDirsCB"));
		duplicateCB = new CheckBox(resourceBundle.getString("checkbox.label.allowDuplicatesCB"));
		changeDelay = new TextField("");
		changeDelayModeGroup = new ToggleGroup();
		currentSelectionTextField = new TextField();
		previewImageView = new ImageViewExt();
		fileListView = new ListView<>();
		listUpButton = new Button(resourceBundle.getString("button.label.listViewButtons.up"));
		listDownButton = new Button(resourceBundle.getString("button.label.listViewButtons.down"));
		deleteListEntryButton = new Button(resourceBundle.getString("button.label.listViewButtons.delete"));

		int colIdx = 0;
		primary.setTitle(resourceBundle.getString("window.label.title"));

		RadioButton singleFileRButton = new RadioButton(resourceBundle.getString("button.radio.label.mode.singleFileRadioButton"));
		RadioButton multiFileRButton = new RadioButton(resourceBundle.getString("button.radio.label.mode.multiFileRadioButton"));
		RadioButton singleDirRButton = new RadioButton(resourceBundle.getString("button.radio.label.mode.directoryRadioButton"));
		singleFileRButton.setToggleGroup(modeRadioGroup);
		singleFileRButton.setUserData(Settings.MODE_SINGLE_FILE);
		multiFileRButton.setToggleGroup(modeRadioGroup);
		multiFileRButton.setUserData(Settings.MODE_MULTI_FILE);
		singleDirRButton.setToggleGroup(modeRadioGroup);
		singleDirRButton.setUserData(Settings.MODE_SINGLE_DIR);

		HBox modeHBox = new HBox();
		VBox modeVBox = new VBox();
		modeVBox.getChildren().add(new Label(resourceBundle.getString("button.radio.group.label.mode.fileModeRadioGroup")));
		modeVBox.getChildren().add(singleFileRButton);
		modeVBox.getChildren().add(multiFileRButton);
		modeVBox.getChildren().add(singleDirRButton);
		modeHBox.getChildren().add(modeVBox);
		modeVBox.getChildren().add(chooseFileButton);
		modeVBox.setSpacing(5);

		VBox optVBox = new VBox();
		randomCB.setAllowIndeterminate(false);
		recurseCB.setAllowIndeterminate(false);
		duplicateCB.setAllowIndeterminate(false);

		optVBox.getChildren().add(randomCB);
		optVBox.getChildren().add(recurseCB);
		optVBox.getChildren().add(duplicateCB);
		optVBox.setSpacing(5);

		HBox delayHBox = new HBox();
		delayHBox.getChildren().add(new Label(resourceBundle.getString("text.field.label.delayChangeTextField")));
		delayHBox.getChildren().add(changeDelay);
		delayHBox.setSpacing(5);
		optVBox.getChildren().add(delayHBox);

		HBox delayModeHBox = new HBox();
		RadioButton delayModeSecondsRButton = new RadioButton(resourceBundle.getString("button.radio.label.delay.delayModeSecondsRadioButton"));
		RadioButton delayModeMinutesRButton = new RadioButton(resourceBundle.getString("button.radio.label.delay.delayModeMinutesRadioButton"));
		RadioButton delayModeHoursRButton = new RadioButton(resourceBundle.getString("button.radio.label.delay.delayModeHoursRadioButton"));
		delayModeSecondsRButton.setToggleGroup(changeDelayModeGroup);
		delayModeSecondsRButton.setUserData(Settings.MODE_DELAY_SECONDS);
		delayModeMinutesRButton.setToggleGroup(changeDelayModeGroup);
		delayModeMinutesRButton.setUserData(Settings.MODE_DELAY_MINUTES);
		delayModeHoursRButton.setToggleGroup(changeDelayModeGroup);
		delayModeHoursRButton.setUserData(Settings.MODE_DELAY_HOURS);
		delayModeHBox.getChildren().add(delayModeSecondsRButton);
		delayModeHBox.getChildren().add(delayModeMinutesRButton);
		delayModeHBox.getChildren().add(delayModeHoursRButton);
		delayModeHBox.setSpacing(5);

		changeDelay.disableProperty().addListener((arg0, arg1, newValue) -> delayModeHBox.setDisable(newValue));

		optVBox.getChildren().add(delayModeHBox);
		modeHBox.getChildren().add(optVBox);

		VBox buttonVBox = new VBox();
		saveButton.setDisable(true);
		revertButton.setDisable(true);
		buttonVBox.getChildren().add(saveButton);
		buttonVBox.getChildren().add(revertButton);
		buttonVBox.getChildren().add(advanceButton);
		buttonVBox.setSpacing(10);

		saveButton.disableProperty().addListener((arg0, arg1, newValue) -> revertButton.setDisable(newValue));

		modeHBox.getChildren().add(buttonVBox);
		modeHBox.setSpacing(10);
		rootPane.add(modeHBox, 0, colIdx++);

		currentSelectionTextField.setEditable(false);
		GridPane.setColumnSpan(currentSelectionTextField, 2);
		rootPane.add(currentSelectionTextField, 0, colIdx++);

		previewImageView.setPreserveRatio(true);
		GridPane.setColumnSpan(previewImageView, 2);
		rootPane.add(previewImageView, 0, colIdx);

		fileListView.setEditable(false);
		fileListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		rootPane.add(fileListView, 0, colIdx);

		VBox listViewButtons = new VBox();
		listViewButtons.setSpacing(5);
		listViewButtons.getChildren().add(listUpButton);
		listViewButtons.getChildren().add(listDownButton);
		listViewButtons.getChildren().add(deleteListEntryButton);
		rootPane.add(listViewButtons, 1, colIdx++);

		previewImageView.visibleProperty().addListener((arg0, arg1, newValue) -> fileListView.setVisible(!newValue));
		fileListView.visibleProperty().addListener((arg0, arg1, newValue) -> listViewButtons.setVisible(newValue));
	}

	public void initializeState(Settings settings) {
		setSelectedRadioButton(modeRadioGroup, settings.getCurrentMode());
		setSelectedRadioButton(changeDelayModeGroup, settings.getChangeDelayMode());

		randomCB.setSelected(settings.isRandomizeList());
		randomCB.setDisable(settings.getCurrentMode() == Settings.MODE_SINGLE_FILE);

		recurseCB.setSelected(settings.isRecurseSubDirs());
		recurseCB.setDisable(settings.getCurrentMode() != Settings.MODE_SINGLE_DIR);

		duplicateCB.setSelected(settings.isAllowDuplicates());
		duplicateCB.setDisable(settings.getCurrentMode() != Settings.MODE_MULTI_FILE);

		changeDelay.setText(Integer.toString(settings.getChangeDelay()));
		changeDelay.setDisable(settings.getCurrentMode() == Settings.MODE_SINGLE_FILE);

		if (settings.getCurrentMode() == Settings.MODE_SINGLE_DIR) {
			currentSelectionTextField.setText(settings.getCurrentDir());
		} else {
			currentSelectionTextField.setText(settings.getFilePath().get());
		}

		chooseFileButton.setText(this.getChooseButtonText(settings.getCurrentMode()));
		advanceButton.setDisable(settings.getCurrentMode() == Settings.MODE_SINGLE_FILE);

		previewImageView.setVisible(settings.getCurrentMode() == Settings.MODE_SINGLE_FILE || settings.getCurrentMode() == Settings.MODE_SINGLE_DIR);
		previewImageView.setImage(settings.getFilePath());

		fileListView.setVisible(settings.getCurrentMode() == Settings.MODE_MULTI_FILE);
		fileListView.setItems(settings.getObservedList());
		fileListView.refresh();
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

		duplicateCB.setOnAction(ae -> {
			unsavedSettings.setAllowDuplicates(duplicateCB.isSelected());
			saveButton.setDisable(false);
		});

		modeRadioGroup.selectedToggleProperty().addListener((x, y, newToggle) -> {
			unsavedSettings.setCurrentMode(((Integer) newToggle.getUserData()).intValue());
			saveButton.setDisable(false);
			initializeState(unsavedSettings);
		});

		changeDelay.addEventHandler(KeyEvent.KEY_RELEASED, ae -> {
			String newText = changeDelay.getText();
			try {
				if (StringUtils.isNumeric(newText)) {
					saveButton.setDisable(false);
					unsavedSettings.setChangeDelay(Integer.parseInt(newText));
				}
			} catch (Exception e) {
				logger.error("Unable to parse value: " + newText);
			}
		});

		changeDelayModeGroup.selectedToggleProperty().addListener((x, y, newToggle) -> {
			int newVal = ((Integer) newToggle.getUserData()).intValue();
			unsavedSettings.setChangeDelayMode(newVal);
			saveButton.setDisable(false);
		});

		revertButton.setOnAction(ae -> {
			saveButton.setDisable(true);
			initializeState(savedSettings);
			unsavedSettings.copyFrom(savedSettings);
		});

		unsavedSettings.getFilePath().addListener(previewImageView);
		chooseFileButton.setOnAction(new FileChoiceEventHandler(primary, unsavedSettings, saveButton));

		FileListButtonController fileListButtonController = new FileListButtonController(this);
		listUpButton.setOnAction(fileListButtonController);
		listDownButton.setOnAction(fileListButtonController);
		deleteListEntryButton.setOnAction(fileListButtonController);

		SaveChangesButtonEventHandler buttonHandler = new SaveChangesButtonEventHandler(unsavedSettings, savedSettings, saveButton, resourceBundle);
		saveButton.setOnAction(buttonHandler);
		advanceButton.setOnAction(buttonHandler);

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

	private void setSelectedRadioButton(ToggleGroup group, int selectedValue) {
		for (Toggle button : group.getToggles()) {
			if (selectedValue == ((Integer) button.getUserData()).intValue()) {
				button.setSelected(true);
				return;
			}
		}
	}
}
