/*
 * Copyright (C) 2014 たんらる
 */

package fourthline.mabiicco;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import fourthline.mabiicco.midi.MabiDLS;
import fourthline.mabiicco.ui.MMLScorePropertyPanel;
import fourthline.mabiicco.ui.MMLSeqView;
import fourthline.mabiicco.ui.MainFrame;
import fourthline.mmlTools.MMLScore;
import fourthline.mmlTools.parser.IMMLFileParser;
import fourthline.mmlTools.parser.MMLParseException;
import fourthline.mmlTools.parser.MMSFile;

public class ActionDispatcher implements ActionListener, IFileStateObserver, IEditStateObserver {

	private MainFrame mainFrame;
	private MMLSeqView mmlSeqView;
	private IFileState fileState;
	private IEditState editState;

	// action commands
	public static final String VIEW_SCALE_UP = "view_scale_up";
	public static final String VIEW_SCALE_DOWN = "view_scale_down";
	public static final String PLAY = "play";
	public static final String STOP = "stop";
	public static final String PAUSE = "pause";
	public static final String FILE_OPEN = "fileOpen";
	public static final String NEW_FILE = "newFile";
	public static final String RELOAD_FILE = "reloadFile";
	public static final String QUIT = "quit";
	public static final String ADD_TRACK = "addTrack";
	public static final String REMOVE_TRACK = "removeTrack";
	public static final String TRACK_PROPERTY = "trackProperty";
	public static final String SET_START_POSITION = "setStartPosition";
	public static final String INPUT_FROM_CLIPBOARD = "inputFromClipboard";
	public static final String OUTPUT_TO_CLIPBOARD = "outputToClipboard";
	public static final String UNDO = "undo";
	public static final String REDO = "redo";
	public static final String SAVE_FILE = "save_file";
	public static final String SAVEAS_FILE = "saveas_file";
	public static final String CUT = "cut";
	public static final String COPY = "copy";
	public static final String PASTE = "paste";
	public static final String DELETE = "delete";
	public static final String SCORE_PROPERTY = "score_property";
	public static final String NEXT_TIME = "next_time";
	public static final String PREV_TIME = "prev_time";
	public static final String PART_CHANGE = "part_change";
	public static final String CHANGE_NOTE_HEIGHT_INT = "change_note_height_";
	public static final String ADD_MEASURE = "add_measure";
	public static final String REMOVE_MEASURE = "remove_measure";
	public static final String NOTE_PROPERTY = "note_property";

	private File openedFile = null;

	private final FileFilter mmsFilter = new FileNameExtensionFilter(AppResource.getText("file.mms"), "mms");
	private final FileFilter mmiFilter = new FileNameExtensionFilter(AppResource.getText("file.mmi"), "mmi");
	private final FileFilter allFilter = new FileNameExtensionFilter(AppResource.getText("file.all"), "mmi", "mms");

	private final JFileChooser openFileChooser = new JFileChooser();
	private final JFileChooser saveFileChooser = new JFileChooser();

	private static ActionDispatcher instance = null;
	public static ActionDispatcher getInstance() {
		if (instance == null) {
			instance = new ActionDispatcher();
		}
		return instance;
	}

	private ActionDispatcher() {
		initializeFileChooser();
	}

	private void initializeFileChooser() {
		openFileChooser.addChoosableFileFilter(allFilter);
		openFileChooser.addChoosableFileFilter(mmiFilter);
		openFileChooser.addChoosableFileFilter(mmsFilter);
		saveFileChooser.addChoosableFileFilter(mmiFilter);
	}

	public void setMainFrame(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		this.mmlSeqView = mainFrame.getMMLSeqView();
		this.fileState = this.mmlSeqView.getFileState();
		this.editState = this.mmlSeqView.getEditState();

		this.fileState.setFileStateObserver(this);
		this.editState.setEditStateObserver(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

		if (command.equals(VIEW_SCALE_UP)) {
			mmlSeqView.expandPianoViewWide();
			mmlSeqView.repaint();
		} else if (command.equals(VIEW_SCALE_DOWN)) {
			mmlSeqView.reducePianoViewWide();
			mmlSeqView.repaint();
		} else if (command.equals(STOP)) {
			MabiDLS.getInstance().getSequencer().stop();
			MabiDLS.getInstance().clearAllChannelPanpot();
			mainFrame.enableNoplayItems();
		} else if (command.equals(PAUSE)) {
			pauseAction();
		} else if (command.equals(FILE_OPEN)) {
			if (checkCloseModifiedFileState()) {
				openMMLFileAction();
			}
		} else if (command.equals(NEW_FILE)) {
			if (checkCloseModifiedFileState()) {
				newMMLFileAction();
			}
		} else if (command.equals(RELOAD_FILE)) {
			reloadMMLFileAction();
		} else if (command.equals(QUIT)) {
			//  閉じる前に、変更が保存されていなければダイアログ表示する.
			if (checkCloseModifiedFileState()) {
				System.exit(0);
			}
		} else if (command.equals(ADD_TRACK)) {
			mmlSeqView.addMMLTrack(null);
		} else if (command.equals(REMOVE_TRACK)) {
			mmlSeqView.removeMMLTrack();
		} else if (command.equals(TRACK_PROPERTY)) {
			mmlSeqView.editTrackPropertyAction(mainFrame);
		} else if (command.equals(SET_START_POSITION)) {
			mmlSeqView.setStartPosition();
		} else if (command.equals(PLAY)) {
			playAction();
		} else if (command.equals(INPUT_FROM_CLIPBOARD)) {
			mmlSeqView.inputClipBoardAction(mainFrame);
		} else if (command.equals(OUTPUT_TO_CLIPBOARD)) {
			mmlSeqView.outputClipBoardAction(mainFrame);
		} else if (command.equals(UNDO)) {
			mmlSeqView.undo();
		} else if (command.equals(REDO)) {
			mmlSeqView.redo();
		} else if (command.equals(SAVE_FILE)) {
			saveMMLFile(openedFile);
		} else if (command.equals(SAVEAS_FILE)) {
			saveAsMMLFileAction();
		} else if (command.equals(CUT)) {
			editState.selectedCut();
		} else if (command.equals(COPY)) {
			editState.selectedCopy();
		} else if (command.equals(PASTE)) {
			editPasteAction();
		} else if (command.equals(DELETE)) {
			editState.selectedDelete();
		} else if (command.equals(SCORE_PROPERTY)) {
			scorePropertyAction();
		} else if (command.equals(NEXT_TIME)) {
			mmlSeqView.nextStepTimeTo(true);
		} else if (command.equals(PREV_TIME)) {
			mmlSeqView.nextStepTimeTo(false);
		} else if (command.equals(PART_CHANGE)) {
			mmlSeqView.partChange(mainFrame);
		} else if (command.startsWith(CHANGE_NOTE_HEIGHT_INT)) {
			int index = Integer.parseInt( command.substring(CHANGE_NOTE_HEIGHT_INT.length()) );
			mmlSeqView.setPianoRollHeightScaleIndex(index);
			MabiIccoProperties.getInstance().setPianoRollViewHeightScaleProperty(index);
		} else if (command.equals(ADD_MEASURE)) {
			mmlSeqView.addMeasure();
		} else if (command.equals(REMOVE_MEASURE)) {
			mmlSeqView.removeMeasure();
		} else if (command.equals(NOTE_PROPERTY)) {
			editState.noteProperty();
		}
	}

	public void openMMLFile(File file) {
		try {
			IMMLFileParser fileParser;
			if (file.toString().endsWith(".mms")) {
				fileParser = new MMSFile();
			} else {
				fileParser = new MMLScore();
			}
			MMLScore score = fileParser.parse(new FileInputStream(file));
			mmlSeqView.setMMLScore(score);

			openedFile = file;
			notifyUpdateFileState();
			MabiIccoProperties.getInstance().setRecentFile(file.getPath());
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(mainFrame, AppResource.getText("error.read"), AppResource.getText("error.nofile"), JOptionPane.WARNING_MESSAGE);
		} catch (MMLParseException e) {
			JOptionPane.showMessageDialog(mainFrame, AppResource.getText("error.read"), AppResource.getText("error.invalid_file"), JOptionPane.WARNING_MESSAGE);
		}
	}

	public void reloadMMLFileAction() {
		if (MabiDLS.getInstance().getSequencer().isRunning()) {
			return;
		}

		if (openedFile != null) {
			if (fileState.isModified()) {
				int status = JOptionPane.showConfirmDialog(mainFrame, AppResource.getText("message.throw"), "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (status == JOptionPane.YES_OPTION) {
					openMMLFile(openedFile);
				}
			}
		}
	}

	private void saveMMLFile(File file) {
		try {
			FileOutputStream outputStream = new FileOutputStream(file);
			mmlSeqView.getMMLScore().writeToOutputStream(outputStream);
			mainFrame.setTitleAndFileName(file.getName());
			fileState.setOriginalBase();
			notifyUpdateFileState();
			MabiIccoProperties.getInstance().setRecentFile(file.getPath());
		} catch (Exception e) {}
	}

	private void newMMLFileAction() {
		openedFile = null;
		mmlSeqView.initializeMMLTrack();
		notifyUpdateFileState();
	}

	private void openMMLFileAction() {
		if (MabiDLS.getInstance().getSequencer().isRunning()) {
			return;
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				String recentPath = MabiIccoProperties.getInstance().getRecentFile();
				openFileChooser.setCurrentDirectory(new File(recentPath));
				openFileChooser.setFileFilter(allFilter);
				openFileChooser.setAcceptAllFileFilterUsed(false);
				int status = openFileChooser.showOpenDialog(mainFrame);
				if (status == JFileChooser.APPROVE_OPTION) {
					File file = openFileChooser.getSelectedFile();
					openMMLFile(file);
				}
			}
		});
	}

	private void saveAsMMLFileAction() {
		if (MabiDLS.getInstance().getSequencer().isRunning()) {
			return;
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				showDialogSaveFile();
				notifyUpdateFileState();
			}
		});
	}

	/**
	 * 別名保存のダイアログ表示
	 * @return 保存した場合は trueを返す.
	 */
	private boolean showDialogSaveFile() {
		String recentPath = MabiIccoProperties.getInstance().getRecentFile();
		if (openedFile != null) {
			saveFileChooser.setSelectedFile(openedFile);
		} else {
			saveFileChooser.setCurrentDirectory(new File(recentPath));
		}
		saveFileChooser.setFileFilter(mmiFilter);
		saveFileChooser.setAcceptAllFileFilterUsed(false);
		int status = saveFileChooser.showSaveDialog(mainFrame);
		if (status == JFileChooser.APPROVE_OPTION) {
			File file = saveFileChooser.getSelectedFile();
			if (!file.toString().endsWith(".mmi")) {
				file = new File(file+".mmi");
			}

			status = JOptionPane.YES_OPTION;
			if (file.exists()) {
				// すでにファイルが存在する場合の上書き警告表示.
				status = JOptionPane.showConfirmDialog(mainFrame, AppResource.getText("message.override"), "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			}
			if (status == JOptionPane.YES_OPTION) {
				saveMMLFile(file);
				openedFile = file;
				return true;
			}
		}

		return false;
	}

	/**
	 * ファイルの変更状態をみて、アプリケーション終了ができるかどうかをチェックする.
	 * @return 終了できる状態であれば、trueを返す.
	 */
	private boolean checkCloseModifiedFileState() {
		if (!fileState.isModified()) {
			// 保存が必要な変更なし.
			return true;
		}

		// 保存するかどうかのダイアログ表示
		int status = JOptionPane.showConfirmDialog(mainFrame, AppResource.getText("message.modifiedClose"), "", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (status == JOptionPane.CANCEL_OPTION) {
			return false;
		} else if (status == JOptionPane.NO_OPTION) {
			return true;
		}

		if (openedFile == null) {
			// 新規ファイルなので、別名保存.
			if (showDialogSaveFile()) {
				return true;
			}
		} else {
			if (isSupportedSaveFile()) {
				// 上書き保存可.
				saveMMLFile(openedFile);
				return true;
			} else if (showDialogSaveFile()) {
				// ファイルOpenされているが、サポート外なので別名保存.
				return true;
			}
		}

		return false;
	}

	private boolean isSupportedSaveFile() {
		if (openedFile != null) {
			if (openedFile.getName().endsWith(".mmi")) {
				return true;
			}
		}

		return false;
	}

	private void scorePropertyAction() {
		MMLScorePropertyPanel propertyPanel = new MMLScorePropertyPanel();
		propertyPanel.showDialog(mainFrame, mmlSeqView.getMMLScore());
		mmlSeqView.repaint();
	}

	private void editPasteAction() {
		long startTick = mmlSeqView.getEditSequencePosition();
		editState.paste(startTick);
	}

	private void pauseAction() {
		MabiDLS.getInstance().getSequencer().stop();
		MabiDLS.getInstance().clearAllChannelPanpot();
		mmlSeqView.pauseTickPosition();
		mainFrame.enableNoplayItems();
	}

	private void playAction() {
		if (MabiDLS.getInstance().getSequencer().isRunning()) {
			pauseAction();
		} else {
			mmlSeqView.startSequence();
			mainFrame.disableNoplayItems();
		}
	}

	@Override
	public void notifyUpdateFileState() {
		mainFrame.setCanSaveFile(false);
		mainFrame.setTitleAndFileName(null);
		mainFrame.setCanReloadFile(false);
		if (openedFile != null) {
			if (fileState.isModified()) {
				// 上書き保存有効
				if (isSupportedSaveFile()) {
					mainFrame.setCanSaveFile(true);
				}
				mainFrame.setTitleAndFileName(openedFile.getName()+" "+AppResource.getText("file.modified"));
				mainFrame.setCanReloadFile(true);
			} else {
				mainFrame.setTitleAndFileName(openedFile.getName());
			}
		}

		// undo-UI更新
		mainFrame.setCanUndo(fileState.canUndo());

		// redo-UI更新
		mainFrame.setCanRedo(fileState.canRedo());
	}

	@Override
	public void notifyUpdateEditState() {
		mainFrame.setSelectedEdit(editState.hasSelectedNote());
		mainFrame.setPasteEnable(editState.canPaste());
	}
}
