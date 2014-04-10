/*
 * Copyright (C) 2014 たんらる
 */

package fourthline.mabiicco.fx;

import java.io.FileInputStream;

import fourthline.mabiicco.ui.IMMLManager;
import fourthline.mmlTools.MMLEventList;
import fourthline.mmlTools.MMLScore;

public class MMLScoreContext implements IMMLManager {

	private MMLScore mmlScore;

	public MMLScoreContext() {
		mmlScore = new MMLScore();
		try {
			mmlScore.parse(new FileInputStream("sample.mmi"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public MMLScore getMMLScore() {
		return mmlScore;
	}

	@Override
	public int getActiveTrackIndex() {
		return 0;
	}

	@Override
	public MMLEventList getActiveMMLPart() {
		return mmlScore.getTrack(0).getMMLEventList().get(0);
	}

	@Override
	public void updateActivePart() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void updateActiveTrackProgram(int program, int songProgram) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int getActivePartProgram() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
