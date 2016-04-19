package org.sidor.androidapps.simpletuner;

import java.util.Observable;
import java.util.Observer;

import org.sidor.androidapps.simpletuner.SoundAnalyzer.*;
import org.sidor.androidapps.simpletuner.Tuning.GuitarString;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;


public class UiController implements Observer, OnItemSelectedListener {
	public static final String TAG = "RealGuitarTuner";

	public static Tabbie ui;
	private double frequency;
	static public int freq = 0;
	private Tuning tuning = new Tuning(0);
	
	private enum MessageClass {
		TUNING_IN_PROGRESS,
		WEIRD_FREQUENCY,
		TOO_QUIET,
		TOO_NOISY,
	}
	
	MessageClass message;
	MessageClass previouslyProposedMessage;
	MessageClass proposedMessage; // needs to get X consecutive votes.
	private int numberOfVotes;
	private final int minNumberOfVotes = 3; // X.

	int line1 = 10;
	int line2 = 150;
	int line3 = 290;
	int line4 = 430;
	int line5 = 570;
	int line6 = 710;
	int fret1 = 100;
	int fret2 = 400;
	int fret3 = 720;
	int fret4 = 1010;
	int fret5 = 1280;
	int fret6 = 1530;

	public UiController(Tabbie u) {
		ui = u;
	}
	
	@Override
	public void update(Observable who, Object obj) {
		if(who instanceof SoundAnalyzer) {
			if(obj instanceof AnalyzedSound) {
				AnalyzedSound result = (AnalyzedSound)obj;
				// result.getDebug();
				//frequency = FrequencySmoothener.getSmoothFrequency(result);
				frequency = result.frequency;
				//Log.d(TAG,"show freq "+String.valueOf(frequency));


				if (frequency>1) {
					freq = (int) frequency;
					//Log.d(TAG, " default freq" + String.valueOf(frequency));
					if (freq >95&& freq<=105)
						ui.setNote(line6,fret1);
					// note G
					else if (freq> 105 && freq<=112 )
						ui.setNote(line6,fret2);
					// note Ab
					else if (freq >=113 && freq <=119)
						ui.setNote(line6,fret3);
					// note A
					else if (freq >=120 && freq <=125)
						ui.setNote(line6,fret4);
					else if (freq >=126 && freq <=132)
						ui.setNote(line6,fret5);
					else if (freq >=133 && freq<=140)
						ui.setNote(line5, fret1);
					else if(freq>140 && freq <=150)
						ui.setNote(line5, fret2);
					else if(freq>150 && freq <=160)
						ui.setNote(line5, fret3);
					else if(freq>160 && freq <=170)
						ui.setNote(line5, fret4);
					else if(freq>170 && freq <=180)
						ui.setNote(line5, fret5);
					else if(freq>180 && freq <=190)
						ui.setNote(line4, fret1);
					else if(freq>190 && freq <=202)
						ui.setNote(line4, fret2);
					else if(freq>202 && freq <=212)
						ui.setNote(line4, fret3);
					else if(freq>212 && freq <=225)
						ui.setNote(line4, fret4);
					else if(freq>225 && freq <=240)
						ui.setNote(line4, fret5);
					else if(freq>240 && freq <= 250)
						ui.setNote(line3, fret1);
					else if(freq>250 && freq <= 270)
						ui.setNote(line3, fret2);
					else if(freq>270 && freq <= 285)
						ui.setNote(line3,fret3);
					else if(freq>285 && freq <= 300)
						ui.setNote(line3,fret4);
					else if(freq>300 && freq <= 315)
						ui.setNote(line3,fret5);
					else if(freq>315 && freq <= 335)
						ui.setNote(line2,fret2);
					else if(freq>335 && freq <= 355)
						ui.setNote(line2,fret3);
					else if(freq>355 && freq <= 375)
						ui.setNote(line2,fret4);
					else if(freq>375 && freq <= 405)
						ui.setNote(line2,fret5);
					else if(freq>405 && freq <= 425)
						ui.setNote(line2,fret6);
					else if(freq>425 && freq <= 450)
						ui.setNote(line1,fret2);
					else if(freq>450 && freq <= 480)
						ui.setNote(line1,fret3);
					else if(freq>480 && freq <= 510)
						ui.setNote(line1,fret4);
					else if(freq>510 && freq <= 535)
						ui.setNote(line1,fret5);
					else if(freq>535 && freq <= 570)
						ui.setNote(line1,fret6);
				}

				//if(frequency>1) Log.d(TAG, "co freq lon hon 1 ");
				if(result.error==AnalyzedSound.ReadingType.BIG_FREQUENCY ||
						result.error==AnalyzedSound.ReadingType.BIG_VARIANCE ||
						result.error==AnalyzedSound.ReadingType.ZERO_SAMPLES)
					proposedMessage = MessageClass.TOO_NOISY;
				else if(result.error==AnalyzedSound.ReadingType.TOO_QUIET)
					proposedMessage = MessageClass.TOO_QUIET;
				else if(result.error==AnalyzedSound.ReadingType.NO_PROBLEMS)
					proposedMessage = MessageClass.TUNING_IN_PROGRESS;
				else {
					Log.e(TAG, "UiController: Unknown class of message.");
					proposedMessage=null;
				}
				if(ConfigFlags.uiControlerInformsWhatItKnowsAboutSound)
					result.getDebug();
				//Log.e(TAG,"Frequency: " + frequency);
				try {
					updateUi();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if(obj instanceof ArrayToDump) {
				ArrayToDump a = (ArrayToDump)obj;
				ui.dumpArray(a.arr, a.elements);
			}
		}
	}



	private void updateUi() throws Exception {
//		GuitarString current = tuning.getString(frequency);
//		// Mark a string in red on a big picture of guitar.
//		ui.changeString(current.stringId);
//
//		// Change color of your guitar.
//
//		double match = 0.0; // How close is current frequency to the desired
//		                    // frequency in 0..1 scale.
//		if(current.stringId == 0) {
//			match = 0.0;
//		} else {
//			if(frequency<current.freq) {
//				match = (frequency-current.minFreq)/(current.freq-current.minFreq);
//			} else {
//				match = (current.maxFreq - frequency )/(current.maxFreq-current.freq);
//			}
//		}
//		try {
//			ui.coloredGuitarMatch(Math.pow(match, 1.5));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		// Update message.
//		// If cannot decide on a string
//		if(proposedMessage == MessageClass.TUNING_IN_PROGRESS && current.stringId == 0)
//			proposedMessage = MessageClass.WEIRD_FREQUENCY;
//		if(message == null) {
//			message = previouslyProposedMessage = proposedMessage;
//		} if(message == proposedMessage) {
//			// do nothing.
//		} else {
//			if(previouslyProposedMessage != proposedMessage) {
//				previouslyProposedMessage = proposedMessage;
//				numberOfVotes = 1;
//			} else if(previouslyProposedMessage == proposedMessage) {
//				numberOfVotes++;
//			}
//			if(numberOfVotes >= minNumberOfVotes) {
//				message = proposedMessage;
//			}
//		}
////		if(frequency>1) Log.d(TAG, "co freq lon hon 1 ");
//
//
//		switch(message) {
////			case TUNING_IN_PROGRESS:
////				ui.displayMessage("Currently tuning string " + current.name +
////						" from " + tuning.getName() + " tuning, matched in " +
////						Math.round(100.0 * match) + "%.", true);
////				Log.d(TAG, "case 1 " + String.valueOf(frequency));
////
////				break;
////			case TOO_NOISY:
////				ui.displayMessage("Please reduce background noise (or play louder).", false);
////				Log.d(TAG,"case 2 " +  String.valueOf(frequency));
////				break;
////			case TOO_QUIET:
////				ui.displayMessage("Please play louder!", false);
////				Log.d(TAG,"case 3 " +  String.valueOf(frequency));
////				break;
////			case WEIRD_FREQUENCY:
////				Log.d(TAG,"case 4 " + String.valueOf(frequency));
////				ui.displayMessage("Are you sure instrument you are playing is guitar? :)", false);
////			default:
//		//		Log.d(TAG, "No message");
		//}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View thing, int itemno,
			long rowno) {
		if(tuning.getTuningId() != itemno)
			tuning = new Tuning(itemno);
		Log.d(TAG,"Changed tuning to " + tuning.getName());
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// do nothing
	}



}
