/*
 * Copyright (C) 2014 たんらる
 */

package fourthline.mabiicco.fx;

import fourthline.mabiicco.fx.ColorPaletteFX;
import fourthline.mabiicco.ui.IMMLManager;
import fourthline.mmlTools.MMLEventList;
import fourthline.mmlTools.MMLNoteEvent;
import fourthline.mmlTools.MMLScore;
import fourthline.mmlTools.MMLTrack;
import fourthline.mmlTools.UndefinedTickException;
import fourthline.mmlTools.core.MMLTicks;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.paint.*;

/**
 * FXML Controller class
 */
public class MainFrameController implements Initializable {

	public ScrollPane pianoRollView;
	public Canvas pianoRollViewCanvas;

	public static final int HEIGHT_C = 6;
	public static final int OCTNUM = 9;

	private IMMLManager mmlManager;

	private double wideScale = 6; // ピアノロールの拡大/縮小率 (1~6)

	private static final Color wKeyColor = new Color(0.9, 0.9, 0.9, 1.0); // 白鍵盤用
	private static final Color bKeyColor = new Color(0.8, 0.8, 0.8, 1.0); // 黒鍵盤用
	private static final Color borderColor = new Color(0.6, 0.6, 0.6, 1.0); // 境界線用
	private static final Color keyColors[] = new Color[] {
		wKeyColor, 
		bKeyColor, 
		wKeyColor, 
		bKeyColor, 
		wKeyColor, 
		bKeyColor, 
		wKeyColor, 
		wKeyColor, 
		bKeyColor, 
		wKeyColor, 
		bKeyColor, 
		wKeyColor
	};

	private static final Color barBorder = new Color(0.5f, 0.5f, 0.5f, 1.0);
	private static final Color darkBarBorder = new Color(0.3f, 0.2f, 0.3f, 1.0);


	/**
	 * Initializes the controller class.
	 * @param url
	 * @param rb
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		mmlManager = new MMLScoreContext();
		GraphicsContext gc = pianoRollViewCanvas.getGraphicsContext2D();
		paint(gc);
	}

	private int getWidth() {
		return 1000;
	}
	private int getHeight() {
		return 649;
	}

	public long convertXtoTick(int x) {
		return (long)(x * wideScale);
	}

	public int convertTicktoX(long tick) {
		return (int)(tick / wideScale);
	}

	private void updateViewWidthTrackLength() {
		pianoRollViewCanvas.setWidth(getWidth());
		pianoRollViewCanvas.setHeight(getHeight());
	}

	private void paint(GraphicsContext gc) {
		updateViewWidthTrackLength();

		for (int i = 0; i <= 8; i++) {
			paintOctPianoLine(gc, i, (char)('0'+8-i-1));
		}

		paintMeasure(gc);

		MMLScore mmlScore = mmlManager.getMMLScore();
		if (mmlScore != null) {
			int i = 0;
			for (MMLTrack track : mmlScore.getTrackList()) {
				paintMusicScoreWithoutActiveTrack(gc, i++, track);
			}
		}

		paintActiveTrack(gc);
	}

	private void paintOctPianoLine(GraphicsContext gc, int pos, char posText) {
		int startY = 12 * HEIGHT_C * pos;

		// グリッド
		int y = startY;
		int width = getWidth();
		gc.setFill(Color.BLUE);
		gc.fillRect(0, y, width, 1);
		for (int i = 0; i < 12; i++) {
			Color fillColor = keyColors[i];
			gc.setFill(fillColor);
			gc.fillRect(0, i*HEIGHT_C+y, width, HEIGHT_C);
			if (i == 0) {
				gc.setFill(darkBarBorder);
			} else {
				gc.setFill(borderColor);
			}
			gc.fillRect(0, i*HEIGHT_C+y, width, 1);
		}
	}

	/**
	 * メジャーを表示します。
	 */
	private void paintMeasure(GraphicsContext gc) {
		int width = (int)convertXtoTick(getWidth());
		try {
			int sect = MMLTicks.getTick(mmlManager.getMMLScore().getBaseOnly());
			int borderCount = mmlManager.getMMLScore().getTimeCountOnly();
			for (int i = 0; i*sect < width; i++) {
				int x = convertTicktoX(i*sect);
				int y1 = 0;
				int y2 = getHeight();
				if (i%borderCount == 0) {
					gc.setFill(darkBarBorder);
				} else {
					gc.setFill(barBorder);
				}
				gc.fillRect(x, y1, 1, y2);
			}
		} catch (UndefinedTickException e) {
			e.printStackTrace();
		}
	}


	/**
	 * 1トラック分のロールを表示します。（アクティブトラックは表示しない）
	 * @param g
	 * @param index トラックindex
	 */
	private void paintMusicScoreWithoutActiveTrack(GraphicsContext gc, int index, MMLTrack track) {
		int trackIndex = mmlManager.getActiveTrackIndex();
		MMLTrack activeTrack = mmlManager.getMMLScore().getTrack(trackIndex);
		if (track == activeTrack) {
			return;
		}

		int part = 0;
		for (MMLEventList targetPart : track.getMMLEventList()) {
			ColorPaletteFX partColor = ColorPaletteFX.getColorType(part++);
			Color rectColor = partColor.getRectColor(index);
			Color fillColor = partColor.getFillColor(index);
			paintMMLPart(gc, targetPart.getMMLNoteEventList(), rectColor, fillColor, false);
		}
	}

	/**
	 * MMLEventリストのロールを表示します。
	 * @param g
	 * @param mmlPart
	 * @return
	 */
	private void paintMMLPart(GraphicsContext gc, List<MMLNoteEvent> mmlPart, Color rectColor, Color fillColor, boolean drawOption) {
		// 現在のView範囲のみを描画する.
		for (MMLNoteEvent noteEvent : mmlPart) {
			drawNote(gc, noteEvent, rectColor, fillColor, drawOption);
		}
	}

	private void drawNote(GraphicsContext gc, MMLNoteEvent noteEvent, Color rectColor, Color fillColor, boolean drawOption) {
		int note = noteEvent.getNote();
		int tick = noteEvent.getTick();
		int offset = noteEvent.getTickOffset();
		int x = convertTicktoX(offset);
		int y = getHeight() - ((note +1) * HEIGHT_C);
		int width = convertTicktoX(tick) -1;
		int height = HEIGHT_C-2;

		gc.setFill(fillColor);
		gc.fillRect(x, y, width, height);

		if (drawOption) {
			// velocityの描画.
			int velocity = noteEvent.getVelocity();
			if (velocity >= 0) {
				String s = "V" + velocity;
				gc.fillText(s, x, y);
			}
		}
	}

	private void paintActiveTrack(GraphicsContext gc) {
		MMLEventList activePart = mmlManager.getActiveMMLPart();
		int trackIndex = mmlManager.getActiveTrackIndex();
		MMLTrack activeTrack = mmlManager.getMMLScore().getTrack(trackIndex);

		int part = 0;
		for (MMLEventList targetPart : activeTrack.getMMLEventList()) {
			ColorPaletteFX partColor = ColorPaletteFX.getColorType(part++);
			if (targetPart != activePart) {
				Color rectColor = partColor.getRectColor(trackIndex);
				Color fillColor = partColor.getFillColor(trackIndex);
				paintMMLPart(gc, targetPart.getMMLNoteEventList(), rectColor, fillColor, false);
			}
		}

		Color rectColor = ColorPaletteFX.ACTIVE.getRectColor(trackIndex);
		Color fillColor = ColorPaletteFX.ACTIVE.getFillColor(trackIndex);

		paintMMLPart(gc, activePart.getMMLNoteEventList(), rectColor, fillColor, true);
	}
}
