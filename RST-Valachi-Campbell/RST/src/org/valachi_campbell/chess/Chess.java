package org.valachi_campbell.chess;

import java.util.Scanner;
import java.lang.AssertionError;

/**
 * A game of Chess. Follows most conventional
 * rules, save for timers and stalemates.
 * @author Gabriel Valachi
 * @author Liam Campbell
 * @category ICS3UR
 * @since June 15, 2016
 */
public class Chess
{
	public static CChessPiece[] g_hPieces;
	public static CChessPiece g_hWhiteKing;
	public static CChessPiece g_hBlackKing;
	
	public static final int ASCII_ZERO = 48;
	public static final int PIECES = 32;
	public static final int BOARD_LENGTH = 8;
	public static final int BOARD_WIDTH = 8;
	public static char[][] g_cChessboard;
	public static char[][] g_cChessPieceMap;
	public static char[][] g_cPieceID;
	
	public static void main( String[] args )
	{
		Scanner in = new Scanner(System.in);
		String[] szThirdInvalidCommandTaunts = {
				"Please stop smudging the keyboard with your dirty, dirty fingers.\n",
				"No, you CANNOT force this command.\n",
				"Is... there something we need to talk about?\n",
				"This is just one big party to you, isn't it?\n",
				"You could always use speech-to-text programs if the keyboard bugs you.\n",
				"Don't you have a mission to accomplish?\n",
				"That doesn't work no matter what. This isn't Fantasia.\n",
		};
		
		//Defines the physical representation of the chess piece map
		g_cPieceID = new char[256][2];
		g_cPieceID['0'][0] = ' '; g_cPieceID['0'][1] = ' ';
		g_cPieceID['1'][0] = 'W'; g_cPieceID['1'][1] = 'K';
		g_cPieceID['2'][0] = 'W'; g_cPieceID['2'][1] = 'Q';
		g_cPieceID['3'][0] = 'W'; g_cPieceID['3'][1] = 'B';
		g_cPieceID['4'][0] = 'W'; g_cPieceID['4'][1] = 'R';
		g_cPieceID['5'][0] = 'W'; g_cPieceID['5'][1] = 'H';
		g_cPieceID['6'][0] = 'W'; g_cPieceID['6'][1] = 'P';
		g_cPieceID['A'][0] = 'B'; g_cPieceID['A'][1] = 'K';
		g_cPieceID['B'][0] = 'B'; g_cPieceID['B'][1] = 'Q';
		g_cPieceID['C'][0] = 'B'; g_cPieceID['C'][1] = 'B';
		g_cPieceID['D'][0] = 'B'; g_cPieceID['D'][1] = 'R';
		g_cPieceID['E'][0] = 'B'; g_cPieceID['E'][1] = 'H';
		g_cPieceID['F'][0] = 'B'; g_cPieceID['F'][1] = 'P';
		
		//Creates and blanks the chess piece map
		g_cChessPieceMap = new char[BOARD_WIDTH][BOARD_LENGTH];
		for ( int j = 0; j < BOARD_LENGTH; j++ )
		{
			for ( int i = 0; i < BOARD_WIDTH; i++ )
			{
				g_cChessPieceMap[i][j] = '0';
			}
		}
		g_cChessboard = new char[BOARD_WIDTH * 4 + 2][BOARD_LENGTH * 3]; //Should be enough memory for the chessboard
		for ( int j = 0; j < BOARD_LENGTH * 3; j++ )
		{
			for ( int i = 0; i < BOARD_WIDTH * 4 + 2; i++ )
			{
				g_cChessboard[i][j] = ' ';
			}
		}
		
		g_hPieces = new CChessPiece[PIECES];
		
		//Setup for the kings - even though they're special pieces, they are still pieces
		g_hWhiteKing = new CChessPiece( 5, 1, "King", false, null, null, getMvRules("King"), '1' );
		g_hBlackKing = new CChessPiece( 5, 8, "King", true, null, null, getMvRules("King"), 'A' );
		g_hWhiteKing.m_hBlackKing = g_hBlackKing;
		g_hBlackKing.m_hWhiteKing = g_hWhiteKing;
		g_hPieces[0] = g_hWhiteKing;
		g_hPieces[1] = g_hBlackKing;
		
		//Setup for the white non-king pieces
		{
			g_hPieces[2] = new CChessPiece( 1, 1, "Rook", false, g_hWhiteKing, g_hBlackKing, getMvRules("Rook"), '4' );
			g_hPieces[3] = new CChessPiece( 8, 1, "Rook", false, g_hWhiteKing, g_hBlackKing, getMvRules("Rook"), '4' );
			g_hPieces[4] = new CChessPiece( 2, 1, "Knight", false, g_hWhiteKing, g_hBlackKing, getMvRules("Knight"), '5' );
			g_hPieces[5] = new CChessPiece( 7, 1, "Knight", false, g_hWhiteKing, g_hBlackKing, getMvRules("Knight"), '5' );
			g_hPieces[6] = new CChessPiece( 3, 1, "Bishop", false, g_hWhiteKing, g_hBlackKing, getMvRules("Bishop"), '3' );
			g_hPieces[7] = new CChessPiece( 6, 1, "Bishop", false, g_hWhiteKing, g_hBlackKing, getMvRules("Bishop"), '3' );
			g_hPieces[8] = new CChessPiece( 4, 1, "Queen", false, g_hWhiteKing, g_hBlackKing, getMvRules("Queen"), '2' );
			for ( int i = 9; i < 17; i++ )
			{
				g_hPieces[i] = new CChessPawn( i - 8, 2, false, g_hWhiteKing, g_hBlackKing );
			}
		}
		
		//Setup for the black non-king pieces
		{
			g_hPieces[17] = new CChessPiece( 1, 8, "Rook", true, g_hWhiteKing, g_hBlackKing, getMvRules("Rook"), 'D' );
			g_hPieces[18] = new CChessPiece( 8, 8, "Rook", true, g_hWhiteKing, g_hBlackKing, getMvRules("Rook"), 'D' );
			g_hPieces[19] = new CChessPiece( 2, 8, "Knight", true, g_hWhiteKing, g_hBlackKing, getMvRules("Knight"), 'E' );
			g_hPieces[20] = new CChessPiece( 7, 8, "Knight", true, g_hWhiteKing, g_hBlackKing, getMvRules("Knight"), 'E' );
			g_hPieces[21] = new CChessPiece( 3, 8, "Bishop", true, g_hWhiteKing, g_hBlackKing, getMvRules("Bishop"), 'C' );
			g_hPieces[22] = new CChessPiece( 6, 8, "Bishop", true, g_hWhiteKing, g_hBlackKing, getMvRules("Bishop"), 'C' );
			g_hPieces[23] = new CChessPiece( 4, 8, "Queen", true, g_hWhiteKing, g_hBlackKing, getMvRules("Queen"), 'B' );
			for ( int i = 24; i < 32; i++ )
			{
				g_hPieces[i] = new CChessPawn( i - 23, 7, true, g_hWhiteKing, g_hBlackKing );
			}
		}
		
		System.out.println( "Chess\n- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -" );
		System.out.println( "How to play:" );
		System.out.println( "Enter coordinates in this format: <x> <y>" );
		System.out.println( "Two-player turn-taking game on an 8 x 8 chessboard." );
		System.out.println( "First, you must select a piece by typing \"move\", then the coordinates" );
		System.out.println( "of the piece you want to control, and then you must enter the coordinates" );
		System.out.println( "of where to move or attack." );
		System.out.println( "To castle your king with a rook, enter \"castle\" and then the direction" );
		System.out.println( "of the rook for the king to castle with (L or R). Neither piece can have" );
		System.out.println( "moved prior, no pieces are between the king or rook, and you cannot castle" );
		System.out.println( "in, through, or out of check." );
		System.out.println( "If you're in check, you must either move your king out of check");
		System.out.println( "or move a piece to block the attacking enemy's line of sight." );
		System.out.println( "If this is not possible, you land in checkmate and lose the game." );
		System.out.println( "You can also type \"surrender\" (without quote marks) to resign" );
		System.out.println( "if your checkmate is inevitable, or if you're in a stalemate." );
		System.out.println( "Type \"help\" if you forget the commands. " ); 
		System.out.println( "Press Enter to continue..." );
		in.nextLine();
		
		boolean bCheckmateEitherTeam = false;
		boolean bCurPly = true;
		String cmd = null;
		do
		{
			bCurPly = !bCurPly;
			boolean bShouldRepeatInnerLoop;
			pseudoClear();
			updateGameAndPieces( true );
			if ( (bCurPly ? g_hBlackKing : g_hWhiteKing).m_bInCheck )
			{
				System.out.println( "Your king is in check!" );
				if ( verifyPossibleSituation( bCurPly ) )
				{
					bCheckmateEitherTeam = true;
					System.out.println( "Checkmate!" );
					break;
				}
			}
			
			int iFailedCmdCounter = 0;
			do
			{
				bShouldRepeatInnerLoop = false;
				System.out.print( (bCurPly ? "Black " : "White ") + "Player's Turn: " );
				cmd = in.nextLine();
				if ( cmd.toLowerCase().equals("help") )
				{
					System.out.println( "\nHelp\n- - - - - - - - - - - - - - - - - - - - -" );
					System.out.println( "Numerical coordinate format: <x> <y>" );
					System.out.println( "Commands:" );
					System.out.println( "help - displays this very list" );
					System.out.println( "surrender - resign from the game" );
					System.out.println( "move - select a piece and move it" );
					System.out.println( "castle - castle your king with a rook\n" );
					bShouldRepeatInnerLoop = true;
				}
				else if ( cmd.toLowerCase().equals("surrender") ) //Automatic win for other team
				{
					System.out.println( (bCurPly ? "Black " : "White ") + "Player resigns!\n" );
					System.out.println( (bCurPly ? "White " : "Black ") + "Player wins the game!" );
					return;
				}
				else if ( cmd.toLowerCase().equals("move") )
				{
					boolean bBool1;
					do	//Repeat piece selection if input is invalid
					{
						bBool1 = false;
						System.out.print( "Enter the coordinates of the piece to move (C to cancel): " );
						cmd = in.nextLine();
						if ( cmd.toLowerCase().equals("c") )
						{
							System.out.println( "Command canceled" );
							bShouldRepeatInnerLoop = true;
							break;
						}
						if ( cmd.length() < 3 || cmd.length() > 3 || cmd.charAt(0) <= ASCII_ZERO || cmd.charAt(0) > BOARD_WIDTH + ASCII_ZERO
								|| cmd.charAt(2) <= ASCII_ZERO || cmd.charAt(2) > BOARD_LENGTH + ASCII_ZERO )
						{
							System.out.println( "Invalid coordinates entered" );
							bBool1 = true;
							continue;	//Repeat if input is less than 1 or more than the board width
						}
						
						CChessPiece hCurPiece = getPieceAtCoords(cmd.charAt(0)-ASCII_ZERO, cmd.charAt(2)-ASCII_ZERO);
						if ( hCurPiece == null )
						{
							System.out.println( "No piece here" );
							bBool1 = true;
							continue;
						}
						if ( hCurPiece.getTeam() != bCurPly )
						{
							System.out.println( "This is not your piece" );
							bBool1 = true;
							continue;
						}
						
						boolean bBool2;
						do	//Repeat movement selection if coordinates are invalid
						{
							bBool2 = false;
							System.out.print( "Enter the coordinates to move this " + hCurPiece.getName() + " to (C to cancel): " );
							cmd = in.nextLine();
							if ( cmd.toLowerCase().equals("c") )
							{
								System.out.println( "Command canceled" );
								bBool1 = true;
								break;
							}
							if ( cmd.length() < 3 || cmd.length() > 3 || cmd.charAt(0) <= ASCII_ZERO || cmd.charAt(0) > BOARD_WIDTH + ASCII_ZERO
									|| cmd.charAt(2) <= ASCII_ZERO || cmd.charAt(2) > BOARD_LENGTH + ASCII_ZERO )
							{
								System.out.println( "Invalid coordinates entered" );
								bBool2 = true;
								continue;
							}
							
							boolean bSuccessfulMove = movePiece( hCurPiece, cmd.charAt(0)-ASCII_ZERO, cmd.charAt(2)-ASCII_ZERO, false );
							
							if ( !bSuccessfulMove )
							{
								System.out.println( "Invalid move" );
								bBool2 = true;
							}
						}
						while ( bBool2 );
					}
					while ( bBool1 );
				}
				else if ( cmd.toLowerCase().equals("castle") )
				{
					boolean bBool1;
					do
					{
						bBool1 = false;
						System.out.print( "Enter the direction of the rook you intend to castle with (L/R) (C to cancel): " );
						cmd = in.nextLine();
						if ( cmd.toLowerCase().equals("c") )
						{
							System.out.println( "Command canceled" );
							bShouldRepeatInnerLoop = true;
							break;
						}
						CChessPiece hCurRook = null;
						switch ( cmd.toLowerCase().charAt(0) )
						{
							case 'l':
								hCurRook = getPieceAtCoords(1, (bCurPly ? 8 : 1 ));
								if ( hCurRook == null )
								{
									System.out.println( "No rook to the left" );
									bBool1 = true;
									break;
								}
								if ( hCurRook.m_bMovedYet )
								{
									System.out.println( "This rook has already moved" );
									bBool1 = true;
									break;
								}
								if ( (bCurPly ? g_hBlackKing : g_hWhiteKing).m_bMovedYet )
								{
									System.out.println( "Your king has already moved" );
									bBool1 = true;
									break;
								}
								if ( !castle( bCurPly, false, hCurRook ) )
								{
									System.out.println( "Castle failed" );
									bBool1 = true;
									break;
								}
								break;
							case 'r':
								hCurRook = getPieceAtCoords(8, (bCurPly ? 8 : 1 ));
								if ( hCurRook == null )
								{
									System.out.println( "No rook to the left" );
									bBool1 = true;
									break;
								}
								if ( hCurRook.m_bMovedYet )
								{
									System.out.println( "This rook has already moved" );
									bBool1 = true;
									break;
								}
								if ( (bCurPly ? g_hBlackKing : g_hWhiteKing).m_bMovedYet )
								{
									System.out.println( "Your king has already moved" );
									bBool1 = true;
									break;
								}
								if ( !castle( bCurPly, true, hCurRook ) )
								{
									System.out.println( "Castle failed" );
									bBool1 = true;
									break;
								}
								break;
							default:
								bBool1 = true;
								System.out.println( "Invalid input" );
								continue;
						}
					}
					while ( bBool1 );
				}
				else
				{
					if ( iFailedCmdCounter < 2 )
						System.out.println( "Unrecognized command\n" );
					else	//Print random taunts if the user enters too many failed commands
						System.out.println( szThirdInvalidCommandTaunts
								[ (int)((szThirdInvalidCommandTaunts.length) * Math.random()) ]);
					
					bShouldRepeatInnerLoop = true;
					iFailedCmdCounter++;
				}
			}
			while ( bShouldRepeatInnerLoop );
			
			System.out.println( "Press Enter to refresh the Chessboard..." );
			in.nextLine();
		}
		while ( !bCheckmateEitherTeam );
		
		Assert( bCheckmateEitherTeam );	//By this point, one of the players has to be in checkmate
		if ( bCurPly )
			System.out.println( "White Player wins the game! " );
		else
			System.out.println( "Black Player wins the game!" );
	}
	
	/**
	 * Prints 25 newlines to hide the previous material
	 * in the console.
	 */
	public static void pseudoClear()
	{
		for ( int i = 0; i < 25; i++ )
		{
			System.out.println();
		}
	}
	
	/**
	 * Crashes the program if bCondition is false.
	 * Great for avoiding logic errors. Wherever you
	 * see this in the code, a pre/postcondition exists.
	 * @param bCondition - Condition to check
	 * @throws AssertionError
	 */
	public static void Assert( boolean bCondition ) throws AssertionError
	{
		if ( !bCondition )
		{
			System.err.println( "ERROR: Assertion failed!" );
			throw new AssertionError();
		}
	}
	
	/**
	 * Moves a piece, taking check into account. If a piece's
	 * king is under check, only allows the move if it will
	 * bring said king out of check.
	 * @param hPiece - Piece to move.
	 * @param x - Destination X-coordinate
	 * @param y - Destination Y-coordinate
	 * @param bVerifyOnly - If true, this function returns
	 * 				whether or not the move is valid without
	 * 				actually performing the move
	 * @return Whether or not the piece moved or can be moved
	 */
	public static boolean movePiece( CChessPiece hPiece, int x, int y, boolean bVerifyOnly )
	{
		//We verify the validity of a move under check by executing the move,
		//and reverting it and the subsequent capture of an enemy piece if the
		//king is still in check without re-drawing the chessboard.
		{
			int prevCoordX = hPiece.getCoords()[0];
			int prevCoordY = hPiece.getCoords()[1];
			CChessPiece hPieceAtTargetCoords = getPieceAtCoords(x, y);
			if ( hPiece.mvToPos(x, y, false, bVerifyOnly) )
			{
				updateGameAndPieces( false );
				//If the king's still in check, move back and revert capture
				if ( (hPiece.getTeam() ? g_hBlackKing : g_hWhiteKing).m_bInCheck )
				{
					if ( !bVerifyOnly )
						System.out.println( "Cannot execute - this move will result in/does not bring your king out of check" );
					
					hPiece.mvToPos(prevCoordX, prevCoordY, false, true);
					if ( hPieceAtTargetCoords != null )
						hPieceAtTargetCoords.setCaptureStatus( false );
					updateGameAndPieces( false );
					return false;
				}
				else
				{
					if ( bVerifyOnly )
					{
						hPiece.mvToPos(prevCoordX, prevCoordY, false, true);
						if ( hPieceAtTargetCoords != null )
							hPieceAtTargetCoords.setCaptureStatus( false );
						updateGameAndPieces( false );
					}
					
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Verifies checkmate for one of the teams. Checkmate happens
	 * if both of the following conditions are true:
	 * 		- King's in check (precondition)
	 * 		- The king can't move anywhere to save itself.
	 * 		- The team's pieces can't move anywhere to save the king.
	 * Does not check for stalemate.
	 * @param bTeam - Team to confirm/deny checkmate for.
	 * @return Whether or not the team's king is done for.
	 */
	public static boolean verifyPossibleSituation( boolean bTeam )
	{
		//First, we check to see if the king can move anywhere to save himself.
		//this will be done through trial and error.
		CChessPiece hTeamKing = bTeam ? g_hBlackKing : g_hWhiteKing;
		Assert( hTeamKing != null );
		Assert( hTeamKing.m_bInCheck );
		int hTeamKing_X = hTeamKing.getCoords()[0];
		int hTeamKing_Y = hTeamKing.getCoords()[1];
		CMoveAttackDef hCurMvRule;
		for ( int i = 0; i < hTeamKing.m_carrMvRules.length; i++ )
		{
			hCurMvRule = hTeamKing.m_carrMvRules[i];
			Assert( hCurMvRule.m_bOneShot );
			if ( hTeamKing_X + hCurMvRule.m_iXMv <= Chess.BOARD_WIDTH
					&& hTeamKing_Y + hCurMvRule.m_iYMv <= Chess.BOARD_LENGTH
					&& hTeamKing_X + hCurMvRule.m_iXMv >= 1
					&& hTeamKing_Y + hCurMvRule.m_iYMv >= 1 )
			{
				if ( movePiece( hTeamKing,
						hTeamKing_X + hCurMvRule.m_iXMv,
						hTeamKing_Y + hCurMvRule.m_iYMv,
						true ) )	//As soon as there is a legal move, end this subroutine
					return false;
			}
		}
		
		//Phase 2 is to check the enemy pieces' lines of sight toward the king
		//and see if two pieces or more are checking the king, the king can't
		//save itself, and none of the pieces can move to obstruct the check.
		CChessPiece hEnemyCheckingKing = null;
		for ( int i = 0; i < g_hPieces.length; i++ )
		{
			Assert( g_hPieces[i] != null );
			if ( g_hPieces[i].getTeam() != bTeam && g_hPieces[i].m_bCheckingEnemyKing )
			{
				if ( hEnemyCheckingKing != null )
				{
					return true;//If multiple pieces are checking an unmovable king, the team's screwed
				}
				hEnemyCheckingKing = g_hPieces[i];
			}
		}
		int[][] iCheckLOSToKingData = hEnemyCheckingKing.m_iCheckLOSToKingData.clone();
		for ( int j = 0; j < g_hPieces.length; j++ )	//j = current piece
		{
			for ( int i = 0; i < iCheckLOSToKingData.length; i++ )	//i = current row of iCheckLOSToKingData
			{
				if ( g_hPieces[j].getTeam() == bTeam
						&& iCheckLOSToKingData[i][0] >= 1
						&& iCheckLOSToKingData[i][1] >= 1
						&& iCheckLOSToKingData[i][0] <= Chess.BOARD_WIDTH
						&& iCheckLOSToKingData[i][1] <= Chess.BOARD_LENGTH )
				{	//movePiece() accounts for both continuous AND one-shot movement
					if ( movePiece( g_hPieces[j], iCheckLOSToKingData[i][0], iCheckLOSToKingData[i][1], true ) )
					{
						
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Performs a castle between a king and a rook. Conditions:
	 * 	- Pieces must be on the first rank
	 * 	- Neither piece moved
	 * 	- No obstructing pieces
	 * 	- King's not in check
	 * 	- Castling doesn't go through check
	 * 	- Castling doesn't result in check
	 * @param bTeam - Team to castle against
	 * @param bDir - Direction (false = left)
	 * @param hRook	- Rook to castle with the king.
	 * @return Whether or not the castle was successful.
	 */
	public static boolean castle( boolean bTeam, boolean bDir, CChessPiece hRook )
	{
		CChessPiece hKing = bTeam ? g_hBlackKing : g_hWhiteKing;
		if ( hKing.m_bInCheck )
			return false;
		if ( hKing.getCoords()[1] != hRook.getCoords()[1] )
			return false;
		if ( hKing.getCoords()[1] != (bTeam ? 8 : 1))
			return false;
		if ( hKing.m_bMovedYet || hRook.m_bMovedYet )
			return false;
		
		if ( movePiece(hKing, hKing.getCoords()[0] + (bDir ? 1 : -1), hKing.getCoords()[1], false) )
		{
			updateGameAndPieces( false );
			if ( movePiece(hKing, hKing.getCoords()[0] + (bDir ? 1 : -1), hKing.getCoords()[1], false) )
			{
				Assert( getPieceAtCoords( hKing.getCoords()[0] + (bDir ? -1 : 1), hKing.getCoords()[1]) == null );
				g_cChessPieceMap[hRook.getCoords()[0] - 1][hRook.getCoords()[1] - 1] = '0';
				hRook.setCoords(hKing.getCoords()[0] + (bDir ? -1 : 1), hKing.getCoords()[1]);	//Gotta bypass mvToPos() here because it "jumps"
				hRook.m_bMovedYet = true;
				updateGameAndPieces( false );
				return true;
			}
			else
			{
				movePiece(hKing, hKing.getCoords()[0] + (bDir ? -1 : 1), hKing.getCoords()[1], false);
				return false;
			}
		}
		
		return false;
	}
	
	/**
	 * Updates all the pieces in g_hPieces, and updates
	 * the visible chessboard to match the chess piece map.
	 * @param bUpdateChessboard - If this subroutine should
	 * 							update the visible chessboard
	 */
	public static void updateGameAndPieces( boolean bUpdateChessboard )
	{
		g_hWhiteKing.m_bInCheck = false;
		g_hBlackKing.m_bInCheck = false;
		
		for ( int i = 0; i < g_hPieces.length; i++ )
		{
			Assert( g_hPieces[i] != null );
			g_hPieces[i].updateStatus();
		}
		
		if ( bUpdateChessboard )//If we're not gonna draw the chessboard, don't bother updating it either
		{
			for ( int j = 0; j < BOARD_LENGTH * 2; j += 2 )
			{
				for ( int i = 0; i < BOARD_WIDTH * 3; i += 3 )
				{
					g_cChessboard[i][j] = '-';
					g_cChessboard[i + 1][j] = '-';
					g_cChessboard[i + 2][j] = '-';
					g_cChessboard[i + 3][j] = '-';
					
					g_cChessboard[i][j + 1] = '|';
					g_cChessboard[i + 1][j + 1] = g_cPieceID[ g_cChessPieceMap[i / 3][j / 2] ][0];
					g_cChessboard[i + 2][j + 1] = g_cPieceID[ g_cChessPieceMap[i / 3][j / 2] ][1];
				}
				g_cChessboard[BOARD_WIDTH * 3][j + 1] = '|';
				g_cChessboard[BOARD_WIDTH * 3 + 1][j + 1] = ' ';
				g_cChessboard[BOARD_WIDTH * 3 + 2][j + 1] = (char)(49 + j / 2);	//49 = 1 in ASCII
			}
			for ( int i = 0; i < BOARD_WIDTH * 3 + 1; i++ )
				g_cChessboard[i][BOARD_LENGTH * 2] = '-';
			
			
			drawChessboard();
		}
	}
	
	/**
	 * Draws the chessboard - that is, g_cChessboard[][].
	 */
	public static void drawChessboard()
	{
		for ( int i = 3; i <= BOARD_WIDTH * 3; i += 3 )
		{
			System.out.print( " " + (i / 3) + " " );
		}
		System.out.println();
		for ( int j = 0; j < BOARD_LENGTH * 2 + 1; j++ )
		{
			for ( int i = 0; i < BOARD_WIDTH * 3 + 3; i++ )
				System.out.print( g_cChessboard[i][j] );
			
			System.out.println();
		}
	}
	
	/**
	 * Retrieves the piece at the specified coordinates.
	 * @param x - X-coordinate
	 * @param y - Y-coordinate
	 * @return CChessPiece at the coordinates (null if none found)
	 */
	public static CChessPiece getPieceAtCoords( int x, int y )
	{
		for ( int i = 0; i < g_hPieces.length; i++ )
		{
			Assert( g_hPieces[i] != null );
			if ( g_hPieces[i].getCoords()[0] == x && g_hPieces[i].getCoords()[1] == y
					&& !g_hPieces[i].isCaptured())
				return g_hPieces[i];
		}
		
		return null;
	}
	
	/**
	 * Gets the movement/attack rules for a specified piece.
	 * @param szName - Name of the piece to get the move/attack rules of
	 * @return CMoveAttackDef array containing the move/attack rules
	 */
	private static CMoveAttackDef[] getMvRules( String szName )
	{
		CMoveAttackDef[] mvRules = null;
		if ( szName.equals("King") )	//One-shot horiz/vert/diag
		{
			mvRules = new CMoveAttackDef[16];
			mvRules[0] = new CMoveAttackDef(1, 0, true, false );
			mvRules[1] = new CMoveAttackDef(1, 1, true, false );
			mvRules[2] = new CMoveAttackDef(0, 1, true, false );
			mvRules[3] = new CMoveAttackDef(-1, 1, true, false );
			mvRules[4] = new CMoveAttackDef(-1, 0, true, false );
			mvRules[5] = new CMoveAttackDef(-1, -1, true, false );
			mvRules[6] = new CMoveAttackDef(0, -1, true, false );
			mvRules[7] = new CMoveAttackDef(1, -1, true, false );
			mvRules[8] = new CMoveAttackDef(1, 0, true, true );
			mvRules[9] = new CMoveAttackDef(1, 1, true, true );
			mvRules[10] = new CMoveAttackDef(0, 1, true, true );
			mvRules[11] = new CMoveAttackDef(-1, 1, true, true );
			mvRules[12] = new CMoveAttackDef(-1, 0, true, true );
			mvRules[13] = new CMoveAttackDef(-1, -1, true, true );
			mvRules[14] = new CMoveAttackDef(0, -1, true, true );
			mvRules[15] = new CMoveAttackDef(1, -1, true, true );
		}
		if ( szName.equals("Queen") )	//Horiz/vert/diag
		{
			mvRules = new CMoveAttackDef[16];
			mvRules[0] = new CMoveAttackDef(1, 0, false, false );
			mvRules[1] = new CMoveAttackDef(1, 1, false, false );
			mvRules[2] = new CMoveAttackDef(0, 1, false, false );
			mvRules[3] = new CMoveAttackDef(-1, 1, false, false );
			mvRules[4] = new CMoveAttackDef(-1, 0, false, false );
			mvRules[5] = new CMoveAttackDef(-1, -1, false, false );
			mvRules[6] = new CMoveAttackDef(0, -1, false, false );
			mvRules[7] = new CMoveAttackDef(1, -1, false, false );
			mvRules[8] = new CMoveAttackDef(1, 0, false, true );
			mvRules[9] = new CMoveAttackDef(1, 1, false, true );
			mvRules[10] = new CMoveAttackDef(0, 1, false, true );
			mvRules[11] = new CMoveAttackDef(-1, 1, false, true );
			mvRules[12] = new CMoveAttackDef(-1, 0, false, true );
			mvRules[13] = new CMoveAttackDef(-1, -1, false, true );
			mvRules[14] = new CMoveAttackDef(0, -1, false, true );
			mvRules[15] = new CMoveAttackDef(1, -1, false, true );
		}
		if ( szName.equals("Rook") )	//Horiz/vert
		{
			mvRules = new CMoveAttackDef[8];
			mvRules[0] = new CMoveAttackDef(1, 0, false, false );
			mvRules[1] = new CMoveAttackDef(0, 1, false, false );
			mvRules[2] = new CMoveAttackDef(-1, 0, false, false );
			mvRules[3] = new CMoveAttackDef(0, -1, false, false );
			mvRules[4] = new CMoveAttackDef(1, 0, false, true );
			mvRules[5] = new CMoveAttackDef(0, 1, false, true );
			mvRules[6] = new CMoveAttackDef(-1, 0, false, true );
			mvRules[7] = new CMoveAttackDef(0, -1, false, true );
		}
		if ( szName.equals("Bishop") )	//Diag
		{
			mvRules = new CMoveAttackDef[8];
			mvRules[0] = new CMoveAttackDef(1, 1, false, false );
			mvRules[1] = new CMoveAttackDef(-1, 1, false, false );
			mvRules[2] = new CMoveAttackDef(-1, -1, false, false );
			mvRules[3] = new CMoveAttackDef(1, -1, false, false );
			mvRules[4] = new CMoveAttackDef(1, 1, false, true );
			mvRules[5] = new CMoveAttackDef(-1, 1, false, true );
			mvRules[6] = new CMoveAttackDef(-1, -1, false, true );
			mvRules[7] = new CMoveAttackDef(1, -1, false, true );
		}
		if ( szName.equals("Knight") )	//L-shape
		{
			mvRules = new CMoveAttackDef[16];
			mvRules[0] = new CMoveAttackDef( 2, 1, true, false );
			mvRules[1] = new CMoveAttackDef( -2, 1, true, false );
			mvRules[2] = new CMoveAttackDef( 2, -1, true, false );
			mvRules[3] = new CMoveAttackDef( -2, -1, true, false );
			mvRules[4] = new CMoveAttackDef( 1, 2, true, false );
			mvRules[5] = new CMoveAttackDef( -1, 2, true, false );
			mvRules[6] = new CMoveAttackDef( 1, -2, true, false );
			mvRules[7] = new CMoveAttackDef( -1, -2, true, false );
			mvRules[8] = new CMoveAttackDef( 2, 1, true, true );
			mvRules[9] = new CMoveAttackDef( -2, 1, true, true );
			mvRules[10] = new CMoveAttackDef( 2, -1, true, true );
			mvRules[11] = new CMoveAttackDef( -2, -1, true, true );
			mvRules[12] = new CMoveAttackDef( 1, 2, true, true );
			mvRules[13] = new CMoveAttackDef( -1, 2, true, true );
			mvRules[14] = new CMoveAttackDef( 1, -2, true, true );
			mvRules[15] = new CMoveAttackDef( -1, -2, true, true );
		}
		
		return mvRules;
	}
}
