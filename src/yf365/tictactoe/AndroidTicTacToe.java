package yf365.tictactoe;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AndroidTicTacToe extends Activity {
	
	// Handles clicks on the game board buttons
	private class ButtonClickListener implements View.OnClickListener 
	{ 
		int location; 
		
		public ButtonClickListener(int location) 
		{ 
			this.location = location; 
		} 
		
		public void onClick(View view) 
		{ 
			if (mBoardButtons[location].isEnabled()) 
			{
				setMove(TicTacToeGame.HUMAN_PLAYER, location); 
				// If no winner yet, let the computer make a move
				int winner = mGame.checkForWinner();
				if (winner == 0) 
				{ 
					mInfoTextView.setText(R.string.turn_computer);
					int move = mGame.getComputerMove();
					setMove(TicTacToeGame.COMPUTER_PLAYER, move);
					winner = mGame.checkForWinner();
				} 
				
				if (winner == 0)
					mInfoTextView.setText(R.string.turn_human);
				else if (winner == 1) 
				{
					mInfoTextView.setText(R.string.result_tie);
					ties++;
				}
				else if (winner == 2) 
				{
					mInfoTextView.setText(R.string.result_human_wins);
					humanWins++;
				}
				else
				{
					mInfoTextView.setText(R.string.result_computer_wins);
					computerWins++;
				}
			} 
		}
		
		private void setMove(char player, int location) 
		{
			mGame.setMove(player, location);
			mBoardButtons[location].setEnabled(false); 
			mBoardButtons[location].setText(String.valueOf(player));
			if (player == TicTacToeGame.HUMAN_PLAYER) 
				mBoardButtons[location].setTextColor(Color.rgb(0, 200, 0)); 
			else
				mBoardButtons[location].setTextColor(Color.rgb(200, 0, 0)); 
		}
	}
	
	private class NGButtonClickListener implements View.OnClickListener 
	{ 
		
		public void onClick(View view) 
		{ 
			if (ngButton.isEnabled()) 
			{
				startNewGame();
			} 
		}
	}
	
	// Represents the internal state of the game
	private TicTacToeGame mGame;
	
	// Buttons making up the board
	private Button mBoardButtons[];
	// New Game button
	private Button ngButton;
	// Various text displayed
	private TextView mInfoTextView;
	//for alternating the first player
	private boolean humanFirst;
	
	private int humanWins;
	private int computerWins;
	private int ties;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		mBoardButtons = new Button[TicTacToeGame.BOARD_SIZE];
		mBoardButtons[0] = (Button) findViewById(R.id.one);
		mBoardButtons[1] = (Button) findViewById(R.id.two); 
		mBoardButtons[2] = (Button) findViewById(R.id.three); 
		mBoardButtons[3] = (Button) findViewById(R.id.four); 
		mBoardButtons[4] = (Button) findViewById(R.id.five); 
		mBoardButtons[5] = (Button) findViewById(R.id.six); 
		mBoardButtons[6] = (Button) findViewById(R.id.seven); 
		mBoardButtons[7] = (Button) findViewById(R.id.eight); 
		mBoardButtons[8] = (Button) findViewById(R.id.nine); 
		mInfoTextView = (TextView) findViewById(R.id.information); 
		mGame = new TicTacToeGame();
		humanFirst = false;
		
		ngButton = (Button) findViewById(R.id.newgame);
		ngButton.setEnabled(true);
		ngButton.setOnClickListener(new NGButtonClickListener());
		
		humanWins = 0;
		computerWins = 0;
		ties = 0;
		
		startNewGame();
	}
	
	// Set up the game board. 
	private void startNewGame() 
	{ 
		mGame.clearBoard();
		
		// Reset all buttons
		for (int i = 0; i < mBoardButtons.length; i++) 
		{
			mBoardButtons[i].setText("");
			mBoardButtons[i].setEnabled(true); 
			mBoardButtons[i].setOnClickListener(new ButtonClickListener(i));
		}
		
		//Decides who goes first
		//if (!humanFirst)
		//{
			mInfoTextView.setText(R.string.first_human);
			humanFirst = true;
		/*}
		else
		{
			mInfoTextView.setText(R.string.turn_computer);
			humanFirst = false;
				
			int move = mGame.getComputerMove();
			mGame.setMove(TicTacToeGame.COMPUTER_PLAYER, move);
		}*/
		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		startNewGame();
		return true;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		super.onCreateOptionsMenu(menu); 
		menu.add("New Game"); 
		return true;
	}

}
