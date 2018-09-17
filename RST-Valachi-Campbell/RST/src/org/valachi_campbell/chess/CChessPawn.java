package org.valachi_campbell.chess;

import java.util.Scanner;
import java.lang.Math;

/**
 * Chess pawn. Has special properties that
 * a normal chess piece does not.
 * @author Gabriel Valachi
 * @author Liam Campbell
 */
public class CChessPawn extends CChessPiece
{
	private static String[] m_szRefuseRetrieveMessages;	//To taunt the player if they refuse to return a capped piece
	
	/**
	 * Constructor.
	 * @param x - Initial X coordinate
	 * @param y - Initial Y coordinate
	 * @param team - False for white, true for black
	 * @param whiteKing - White king
	 * @param blackKing - Black king
	 */
	public CChessPawn( int x, int y, boolean team, CChessPiece whiteKing,
						CChessPiece blackKing )
	{
		super( x, y, "Pawn", team, whiteKing, blackKing, null,
				(team ? 'F' : '6') );
		
		m_carrMvRules = new CMoveAttackDef[4];	//Custom attack rules specific to this class
		m_carrMvRules[0] = new CMoveAttackDef( 0, 2, true, false );
		m_carrMvRules[1] = new CMoveAttackDef( 0, 1, true, false );
		m_carrMvRules[2] = new CMoveAttackDef( 1, 1, true, true );
		m_carrMvRules[3] = new CMoveAttackDef( -1, 1, true, true );
		
		if ( team )	//If it's on black team, invert the movement rules
		{
			m_carrMvRules[0].m_iYMv = -m_carrMvRules[0].m_iYMv;
			m_carrMvRules[1].m_iYMv = -m_carrMvRules[1].m_iYMv;
			m_carrMvRules[2].m_iYMv = -m_carrMvRules[2].m_iYMv;
			m_carrMvRules[3].m_iYMv = -m_carrMvRules[3].m_iYMv;
		}
		
		m_szRefuseRetrieveMessages = new String[6];
		m_szRefuseRetrieveMessages[0] = "...so be it, then.";
		m_szRefuseRetrieveMessages[1] = "But... why?";
		m_szRefuseRetrieveMessages[2] = "Sicko.";
		m_szRefuseRetrieveMessages[3] = "No, your pieces won't free themselves from prison, this isn't Rambo.";
		m_szRefuseRetrieveMessages[4] = "That can't be right.";
		m_szRefuseRetrieveMessages[5] = "Fine. N00blet.";
	}
	
	/**
	 * Checks if a piece is obstructing this piece. Only
	 * useful for the first move where you can move two
	 * squares.
	 * @return True if there's a piece obstructing this one.
	 */
	public boolean isPieceObstructingThis()
	{
		if ( (getTeam()
				&& Chess.getPieceAtCoords( this.getCoords()[0], this.getCoords()[1] - 1 ) != null)
				|| (!getTeam()
					&& Chess.getPieceAtCoords( this.getCoords()[0], this.getCoords()[1] + 1 ) != null) )
			return true;
		
		return false;
	}
	
	/**
	 * Same as the superclass mvToPos(), but
	 * also sets m_bMovedYet to true.
	 * @return Whether or not this piece moved.
	 */
	@Override
	public boolean mvToPos( int xPos, int yPos, boolean bVerifyOnly, boolean bSuppressCapMsg )
	{
		if ( this.isPieceObstructingThis() && yPos == this.getCoords()[1] + 2 )
			return false;
		
		if ( super.mvToPos(xPos, yPos, bVerifyOnly, bSuppressCapMsg) )
		{
			if ( !bVerifyOnly )
			{
				Chess.Assert( this.getCoords()[0] == xPos );
				Chess.Assert( this.getCoords()[1] == yPos );
				m_bMovedYet = true;
			}
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Called every time updateGameAndPieces() is called
	 * in the Chess class.
	 * For CChessPawn, checks if the piece moved yet. Also
	 * checks if it's on the other side of the board - if
	 * that's the case, the respective player can retrieve
	 * a piece of theirs that was captured.
	 */
	@Override
	public void updateStatus()
	{
		super.updateStatus();
		
		if ( m_bMovedYet )
			m_carrMvRules[0].m_iYMv = 1;
		
		if ( (( this.getCoords()[1] == Chess.BOARD_LENGTH && !this.getTeam() )
				|| this.getCoords()[1] == 1 && this.getTeam() )
				&& !this.isCaptured() )
		{
			Chess.Assert( !isCaptured() );
			this.setCaptureStatus( true );
			Chess.g_cChessPieceMap[this.getCoords()[0] - 1][this.getCoords()[1] - 1] = '0';
			String szPiece;
			do
			{
				System.out.print( "Your " + (this.getTeam() ? "Black " : "White ")
						+ "Pawn has reached the enemy lines!\nSelect a piece to retrieve (0 to cancel): " );
				szPiece = (new Scanner(System.in)).nextLine();
				
				if ( szPiece.startsWith("0") )//Who on Earth would do this I don't know, but we still must have this option just in case
				{
					System.out.println( m_szRefuseRetrieveMessages
							[ (int)((m_szRefuseRetrieveMessages.length) * Math.random()) ] );
					break;
				}
				
				for ( int i = 0; i < Chess.g_hPieces.length; i++ )//Cycles through all the pieces and looks for a matching name
				{
					if ( Chess.g_hPieces[i].isCaptured()
						&& Chess.g_hPieces[i].getTeam() == this.getTeam()
						&& Chess.g_hPieces[i].getName().equals( szPiece ) )
					{
						if ( Chess.g_hPieces[i] instanceof CChessPawn )
						{
							System.out.println( "You can't retrieve your own pawns" );
							continue;
						}
						
						System.out.println( "Retrieved a " + szPiece );
						Chess.g_hPieces[i].setCaptureStatus( false );
						Chess.g_hPieces[i].setCoords( getCoords()[0], getCoords()[1] );
						return;
					}
				}
				System.out.println( "No such piece captured" );
			}
			while ( true );
		}
	}
}
