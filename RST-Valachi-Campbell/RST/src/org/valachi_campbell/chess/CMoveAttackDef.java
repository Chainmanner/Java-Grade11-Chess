package org.valachi_campbell.chess;

/**
 * Storage class for movement and attack rules,
 * for the chess pieces.
 * @author Gabriel Valachi
 * @author Liam Campbell
 */
public class CMoveAttackDef
{
	public int m_iXMv;
	public int m_iYMv;
	
	public boolean m_bOneShot;
	public boolean m_bAttackRule;
	
	/**
	 * Constructor.
	 * @param iXMv - X-axis movement
	 * @param iYMv - Y-axis movement
	 * @param bOneShot - Set to false for queens, bishops, and rooks
	 * @param bAttackRule - Is this an attack rule?
	 */
	public CMoveAttackDef( int iXMv, int iYMv, boolean bOneShot, boolean bAttackRule )
	{
		m_iXMv = iXMv;
		m_iYMv = iYMv;
		m_bOneShot = bOneShot;
		m_bAttackRule = bAttackRule;
	}
}
