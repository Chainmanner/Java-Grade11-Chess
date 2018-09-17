package org.valachi_campbell.chess;

/**
 * Class that represents all chess pieces,
 * except for the pawn.
 * @author Gabriel Valachi
 * @author Liam Campbell
 */
public class CChessPiece
{
	private int m_iXPos;
	private int m_iYPos;
	
	private final String m_szName;
	private final char m_cIDChar;
	
	private final boolean m_bTeam;	//False = white
	private boolean m_bIsCaptured;
	
	protected boolean m_bMovedYet;
	
	//The lower two variables are for verifying checkmate
	protected int[][] m_iCheckLOSToKingData;
	protected boolean m_bCheckingEnemyKing;
	protected boolean m_bInCheck;
	//m_bInCheck applies only for the kings. No point in creating a new class because
	//aside from their importance, kings really have no special properties.
	
	protected CChessPiece m_hWhiteKing;
	protected CChessPiece m_hBlackKing;
	
	protected CMoveAttackDef[] m_carrMvRules;
	
	/**
	 * Constructor.
	 * @param x - Initial X coordinate
	 * @param y - Initial Y coordinate
	 * @param name - Piece name
	 * @param team - False for white, true for black
	 * @param whiteKing - White king
	 * @param blackKing - Black king
	 * @param mvRules[] - Movement/attack behavior
	 */
	public CChessPiece( int x, int y, String name, boolean team, CChessPiece whiteKing,
						CChessPiece blackKing, CMoveAttackDef[] mvRules, char cIDChar )
	{
		m_iXPos = x;
		m_iYPos = y;
		m_szName = name;
		m_bTeam = team;
		m_bInCheck = false;
		m_bIsCaptured = false;
		m_hWhiteKing = whiteKing;
		m_hBlackKing = blackKing;
		m_carrMvRules = mvRules;
		m_cIDChar = cIDChar;
		
		if ( name.equals("King") )
		{
			if ( team )
				m_hBlackKing = this;
			else
				m_hWhiteKing = this;
		}
		
		m_iCheckLOSToKingData = new int[16][2];
		m_bCheckingEnemyKing = false;
		m_bMovedYet = false;
	}
	
	/**
	 * Gets the name of this piece.
	 * @return m_szName
	 */
	public String getName()
	{
		return m_szName;
	}
	
	/**
	 * Which team is this piece on?
	 * @return m_bTeam
	 */
	public boolean getTeam()
	{
		return m_bTeam;
	}
	
	/**
	 * Gets the coordinates of this piece
	 * @return 1D Array of coordinates:
	 * 			0 has m_iXPos, 1 has m_iYPos
	 */
	public int[] getCoords()
	{
		int[] ret = { m_iXPos, m_iYPos };
		return ret;
	}
	
	/**
	 * Sets this piece's coordinates
	 * @param x - X coordinate
	 * @param y - Y coordinate
	 */
	public void setCoords( int x, int y )
	{
		m_iXPos = x;
		m_iYPos = y;
	}
	
	/**
	 * Has this piece been captured?
	 * @return m_bIsCaptured
	 */
	public boolean isCaptured()
	{
		return m_bIsCaptured;
	}
	
	/**
	 * Capture/return this piece.
	 * @param bCap - True to capture this piece.
	 */
	public void setCaptureStatus( boolean bCap )
	{
		m_bIsCaptured = bCap;
	}
	
	/**
	 * Checks if a piece can move to a position. If so,
	 * returns true and moves the piece. Does not take
	 * check into account, so it's less safe than
	 * Chess.movePiece().
	 * @param xPos - X Position.
	 * @param yPos - Y Position.
	 * @param bVerifyOnly - If true, only checks if it CAN
	 * 				move to the specified position without
	 * 				actually moving it
	 * @return Whether or not the piece was moved.
	 */
	public boolean mvToPos( int xPos, int yPos, boolean bVerifyOnly, boolean bSuppressCapMsg )
	{
		Chess.Assert( xPos > 0 && xPos <= Chess.BOARD_WIDTH );
		Chess.Assert( yPos > 0 && yPos <= Chess.BOARD_LENGTH );
		
		if ( xPos == this.m_iXPos && yPos == this.m_iYPos )
			return false;
		
		if ( this.m_bIsCaptured )
			return false;
		
		//Here, we check every move rule and see if a piece falls within one of them
		for ( int i = 0; i < m_carrMvRules.length; i++ )
		{
			Chess.Assert( m_carrMvRules[i] != null );
			int iarrLOS[] = { 0, 0, 0 };
			CChessPiece piece = Chess.getPieceAtCoords(xPos, yPos);
			if ( m_carrMvRules[i].m_bOneShot )
			{
				if ( m_carrMvRules[i].m_iXMv + this.m_iXPos == xPos
						&& m_carrMvRules[i].m_iYMv + this.m_iYPos == yPos )
				{
					if ( m_carrMvRules[i].m_bAttackRule
							&& piece != null
							&& piece.getTeam() != this.getTeam() )
					{
						if ( !bVerifyOnly )
							unsafeMvToPos( xPos, yPos, bSuppressCapMsg );
						
						return true;
					}
					else if ( !m_carrMvRules[i].m_bAttackRule
							&& piece == null )
					{
						if ( !bVerifyOnly )
							unsafeMvToPos( xPos, yPos, bSuppressCapMsg );
						
						return true;
					}
				}
			}
			else
			{
				//Checks the line-of-sight to a position.
				//If piece has been replaced, we go instead
				//to the new coordinates returned
				iarrLOS = this.hasLOSToPosition(xPos, yPos, m_carrMvRules[i], false);
				if ( iarrLOS[0] == 1 )
				{
					if ( !bVerifyOnly )
						unsafeMvToPos( xPos, yPos, bSuppressCapMsg );
					
					return true;
				}
				else if ( iarrLOS[0] == 2 )
				{
					Chess.Assert( iarrLOS[1] >= 1 && iarrLOS[1] <= Chess.BOARD_WIDTH );
					Chess.Assert( iarrLOS[2] >= 1 && iarrLOS[2] <= Chess.BOARD_LENGTH );
					
					if ( !bVerifyOnly )
						unsafeMvToPos( iarrLOS[1], iarrLOS[2], bSuppressCapMsg );
					
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Moves to a position and captures an enemy
	 * if present. Less safe than mvToPos(), since
	 * it does not check the movement/attack rules.
	 * @param x - X coordinate
	 * @param y - Y coordinate
	 */
	private void unsafeMvToPos( int x, int y, boolean bSuppressCaptureMessage )
	{
		if ( Chess.getPieceAtCoords(x, y) != null
			&& Chess.getPieceAtCoords(x, y).getTeam() != this.m_bTeam )
		{
			if ( !bSuppressCaptureMessage )
			{
				System.out.println( (m_bTeam ? "Black " : "White ") + m_szName
					+ " has captured an enemy " + Chess.getPieceAtCoords(x, y).getName() );
			}
			Chess.getPieceAtCoords(x, y).setCaptureStatus( true );
		}
		
		Chess.g_cChessPieceMap[m_iXPos - 1][m_iYPos - 1] = '0';
		m_bMovedYet = true;
		this.setCoords(x, y);
	}
	
	/**
	 * Checks if an attack rule of this piece has a
	 * line-of-sight to a position and/or enemy.
	 * @param x - X coordinate of the position.
	 * @param y - Y coordinate of the position.
	 * @param mvRule - Movement/attack rule to check.
	 * @param bGenerateLOSToKingData - if this is set to
	 * 					true, generates a list of positions
	 * 					an enemy piece can move to in order
	 * 					to bring their king out of check. This
	 * 					is more useful for verifying checkmate.
	 * @return Integer array containing the following data:
	 * 			[0] - Line-of-sight status:
	 * 					0 = No LOS, or LOS obstructed by friendly
	 * 					1 = Clear LOS
	 * 					2 = Enemy blocking LOS
	 * 			[1] - X-coordinate of enemy blocking LOS, if [0] == 2
	 * 			[2] - Y-coodinate of enemy blocking LOS, if [0] == 2
	 * 			Damn you, Java, for not implementing passing by reference.
	 */
	public int[] hasLOSToPosition( int x, int y, CMoveAttackDef mvRule, boolean bGenerateLOSToKingData )
	{
		int ret[] = { 0, 0, 0 };
		Chess.Assert( x >= 1 && x <= Chess.BOARD_WIDTH );
		Chess.Assert( y >= 1 && y <= Chess.BOARD_LENGTH );
		Chess.Assert( !mvRule.m_bOneShot );
		
		int iTempX = this.getCoords()[0], iTempY = this.getCoords()[1];
		CChessPiece piece = null, hEnemy = Chess.getPieceAtCoords(x, y);
		boolean bPieceBlockingLOS = false;	//We don't want to cap any other piece unless it blocks LOS
		
		int counter = 0;
		while ( (iTempX <= Chess.BOARD_WIDTH && iTempY <= Chess.BOARD_LENGTH)
				&& (iTempX >= 0 && iTempY >= 0))
		{
			if ( bGenerateLOSToKingData )//This assumes that the enemy king's what we're looking toward
			{
				Chess.Assert( this.m_iCheckLOSToKingData != null );
				Chess.Assert( (m_bTeam ? m_hWhiteKing : m_hBlackKing).getCoords()[0] == x );
				Chess.Assert( (m_bTeam ? m_hWhiteKing : m_hBlackKing).getCoords()[1] == y );
				
				//We don't want the king's position to be included in m_iCheckLOSToKingData, or
				//else we won't be able to verify checkmate. At least so it seems, since
				//adding this if statement fixed one such bug.
				if ( (m_bTeam ? m_hWhiteKing : m_hBlackKing).getCoords()[0] != iTempX
						&& (m_bTeam ? m_hWhiteKing : m_hBlackKing).getCoords()[1] != iTempY )
				{
					this.m_iCheckLOSToKingData[counter][0] = iTempX;
					this.m_iCheckLOSToKingData[counter][1] = iTempY;
				}
			}
			
			piece = Chess.getPieceAtCoords(iTempX, iTempY);
			//If we find a piece that is not hEnemy but is still
			//an enemy, we change it so that it becomes the intended target
			if ( piece != null && piece != this && piece != hEnemy )
			{
				if ( piece.getTeam() == this.m_bTeam )
				{
					return ret;
				}
				else
				{
					bPieceBlockingLOS = true;
					if ( mvRule.m_bAttackRule )	//Can only capture if this is an attack rule
					{
						ret[1] = piece.getCoords()[0];
						ret[2] = piece.getCoords()[1];
					}
				}
			}
			
			if ( iTempX == x && iTempY == y )
			{
				if ( bPieceBlockingLOS && mvRule.m_bAttackRule )
					ret[0] = 2;
				else if ( (!bPieceBlockingLOS && !mvRule.m_bAttackRule) && Chess.getPieceAtCoords(x, y) == null 
							|| ( !bPieceBlockingLOS && mvRule.m_bAttackRule && Chess.getPieceAtCoords(x, y) != null
									&& Chess.getPieceAtCoords(x, y).getTeam() != this.getTeam() ) )
					ret[0] = 1;
				
				return ret;
			}
			
			iTempX += mvRule.m_iXMv;
			iTempY += mvRule.m_iYMv;
			
			if ( bGenerateLOSToKingData )
				counter++;
			
			Chess.Assert( mvRule.m_iXMv != 0 || mvRule.m_iYMv != 0 );
		}
		
		
		return ret;
	}
	
	/**
	 * Called every time updateGameAndPieces() is called in
	 * the Chess class.
	 */
	public void updateStatus()
	{
		Chess.Assert( m_iXPos >= 1 && m_iXPos <= Chess.BOARD_WIDTH );
		Chess.Assert( m_iYPos >= 1 && m_iYPos <= Chess.BOARD_LENGTH );
		
		if ( this.m_bIsCaptured )
			return;	//Everything below this only works for active pieces
		
		Chess.g_cChessPieceMap[m_iXPos - 1][m_iYPos - 1] = this.m_cIDChar;
		
		Chess.Assert( m_hWhiteKing != null );
		Chess.Assert( m_hBlackKing != null );
		m_bCheckingEnemyKing = false;
		int enemyKingX = (m_bTeam ? m_hWhiteKing : m_hBlackKing).getCoords()[0];
		int enemyKingY = (m_bTeam ? m_hWhiteKing : m_hBlackKing).getCoords()[1];
		for ( int j = 0; j < this.m_carrMvRules.length; j++ )	//j = current attack rule
		{
			if ( !m_carrMvRules[j].m_bAttackRule )	//We can only check an enemy king using attack rules
				continue;

			if ( !m_carrMvRules[j].m_bOneShot )	//Checks if we have a line-of-sight to the enemy king
			{
				if ( this.hasLOSToPosition(enemyKingX, enemyKingY, m_carrMvRules[j], false)[0] == 1 )
				{
					(m_bTeam ? m_hWhiteKing : m_hBlackKing).m_bInCheck = true;
					m_bCheckingEnemyKing = true;
					//Also needs to generate LOS-to-king data to verify whether or not the enemy's in checkmate
					this.hasLOSToPosition( enemyKingX, enemyKingY, m_carrMvRules[j], true );
					break;
				}
			}
			else	//Checks if we can move to where the king is
			{
				if ( m_carrMvRules[j].m_iXMv + getCoords()[0] == enemyKingX
						&& m_carrMvRules[j].m_iYMv + getCoords()[1] == enemyKingY )
				{
					{
						(m_bTeam ? m_hWhiteKing : m_hBlackKing).m_bInCheck = true;
						m_bCheckingEnemyKing = true;
						break;
					}
				}
			}
		}
	}
}
