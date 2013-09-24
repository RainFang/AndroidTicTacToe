package scottmd3.tictactoe;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AndroidTicTacToe extends Activity {

	private static final String TAG = "AndroidTicTacToe";

	private static final int DIALOG_DIFFICULTY_ID = 0;
	private static final int DIALOG_QUIT_ID = 1;
	private static final int DIALOG_ABOUT_ID = 2;

	private static final int[] BUTTON_IDS = {R.id.one, R.id.two, R.id.three, R.id.four,
		R.id.five, R.id.six, R.id.seven, R.id.eight, R.id.nine
	};

	// Whose turn to go first
	private char mTurn = TicTacToeGame.COMPUTER_PLAYER;    

	// Keep track of wins
	private int mHumanWins = 0;
	private int mComputerWins = 0;
	private int mTies = 0;

	// game logic
	private TicTacToeGame mGame;

	// Buttons making up the board
	private Button mBoardButtons[];

	// Various text displayed
	private TextView mInfoTextView;
	private TextView mHumanScoreTextView;
	private TextView mComputerScoreTextView;
	private TextView mTieScoreTextView;

	private boolean mGameOver; 


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.CustomTheme);

//		// hide the title if it exists
//		int currentAPIVersion = android.os.Build.VERSION.SDK_INT;
//		if(currentAPIVersion >= android.os.Build.VERSION_CODES.HONEYCOMB)
//			this.getActionBar().setDisplayShowTitleEnabled(false);

		setContentView(R.layout.main);
		

  

		mBoardButtons = new Button[TicTacToeGame.BOARD_SIZE];
		for(int i = 0; i < mBoardButtons.length; i++)
			mBoardButtons[i] = (Button) findViewById(BUTTON_IDS[i]);

		// get the TextViews
		mInfoTextView = (TextView) findViewById(R.id.information);
		mHumanScoreTextView = (TextView) findViewById(R.id.player_score);
		mComputerScoreTextView = (TextView) findViewById(R.id.computer_score);
		mTieScoreTextView = (TextView) findViewById(R.id.tie_score);
		mGame = new TicTacToeGame();	

		startNewGame();
	}

	// Set up the game baord. 
	private void startNewGame() {
		mGameOver = false;

		mGame.clearBoard();  

		// Reset all buttons
		for (int i = 0; i < mBoardButtons.length; i++) {
			mBoardButtons[i].setText("");
			mBoardButtons[i].setEnabled(true);    
			mBoardButtons[i].setOnClickListener(new ButtonClickListener(i));		   
		}

		// Alternate who goes first
		if (mTurn == TicTacToeGame.HUMAN_PLAYER) {
			mTurn = TicTacToeGame.COMPUTER_PLAYER;
			mInfoTextView.setText(R.string.first_computer);
			int move = mGame.getComputerMove();
			setMove(TicTacToeGame.COMPUTER_PLAYER, move);
			mInfoTextView.setText(R.string.turn_human);
		}
		else {
			mTurn = TicTacToeGame.HUMAN_PLAYER;
			mInfoTextView.setText(R.string.first_human); 
		}	
	}

	private void setMove(char player, int location) {
		mGame.setMove(player, location);
		mBoardButtons[location].setEnabled(false); 
		mBoardButtons[location].setText(String.valueOf(player));
		if (player == TicTacToeGame.HUMAN_PLAYER) 
			mBoardButtons[location].setTextColor(Color.rgb(0, 200, 0));     	    
		else 
			mBoardButtons[location].setTextColor(Color.rgb(200, 0, 0)); 
	}

	// when game is over, disable all buttons and set flag
	private void gameOver() {
		mGameOver = true;
		for(int i = 0; i < mBoardButtons.length; i++)
			mBoardButtons[i].setEnabled(false);
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
				startNewGame();
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






	// Handles clicks on the game baord buttons
	private class ButtonClickListener implements View.OnClickListener { 
		int location; 

		public ButtonClickListener(int location) { 
			this.location = location; 
		} 

		public void onClick(View view) { 
			if (!mGameOver && mBoardButtons[location].isEnabled()) {
				setMove(TicTacToeGame.HUMAN_PLAYER, location);        		

				// If no winner yet, let the <Mike's version> computer make a move
				int winner = mGame.checkForWinner();
				if (winner == 0) { 
					mInfoTextView.setText(R.string.turn_computer);
					int move = mGame.getComputerMove();
					setMove(TicTacToeGame.COMPUTER_PLAYER, move);
					winner = mGame.checkForWinner();
				} 

				if (winner == 0)
					mInfoTextView.setText(R.string.turn_human);
				else {
					if (winner == 1)  {
						mInfoTextView.setText(R.string.result_tie);
						mTies++;
						mTieScoreTextView.setText(Integer.toString(mTies));
					}
					else if (winner == 2) {
						mHumanWins++;
						mHumanScoreTextView.setText(Integer.toString(mHumanWins));
						mInfoTextView.setText(R.string.result_human_wins);
					}
					else {
						mComputerWins++;
						mComputerScoreTextView.setText(Integer.toString(mComputerWins));
						mInfoTextView.setText(R.string.result_computer_wins);
					}
					gameOver();
				}
			}    
		}
	}
}
