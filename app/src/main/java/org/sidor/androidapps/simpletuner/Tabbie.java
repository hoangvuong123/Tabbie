package org.sidor.androidapps.simpletuner;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout;

import org.w3c.dom.Text;

public class Tabbie extends Activity {
	// switch off gc logs.
	// System.setProperty("log.tag.falvikvm", "SUPPRESS"); 
	public static final String TAG = "RealGuitarTuner";
	
	private final boolean LAUNCHANALYZER = true;

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
    private ImageView guitar = null;
	private ImageView tune = null;
	private ImageView dot = null;
	private Spinner tuningSelector = null;
	private SoundAnalyzer soundAnalyzer = null ;
	private UiController uiController = null;
	private TextView mainMessage = null;
	private TextView txt = null;
	private TextView txtSongname = null;
    private static RelativeLayout.LayoutParams params;

	Button btnbrowse;
	Button btnPlay;
	String path;
	FileChooser newFile;
	MediaPlayer mediaPlayer = new MediaPlayer();
	Boolean playstt = false;
	private double startTime = 0;
	private double finalTime = 0;
	public static int oneTimeOnly = 0;
	SeekBar seekbar;
	TextView txt1;
	TextView txt2;
	TextView txt3;
	private Handler myHandler = new Handler();
	Boolean toggle = false;
	Boolean newSong = false;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.main);
        uiController = new UiController(this);
        if(LAUNCHANALYZER) {
	        try {
	        	soundAnalyzer = new SoundAnalyzer();
	        } catch(Exception e) {
	        	Toast.makeText(this, "The are problems with your microphone :(", Toast.LENGTH_LONG ).show();
	        	Log.e(TAG, "Exception when instantiating SoundAnalyzer: " + e.getMessage());
	        }
	        soundAnalyzer.addObserver(uiController);
        }
        guitar = (ImageView)findViewById(R.id.guitar);
        tune = (ImageView)findViewById(R.id.tune);
        mainMessage = (TextView)findViewById(R.id.mainMessage);
        tuningSelector = (Spinner)findViewById(R.id.tuningSelector);
        Tuning.populateSpinner(this, tuningSelector);
        tuningSelector.setOnItemSelectedListener(uiController);
		//txt = (TextView)findViewById(R.id.txt);
		dot = (ImageView)findViewById(R.id.dot);
		params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

		btnbrowse = (Button)findViewById(R.id.btnBro);
		btnPlay = (Button)findViewById(R.id.btnPlay);
		txtSongname = (TextView)findViewById(R.id.txtSongname);
		seekbar = (SeekBar)findViewById(R.id.seekbar);
		txt1 = (TextView)findViewById(R.id.txt1);
		txt2 = (TextView)findViewById(R.id.txt2);
		txt3 = (TextView)findViewById(R.id.txtpercent);

		seekbar.setClickable(true);
		seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (fromUser)
					mediaPlayer.seekTo(seekBar.getProgress());
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});

	}

	public void onBrowse(View view){
		newFile = new FileChooser(this);
		newFile.setFileListener(new FileChooser.FileSelectedListener() {
			@Override
			public void fileSelected(final File file) {
				path = file.getPath();
				txtSongname.setText(file.getName());
			}
		}).showDialog();
		newSong = true;
	}

	public void onPlay(View view) throws IOException {
		if (path == null) {
			Toast.makeText(this, "You must chose a media file!", Toast.LENGTH_SHORT).show();
		}
		else if (!toggle) {
			if (!playstt || newSong) {
				Uri myUri = Uri.parse(path); // initialize Uri here
				mediaPlayer.reset();
				mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				mediaPlayer.setDataSource(getApplicationContext(), myUri);
				mediaPlayer.prepare();
				mediaPlayer.start();

				playstt = true;

				finalTime = mediaPlayer.getDuration();
				startTime = mediaPlayer.getCurrentPosition();

				if (oneTimeOnly == 0) {
					seekbar.setMax((int) finalTime);
					oneTimeOnly = 1;
				}

				txt2.setText(String.format("%d min, %d sec",
								TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
								TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
										TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime)))
				);

				txt1.setText(String.format("%d min, %d sec",
								TimeUnit.MILLISECONDS.toMinutes((long) startTime),
								TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
										TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime)))
				);

				seekbar.setProgress((int) startTime);
				myHandler.postDelayed(UpdateSongTime, 100);
			} else {
				mediaPlayer.start();
			}
			toggle = true;
			btnPlay.setText("Stop");
		}
		else
		{
			btnPlay.setText("Play");
			mediaPlayer.pause();
			toggle = false;
		}
		newSong = false;
	}

	private Runnable UpdateSongTime = new Runnable() {
		public void run() {
			startTime = mediaPlayer.getCurrentPosition();
			txt1.setText(String.format("%d min, %d sec",
							TimeUnit.MILLISECONDS.toMinutes((long) startTime),
							TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
									TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
											toMinutes((long) startTime)))
			);
			txt2.setText(String.format("%d min, %d sec",
							(TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
									TimeUnit.MILLISECONDS.toSeconds((long) startTime)) / 60,
							(TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
									TimeUnit.MILLISECONDS.toSeconds((long) startTime)) % 60)
			);
			txt3.setText(Integer.toString((int) (seekbar.getProgress() * 100 / finalTime)) + " %");
			myHandler.postDelayed(this, 100);

			seekbar.setProgress((int) startTime);

		}
	};
	
	private Map<Integer, Bitmap> preloadedImages;
	private BitmapFactory.Options bitmapOptions;
	
	private Bitmap getAndCacheBitmap(int id) {
		if(preloadedImages == null)
			preloadedImages = new HashMap<Integer,Bitmap>();
		Bitmap ret = preloadedImages.get(id);
		if(ret == null) {
			if(bitmapOptions == null) {
				BitmapFactory.Options options=new BitmapFactory.Options();
				options.inSampleSize = 4; // The higher it goes, the smaller the image.
			}
			ret = BitmapFactory.decodeResource(getResources(), id, bitmapOptions);
			preloadedImages.put(id, ret);
		}
		return ret;
	}
	
	public void dumpArray(final double [] inputArray, final int elements) {
		Log.d(TAG, "Starting File writer thread...");
		final double [] array = new double[elements];
		for(int i=0; i<elements; ++i)
			array[i] = inputArray[i];
		new Thread(new Runnable() {
			@Override
			public void run() {
				try { // catches IOException below
					// Location: /data/data/your_project_package_structure/files/samplefile.txt         
					String name = "Chart_" + (int)(Math.random()*1000) + ".data";
					FileOutputStream fOut = openFileOutput(name,
							Context.MODE_WORLD_READABLE);
					OutputStreamWriter osw = new OutputStreamWriter(fOut); 

					// Write the string to the file
					for(int i=0; i<elements; ++i) 
							osw.write("" + array[i] + "\n");
					/* ensure that everything is
					 * really written out and close */
					osw.flush();
					osw.close();
					Log.d(TAG, "Successfully dumped array in file " + name);
				} catch(Exception e) {
					Log.e(TAG,e.getMessage());
				}
			}
		}).start();
	}
	
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if(ConfigFlags.menuKeyCausesAudioDataDump) {
		    if (keyCode == KeyEvent.KEYCODE_MENU) {
		        Log.d(TAG,"Menu button pressed");
		        Log.d(TAG,"Requesting audio data dump");
		        soundAnalyzer.dumpAudioDataRequest();
		        return true;
		    }
		}
	    return false;
	}
	
	private int [] stringNumberToImageId = new int[]{
		//	R.drawable.strings0,
		//	R.drawable.strings1,
		//	R.drawable.strings2,
		//	R.drawable.strings3,
		//	R.drawable.strings4,
		//	R.drawable.strings5,
		//	R.drawable.strings6
	};
	

	
	
    int oldString = 0;
    // 1-6 strings (ascending frequency), 0 - no string.
    public void changeString(int stringId) {
    	if(oldString!=stringId) {
    		guitar.setImageBitmap(getAndCacheBitmap(stringNumberToImageId[stringId]));
        	oldString=stringId;
    	}
    }
    
	int [] targetColor =         new int[]{160,80,40};
	int [] awayFromTargetColor = new int[]{160,160,160};

    
    public void coloredGuitarMatch(double match) {

		tune.setBackgroundColor(
				Color.rgb((int) (match * targetColor[0] + (1 - match) * awayFromTargetColor[0]),
						(int) (match * targetColor[1] + (1 - match) * awayFromTargetColor[1]),
						(int) (match * targetColor[2] + (1 - match) * awayFromTargetColor[2])));
        
    }
    
    public void displayMessage(String msg, boolean positiveFeedback) {
    	int textColor = positiveFeedback ? Color.rgb(34,139,34) : Color.rgb(255,36,0);
    	mainMessage.setText(msg);
    	mainMessage.setTextColor(textColor);
    }
	public void displayfreq(String freq)
	{
		//txt.setText(freq);
	}
    
    
    @Override
	protected void onDestroy() {
		super.onDestroy();
        Log.d(TAG, "onDestroy()");

	}

	@Override
	protected void onPause() {
		super.onPause();
        Log.d(TAG,"onPause()");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
        Log.d(TAG,"onRestart()");

	}

	@Override
	protected void onResume() {
		super.onResume();
        Log.d(TAG,"onResume()");
        if(soundAnalyzer!=null)
        	soundAnalyzer.ensureStarted();
	}

	@Override
	protected void onStart() {
		super.onStart();
        Log.d(TAG,"onStart()");
        if(soundAnalyzer!=null)
        	soundAnalyzer.start();
	}

	@Override
	protected void onStop() {
		super.onStop();
        Log.d(TAG, "onStop()");
        if(soundAnalyzer!=null)
        	soundAnalyzer.stop();
	}

	public void setNote(int line, int fret){

        params.topMargin= line;
        params.leftMargin = fret;

        dot.setLayoutParams(params);
    }


}