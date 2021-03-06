/*
 * Copyright (C) 2013-2014 たんらる
 */

package fourthline.mabiicco.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JCheckBox;

import fourthline.mabiicco.AppResource;
import fourthline.mmlTools.MMLNoteEvent;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;


/**
 * ノートプロパティを編集するためのダイアログ表示で用いるPanelです.
 */
public class MMLNotePropertyPanel extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 646262293010195918L;

	private JSpinner velocityValueField;
	private JCheckBox velocityCheckBox;
	private JCheckBox tuningNoteCheckBox;
	private MMLNoteEvent noteEvent[];

	public void showDialog() {
		int status = JOptionPane.showConfirmDialog(null, 
				this,
				AppResource.getText("note.properties"),
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);

		if (status == JOptionPane.OK_OPTION) {
			applyProperty();
		}
	}

	/**
	 * Create the panel.
	 */
	public MMLNotePropertyPanel() {
		this(null);
	}

	/**
	 * Create the panel.
	 */
	public MMLNotePropertyPanel(MMLNoteEvent noteEvent[]) {
		super();
		setLayout(null);

		velocityValueField = new JSpinner();
		velocityValueField.setModel(new SpinnerNumberModel(8, 0, 15, 1));
		velocityValueField.setBounds(209, 37, 72, 19);
		add(velocityValueField);

		velocityCheckBox = new JCheckBox(AppResource.getText("note.properties.velocity"));
		velocityCheckBox.setBounds(42, 36, 150, 21);
		velocityCheckBox.addActionListener(this);
		add(velocityCheckBox);

		tuningNoteCheckBox = new JCheckBox(AppResource.getText("note.properties.tuning"));
		tuningNoteCheckBox.setBounds(43, 99, 220, 21);
		add(tuningNoteCheckBox);

		setNoteEvent(noteEvent);
	}

	private void setNoteEvent(MMLNoteEvent noteEvent[]) {
		this.noteEvent = noteEvent;
		if (noteEvent == null) {
			return;
		}

		tuningNoteCheckBox.setSelected( noteEvent[0].isTuningNote() );

		int velocity = noteEvent[0].getVelocity();
		if (velocity >= 0) {
			velocityCheckBox.setSelected(true);
			velocityValueField.setEnabled(true);
			velocityValueField.setValue(velocity);
		} else {
			velocityCheckBox.setSelected(false);
			velocityValueField.setEnabled(false);
		}
	}

	/**
	 * パネルの情報をノートに反映します.
	 */
	public void applyProperty() {
		for (MMLNoteEvent event : noteEvent) {
			if (tuningNoteCheckBox.isSelected()) {
				event.setTuningNote(true);
			} else {
				event.setTuningNote(false);
			}

			if (velocityCheckBox.isSelected()) {
				Integer value = (Integer) velocityValueField.getValue();
				event.setVelocity(value);
			} else {
				event.setVelocity(MMLNoteEvent.NO_VEL);
			}
		}
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(350, 150);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == velocityCheckBox) {
			velocityValueField.setEnabled( velocityCheckBox.isSelected() );
		}
	}
}
