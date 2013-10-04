package scottmd3.tictactoe;


/* TicTacToeConsole.java
 * By Frank McCown (Harding University)
 * Modified by Mike Scott
 * 
 * Logic for a tic tac toe game.
 */

import java.util.Arrays;
import java.util.Random;

import android.util.Log;

public class TicTacToeGame {

	private static final String TAG = "TicTacToeGame";

	// The computer's difficulty levels 
	public enum DifficultyLevel {Easy, Harder, Expert};

	// Current difficulty level
	private DifficultyLevel mDifficultyLevel = DifficultyLevel.Expert;   


	private final char mBoard[];
	public static final int BOARD_SIZE = 9;

	public static final char HUMAN_PLAYER = 'X';
	public static final char COMPUTER_PLAYER = 'O';
	public static final char OPEN_SPOT = ' ';

	private Random mRand; 

	public TicTacToeGame() {
		// Seed the random number generator
		mRand = new Random(); 

		// fill the board
		mBoard = new char[BOARD_SIZE];
		clearBoard();
	}
	
	/**
	 * Copies the board state
	 * @return copy of current board
	 */
	public char[] getBoardState() 
	{
		return mBoard.clone();
	}
	
	/**d 
	 * Set the board to the new state. board.length must equal 9. 
	 * All elements must be HUMAN_PLAYER, COMPUTER_PLAYERS, or
	 * OPEN_SPOT
	 */
	public void setBoardState(char[] board) 
	{
		if(board == null || board.length != 9 || badChar(board))
			throw new IllegalArgumentException("bad board: " + Arrays.toString(board));
		for(int i = 0; i < board.length; i++)
			mBoard[i] = board[i];
	}
	
	private boolean badChar(char[] board) {
		for(char ch : board)
			if(!(ch == HUMAN_PLAYER
						|| ch == COMPUTER_PLAYER
						|| ch == OPEN_SPOT))
				return true;
		return false;
	}

	/** Clear the board of all X's and O's by setting all spots to OPEN_SPOT. */
	public void clearBoard() {
		for(int i = 0; i < mBoard.length; i++)
			mBoard[i] = OPEN_SPOT;
	}

	/** Set the given player at the given location on the game board.
	 *  The location must be available, or the board will not be changed.
	 * 
	 * @param player - The HUMAN_PLAYER or COMPUTER_PLAYER
	 * @param location - The location (0-8) to place the move
	 */
	public boolean setMove(char player, int location) 
	{
		if(location < 0 || location >= BOARD_SIZE)
			throw new IllegalArgumentException("location must be between 0 and 8 inclusive: " + location);
		if(!(player == HUMAN_PLAYER || player == COMPUTER_PLAYER))
			throw new IllegalArgumentException("player must be "  + HUMAN_PLAYER + " or " 
					+ COMPUTER_PLAYER + ". " + player);
		
		if(mBoard[location] == OPEN_SPOT) 
		{
			mBoard[location] = player;
			return true;
		}
		return false;
	}
	
	public DifficultyLevel getDifficultyLevel() {
		return mDifficultyLevel;
	}
		
	public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
		mDifficultyLevel = difficultyLevel;
	}
	
	public int getBoardOccupant(int cellNum) 
	{
		return mBoard[cellNum];
	}


	// Check for a winner.  Return
	//  0 if no winner or tie yet
	//  1 if it's a tie
	//  2 if X won
	//  3 if O won
	public int checkForWinner() {

		// Check horizontal wins
		for (int i = 0; i <= 6; i += 3)	{
			if (mBoard[i] == HUMAN_PLAYER && 
					mBoard[i+1] == HUMAN_PLAYER &&
					mBoard[i+2]== HUMAN_PLAYER)
				return 2;
			if (mBoard[i] == COMPUTER_PLAYER && 
					mBoard[i+1]== COMPUTER_PLAYER && 
					mBoard[i+2] == COMPUTER_PLAYER)
				return 3;
		}

		// Check vertical wins
		for (int i = 0; i <= 2; i++) {
			if (mBoard[i] == HUMAN_PLAYER && 
					mBoard[i+3] == HUMAN_PLAYER && 
					mBoard[i+6]== HUMAN_PLAYER)
				return 2;
			if (mBoard[i] == COMPUTER_PLAYER && 
					mBoard[i+3] == COMPUTER_PLAYER && 
					mBoard[i+6]== COMPUTER_PLAYER)
				return 3;
		}

		// Check for diagonal wins
		if ((mBoard[0] == HUMAN_PLAYER &&
				mBoard[4] == HUMAN_PLAYER && 
				mBoard[8] == HUMAN_PLAYER) ||
				(mBoard[2] == HUMAN_PLAYER && 
				mBoard[4] == HUMAN_PLAYER &&
				mBoard[6] == HUMAN_PLAYER))
			return 2;
		if ((mBoard[0] == COMPUTER_PLAYER &&
				mBoard[4] == COMPUTER_PLAYER && 
				mBoard[8] == COMPUTER_PLAYER) ||
				(mBoard[2] == COMPUTER_PLAYER && 
				mBoard[4] == COMPUTER_PLAYER &&
				mBoard[6] == COMPUTER_PLAYER))
			return 3;

		// Check for tie
		for (int i = 0; i < BOARD_SIZE; i++) {
			// If we find a number, then no one has won yet
			if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER)
				return 0;
		}

		// If we make it through the previous loop, all places are taken, so it's a tie
		return 1;
	}



	public int getComputerMove() {

		int move = -1;

		if (mDifficultyLevel == DifficultyLevel.Easy) 
			move = getRandomMove();    	
		else if (mDifficultyLevel == DifficultyLevel.Harder) {
			move = getWinningMove();
			if (move == -1)
				move = getRandomMove();
		}
		else if (mDifficultyLevel == DifficultyLevel.Expert) {
			// Try to win, but if that's not possible, block.
			// If that's not possible, move anywhere.
			move = getWinningMove();
			if (move == -1)
				move = getBlockingMove();
			if (move == -1)
				move = getRandomMove();
		}

		return move;
	}

	public int getRandomMove() 
	{
		int move;
		do
		{
			move = mRand.nextInt(BOARD_SIZE);
		}
		while (mBoard[move] != OPEN_SPOT);
		
		Log.d(TAG, "Computer moving to " + move + " as a random move.");
		
		return move;
	}
	
	public int getWinningMove()
	{
		int move = -1;
		for (int i = 0; i < BOARD_SIZE; i++) 
		{
			if (mBoard[i] == OPEN_SPOT) 
			{
				mBoard[i] = COMPUTER_PLAYER;
				if (checkForWinner() == 3) 
				{
					mBoard[i] = OPEN_SPOT;
					Log.d(TAG, "Computer moving to " + i + " to win.");
					return i;
				}
				else
					mBoard[i] = OPEN_SPOT;
			}
		}
		Log.d(TAG, "computer couldn't find a blocking  move");
		return move;
	}
	
	public int getBlockingMove()
	{
		int move = -1;
		for (int i = 0; i < BOARD_SIZE; i++) 
		{
			if (mBoard[i] == OPEN_SPOT) 
			{
				mBoard[i] = HUMAN_PLAYER;
				if (checkForWinner() == 2) 
				{
					//mBoard[i] = COMPUTER_PLAYER;
					mBoard[i] = OPEN_SPOT;
					Log.d(TAG, "Computer moving to " + i + " to block win.");
					return i;
				}
				else
					mBoard[i] = OPEN_SPOT;
			}
		}
		Log.d(TAG, "computer couldn't find a blocking  move");
		return move;
	}
}

