package scottmd3.tictactoe;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AndroidTicTacToe extends Activity {

	private static final String TAG = "AndroidTicTacToe";

	private static final int DIALOG_DIFFICULTY_ID = 0;
	private static final int DIALOG_QUIT_ID = 1;
	private static final int DIALOG_ABOUT_ID = 2;
	
	private BoardView mBoardView;

	// Whose turn is it?
	private char mTurn;
	
	// For alternating the player who goes first
	private char mFirst;
	
	// allows game pauses
	private Handler mPauseHandler;
	private Runnable myRunnable;
	
	// Keep track of wins
	private int mHumanWins = 0;
	private int mComputerWins = 0;
	private int mTies = 0;

	// game logic
	private TicTacToeGame mGame;

	// Various text displayed
	private TextView mInfoTextView;
	private TextView mHumanScoreTextView;
	private TextView mComputerScoreTextView;
	private TextView mTieScoreTextView;

	private boolean mGameOver;
	
	// for all the sounds we play
	private SoundPool mSounds;
	private int mHumanMoveSoundID;
	private int mComputerMoveSoundID;
	// extra sounds for the extra challenge
	private int mHumanWinSoundID;
	private int mComputerWinSoundID;
	private int mTieSoundID;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		mGame = new TicTacToeGame();
		mBoardView = (BoardView) findViewById(R.id.board);
		mBoardView.setGame(mGame);
		
		// Listen for touches on the board
		mBoardView.setOnTouchListener(mTouchListener);

		// get the TextViews
		mInfoTextView = (TextView) findViewById(R.id.information);
		mHumanScoreTextView = (TextView) findViewById(R.id.player_score);
		mComputerScoreTextView = (TextView) findViewById(R.id.computer_score);
		mTieScoreTextView = (TextView) findViewById(R.id.tie_score);
		
		mTurn = TicTacToeGame.HUMAN_PLAYER;
		mFirst = TicTacToeGame.COMPUTER_PLAYER; // computer starts the next game
		mPauseHandler = new Handler();
		
		startNewGame(true);
	}
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		mSounds = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
		// 2 = maximum sounds to play at the same time, 
		// AudioManager.STREAM_MUSIC is the stream type typically used for games
		// 0 is the "the sample-rate converter quality. Currently has no effect. Use 0 for the default."
		mHumanMoveSoundID = mSounds.load(this, R.raw.human_move, 1); 
		// Context, id of resource, priority (currently no effect)
		mComputerMoveSoundID = mSounds.load(this, R.raw.computer_move, 1); 
		mHumanWinSoundID = mSounds.load(this, R.raw.human_win, 1);
		mComputerWinSoundID = mSounds.load(this, R.raw.computer_win, 1);
		mTieSoundID = mSounds.load(this, R.raw.tie, 1);
	}
	
	@Override
	protected void onPause() 
	{
		super.onPause();
		
		Log.d(TAG, "in onPause");
		
		if(mSounds != null) 
		{
			mSounds.release();
			mSounds = null;
		}
	}

	// Set up the game board. 
	private void startNewGame(boolean first) 
	{
		// alternates the first player
		if(mGameOver) 
		{
			mTurn = mFirst;
			mFirst = (mFirst == TicTacToeGame.COMPUTER_PLAYER) ? 
					TicTacToeGame.HUMAN_PLAYER : TicTacToeGame.COMPUTER_PLAYER;
		}
		
		// player quits on their turn, computer goes next
		else if(mTurn == TicTacToeGame.HUMAN_PLAYER && !first) 
		{
			mTurn = TicTacToeGame.COMPUTER_PLAYER;
			mFirst = TicTacToeGame.HUMAN_PLAYER;
		}
		mGameOver = false;

		mGame.clearBoard();  
		mBoardView.invalidate(); // Leads to a redraw of the board view 

		// Who begins?
		if (mTurn == TicTacToeGame.COMPUTER_PLAYER) 
		{
			Log.d(TAG, "Computers turn!!!");
			mInfoTextView.setText(R.string.first_computer);
			int move = mGame.getComputerMove();
			setMove(TicTacToeGame.COMPUTER_PLAYER, move);
		}
		else 
		{
			mInfoTextView.setText(R.string.first_human); 
		}
	}

	private boolean setMove(char player, int location) 
	{ 
		if (player == TicTacToeGame.COMPUTER_PLAYER) {    		
			// extra challenge - delay the computer for 1 second
			myRunnable = createRunnable(location);
			mPauseHandler.postDelayed(myRunnable, 1000); 
			return true;
		}
		else if (mGame.setMove(player, location)) 
		{ 
			mBoardView.invalidate(); // Redraw the board
			mSounds.play(mHumanMoveSoundID, 1, 1, 1, 0, 1);
			return true;
		}
		return false;
	}
	
	private Runnable createRunnable(final int location) 
	{
		return new Runnable() 
		{
			public void run() 
			{ 

				mGame.setMove(TicTacToeGame.COMPUTER_PLAYER, location);
				// soundID, leftVolume, rightVolume, priority, loop, rate
				mSounds.play(mComputerMoveSoundID, 1, 1, 1, 0, 1);	
				
				mBoardView.invalidate();   // Redraw the board

				int winner = mGame.checkForWinner();
				if (winner == 0) 
				{
					mTurn = TicTacToeGame.HUMAN_PLAYER;	                                	
					mInfoTextView.setText(R.string.turn_human);
				}
				else 
					endGame(winner);
			}
		};		
	}
	
	// for ending a game
	private void endGame(int winner) 
	{
		if (winner == 1) 
		{
			mTies++;
			mTieScoreTextView.setText(Integer.toString(mTies));
			mInfoTextView.setText(R.string.result_tie);
			mSounds.play(mTieSoundID, 1, 1, 1, 0, 1);
		}
		else if (winner == 2) 
		{
			mHumanWins++;
			mHumanScoreTextView.setText(Integer.toString(mHumanWins));
			mInfoTextView.setText(R.string.result_human_wins);
			mSounds.play(mHumanWinSoundID, 1, 1, 1, 0, 1);
		}
		else 
		{
			mComputerWins++;
			mComputerScoreTextView.setText(Integer.toString(mComputerWins));
			mInfoTextView.setText(R.string.result_computer_wins);
			mSounds.play(mComputerWinSoundID, 1, 1, 1, 0, 1);
		}
		mGameOver = true;
	}

	@Override 
	public boolean onCreateOptionsMenu(Menu menu) 
	{ 
		super.onCreateOptionsMenu(menu); 

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return true;

	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId()) 
		{
			case R.id.new_game:
				if (myRunnable != null);
					mPauseHandler.removeCallbacks(myRunnable);
				startNewGame(false);
				return true;
				
			case R.id.ai_difficulty: 
				showDialog(DIALOG_DIFFICULTY_ID);   	
				return true;
				
			case R.id.quit:
				showDialog(DIALOG_QUIT_ID);
				return true;
				
			case R.id.about:
				showDialog(DIALOG_ABOUT_ID);
				return true;
		}
		return false;
	}   

	protected Dialog onCreateDialog(int id) 
	{
		
		Log.d(TAG, "In onCreateDialog");
		
		Dialog dialog = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		switch(id) 
		{
			case DIALOG_DIFFICULTY_ID:

			builder.setTitle(R.string.difficulty_choose);

			final CharSequence[] levels = 
			{
					getResources().getString(R.string.difficulty_easy),
					getResources().getString(R.string.difficulty_harder), 
					getResources().getString(R.string.difficulty_expert)
			};

			final int selected = mGame.getDifficultyLevel().ordinal();
			Log.d(TAG, "selected difficulty value: " + selected + ", level: " + mGame.getDifficultyLevel());

			builder.setSingleChoiceItems(levels, selected, 
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) 
				{
					dialog.dismiss();   // Close dialog

					mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.values()[item]);
					Log.d(TAG, "Difficulty level: " + mGame.getDifficultyLevel());

					// Display the selected difficulty level
					Toast.makeText(getApplicationContext(), levels[item], 
							Toast.LENGTH_SHORT).show();        	    
				}
			});
			dialog = builder.create();
			break;    // this case
			
		case DIALOG_QUIT_ID:
			// Create the quit confirmation dialog

			builder.setMessage(R.string.quit_question).setCancelable(false)
			.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int id) 
				{
					AndroidTicTacToe.this.finish();
				}
			}).setNegativeButton(R.string.no, null);   
			dialog = builder.create();
			break;
			
		case DIALOG_ABOUT_ID:
			Log.d(TAG, "Create about dialog");
			Context context = getApplicationContext();
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.about_dialog, null); 		
			builder.setView(layout);
			builder.setPositiveButton("OK", null);	
			dialog = builder.create();
			break;
		}

		if(dialog == null)
			Log.d(TAG, "Dialog has a null value");
		else
			Log.d(TAG, "Dialog created: " + id + ", dialog: " + dialog);
		return dialog;          
	}
	
	// For Listening in on touch screen presses
	private OnTouchListener mTouchListener = new OnTouchListener() 
	{
		public boolean onTouch(View v, MotionEvent event) 
		{

			// Determine which cell was touched	    	
			int col = (int) event.getX() / mBoardView.getBoardCellWidth();
			int row = (int) event.getY() / mBoardView.getBoardCellHeight();
			int pos = row * 3 + col;

			if (!mGameOver && mTurn == TicTacToeGame.HUMAN_PLAYER && setMove(TicTacToeGame.HUMAN_PLAYER, pos))	
			{        		

				// If no winner yet, let the computer make a move
				int winner = mGame.checkForWinner();
				if (winner == 0) 
				{ 
					mInfoTextView.setText(R.string.turn_computer); 
					int move = mGame.getComputerMove();
					setMove(TicTacToeGame.COMPUTER_PLAYER, move);            		
				} 
				else
					endGame(winner);           	

			}

			// So we aren't notified of continued events when finger is moved
			return false;
		}
	};
}
