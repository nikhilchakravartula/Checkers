import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

class Move
{
	ArrayList<Pair> fromTo;
	char captures[];
	int nCaptures;
	boolean isJump=false;
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		Pair prev = null;
		for(Pair step:fromTo)
		{
			if(prev == null)
			{
				prev = step;
				continue;
			}
			if(isJump)
				sb.append('J');
			else sb.append('E');
			sb.append(' ');
			sb.append(prev.toString());
			sb.append(' ');
			sb.append(step.toString());
			sb.append("\n");
			prev = step;
		}
		
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
	
	Move()
	{
		fromTo = new ArrayList<Pair>();
		captures = new char[50];
		nCaptures = 0;
	}
	
	void add(Pair p)
	{
		fromTo.add(p);
	}
	
	void add(int i,int j)
	{
		fromTo.add(new Pair(i,j));
	}
	
	Move(LinkedList<Pair> moveList,boolean isJump)
	{
		this.isJump = isJump;
		fromTo = new ArrayList<Pair>();
		captures = new char[50];
		for(Pair p:moveList)
		{
			add(p);
		}
	}
	
	void applyMove(State state)
	{
	
		for(int i=1;i<fromTo.size();i++)
		{
			state.board[fromTo.get(i).x]
					[fromTo.get(i).y] = 
					state.board[fromTo.get(i-1).x][fromTo.get(i-1).y];
			state.board[fromTo.get(i-1).x][fromTo.get(i-1).y]  = PieceType.EMPTY;
			if(Math.abs(fromTo.get(i).x - fromTo.get(i-1).x)==2 ||
					Math.abs(fromTo.get(i).y - fromTo.get(i-1).y) ==2   )
			{
				captures[nCaptures++]=state.board[ 
				                                   (fromTo.get(i).x+fromTo.get(i-1).x )/2
				                                 ]
				                                [
				                                   (fromTo.get(i).y+fromTo.get(i-1).y)/2
				                                ];
				state.board[ 
                            (fromTo.get(i).x+fromTo.get(i-1).x )/2
                          ]
                         [
                            (fromTo.get(i).y+fromTo.get(i-1).y)/2
                         ] = PieceType.EMPTY;
				
				                                		   
			}
			if(state.turn == Turn.BLACKTURN && fromTo.get(fromTo.size()-1).x==Game.BOARDSIZE-1)
			{
				state.board[fromTo.get(fromTo.size()-1).x][fromTo.get(fromTo.size()-1).y] = PieceType.BLACKKING;
			}
			if(state.turn == Turn.WHITETURN && fromTo.get(fromTo.size()-1).x==0)
			{
				state.board[fromTo.get(fromTo.size()-1).x][fromTo.get(fromTo.size()-1).y] = PieceType.WHITEKING;
			}
		}
	}
	void unApplyMove(State state)
	{
		for(char c:captures)
		{
			System.out.print(c+" ");
		}
		
		int temp = nCaptures-1;
		for(int i=fromTo.size()-1;i>=1;i--)
		{

			state.board[fromTo.get(i-1).x][fromTo.get(i-1).y] = 
					state.board[fromTo.get(i).x][fromTo.get(i).y];
			state.board[fromTo.get(i).x][fromTo.get(i).y]  = PieceType.EMPTY;
			if(Math.abs(fromTo.get(i).x-fromTo.get(i-1).x)==2 ||Math.abs(fromTo.get(i).y-fromTo.get(i-1).y) ==2   )
			{
				state.board[(fromTo.get(i).x+fromTo.get(i-1).x )/2
				           ]
				           [
				            (fromTo.get(i).y+fromTo.get(i-1).y)/2
				           ] = captures[temp--];
				
				
				                                		   
			}
		}
		if(state.turn == Turn.BLACKTURN && fromTo.get(fromTo.size()-1).x==Game.BOARDSIZE-1)
		{
			state.board[fromTo.get(0).x][fromTo.get(0).y] = PieceType.BLACK;
		}
		if(state.turn == Turn.WHITETURN && fromTo.get(fromTo.size()-1).x==0)
		{
			state.board[fromTo.get(0).x][fromTo.get(0).y] = PieceType.WHITE;
		}
		
	}
}


class MoveList
{
	ArrayList<Move> moves;
	
	void shuffle()
	{
		Collections.shuffle(moves);
	}
	MoveList()
	{
		moves = new ArrayList<Move>();
	}
	
	Move get(int index)
	{
		return moves.get(index);
	}
	int size()
	{
		return moves.size();
	}
	public String toString()
	{
		StringBuilder sb=new StringBuilder();
		System.out.println(moves.size());
		for(int i=0;i<moves.size();i++)
		{
			sb.append(moves.get(i).toString());
			sb.append("\n");
		}
		return sb.toString();
	}
	
 	boolean jumpMoveHelperAt(State state,
			int i,
			int j,
			LinkedList<Pair> moveList,
			int jumpCount,
			int mode)
	{
		
		int opponentI;
		int opponentJ;
		int jumpI;
		int jumpJ;

		boolean jumpPossible = false;
		

			for(int move=0;move<Game.MOVESX[state.turn].length;move++)
			{
				opponentI = i+Game.MOVESX[state.turn][move];
				opponentJ = j+Game.MOVESY[state.turn][move];
				jumpI = opponentI + Game.MOVESX[state.turn][move];
				jumpJ = opponentJ + Game.MOVESY[state.turn][move];
				if(state.isOpponent(opponentI, opponentJ) && 
						state.isEmpty(jumpI,jumpJ))
				{
					jumpPossible = true;
					moveList.addLast(new Pair(jumpI,jumpJ));

					state.board[jumpI][jumpJ] = state.board[i][j];
					state.board[i][j] = PieceType.EMPTY;
					
					char opponent = state.board[opponentI][opponentJ];
					state.board[opponentI][opponentJ]=PieceType.EMPTY;
					
					
					if(jumpMoveHelperAt(state,jumpI,jumpJ,moveList,jumpCount+1,mode) &&
							mode == Mode.SINGLE)
					{
						return true;
					}
					moveList.removeLast();
					state.board[i][j] = state.board[jumpI][jumpJ];
					state.board[jumpI][jumpJ] =  PieceType.EMPTY;
					state.board[opponentI][opponentJ] = opponent;
					
				}
			}
			
			if(state.isKing(i,j))
			{
				for(int move=0;move<Game.MOVESX[state.turn^1].length;move++)
				{
					opponentI = i+Game.MOVESX[state.turn^1][move];
					opponentJ = j+Game.MOVESY[state.turn^1][move];
					jumpI = opponentI + Game.MOVESX[state.turn^1][move];
					jumpJ = opponentJ + Game.MOVESY[state.turn^1][move];
					if(state.isOpponent(opponentI, opponentJ) && 
							state.isEmpty(jumpI,jumpJ))
					{
						jumpPossible = true;
						moveList.addLast(new Pair(jumpI,jumpJ));

						state.board[jumpI][jumpJ] = state.board[i][j];
						state.board[i][j] =  PieceType.EMPTY;
						
						char opponent = state.board[opponentI][opponentJ];
						state.board[opponentI][opponentJ]=  PieceType.EMPTY;
						
						
						if(jumpMoveHelperAt(state,jumpI,jumpJ,moveList,jumpCount+1,mode) &&
								mode == Mode.SINGLE)
						{
							return true;
						}
						moveList.removeLast();
						state.board[i][j] = state.board[jumpI][jumpJ];
						state.board[jumpI][jumpJ] =  PieceType.EMPTY;
						state.board[opponentI][opponentJ] = opponent;
						
					}
				}
			}

			
			if(jumpCount != 0 && jumpPossible == false)
			{
				moves.add(new Move(moveList,true));
				if(mode == Mode.SINGLE)
				{
					return true;
				}
			}


		return false;
		
	}
	
	
	

	void generateJumpMoveAt(State state, 
			int i, 
			int j,
			int mode
			)
	{
		LinkedList<Pair> moves = new LinkedList<Pair>();
		moves.add(new Pair(i,j));
		jumpMoveHelperAt(state, i, j,moves,0,mode);		
	}
	
	
	
	void generateRegularMoveAt(State state,int i, int j,int mode)
	{
		
		for(int move=0;move<Game.MOVESX[state.turn].length;move++)
		{
			if(state.isEmpty(i+Game.MOVESX[state.turn][move], j+Game.MOVESY[state.turn][move]))
			{
				Move moveObj = new Move();
				moveObj.add(i,j);
				moveObj.add(i+Game.MOVESX[state.turn][move],j+Game.MOVESY[state.turn][move]);
				moveObj.isJump  = false;
				moves.add(moveObj);

				if(mode==Mode.SINGLE)
					return;
			}
		}
		
		if(state.isKing(i,j))
		{
			for(int move=0;move<Game.MOVESX[state.turn^1].length;move++)
			{
				if(state.isEmpty(i+Game.MOVESX[state.turn^1][move], j+Game.MOVESY[state.turn^1][move]))
				{
					Move moveObj = new Move();
					moveObj.add(i,j);
					moveObj.add(i+Game.MOVESX[state.turn^1][move],j+Game.MOVESY[state.turn^1][move]);
					moves.add(moveObj);
					moveObj.isJump  = false;
					if(mode==Mode.SINGLE)
						return;
				}
			}
		}
	}
		
	void generateMoves(State state,int mode)
	{
		
		for(int i=0;i<Game.BOARDSIZE;i++)
		{
			for(int j=0;j<Game.BOARDSIZE;j++)
			{
				if(state.isPlayerPiece(i, j))
				{
					generateJumpMoveAt(state, i, j, mode);
					if(mode==Mode.SINGLE && moves.size()!=0)
					{
						return;
					}
						
				}
			}
		}
		
		if(moves.size()==0)
		{
			for(int i=0;i<Game.BOARDSIZE;i++)
			{
				for(int j=0;j<Game.BOARDSIZE;j++)
				{
					if(state.isPlayerPiece(i, j))
					{
						generateRegularMoveAt(state, i, j, mode);
						if(mode==Mode.SINGLE && moves.size()!=0)
						{
							return;
						}
					}
				}
			}
		}

	}
}





double minimax(State state,
		double alpha, 
		double beta,
		int turn,
		int curDepth,
		int maxDepth,
		int max)
{
	
	
	MoveList mvList = new MoveList();
	mvList.generateMoves(state, max);
	
	if(mvList.size() == 0)
	{
		if(turn==state.turn)
		{
			return Integer.MIN_VALUE;
		}
		else return Integer.MAX_VALUE;
	}
	
	if(curDepth==maxDepth)
	{
		return  eval.getEvaluation(state, turn);
	}

	mvList.shuffle();	
	if(max==1)
	{
		double v = -Double.MAX_VALUE;

		for(int i=0;i<mvList.size();i++)
		{
			mvList.get(i).applyMove(state);
			double ret = minimax(state,alpha,beta,turn,curDepth+1,maxDepth,max^1);
			mvList.get(i).unApplyMove(state);
			
			if(v < ret)
			{
				v = ret;
				if(curDepth==0)
					bestMove = mvList.get(i);
			}

			if(v > alpha)
			{
				alpha = v;
			}
			if(alpha >= beta)
			{
				break;
			}
			
		}
		return v;
		

		
	}
	else
	{
		double v = Double.MAX_VALUE;
		
		for(int i=0;i<mvList.size();i++)
		{
			mvList.get(i).applyMove(state);
			double ret = minimax(state,alpha,beta,turn,curDepth+1,maxDepth,max^1);
			mvList.get(i).unApplyMove(state);
			if(v > ret)
			{
				v = ret;
				if(curDepth==0)
					bestMove=mvList.get(i);
			}

			if(v < beta)
			{
				beta = v;
			}
			if(alpha >= beta)
			{
				break;
			}
			
		}
		return v;
	}
	
	
}


void playNew() throws IOException
{
	

	 minimax(this.currentState,
			-Double.MAX_VALUE, 
			Double.MAX_VALUE,
			this.turn,
			0,
			10,
			1);
	if(bestMove==null)
	{
		return ;
	}
	
	bw.write(bestMove.toString());
	bw.flush();
	bw.close();
	
	
	
	
//	ArrayList<State> nextStates = nextStateGenerator.generateNextStates(currentState, mode,true);
//	bw.write(" "+nextStates.size()+"\n");
//	for(State nextState:nextStates)
//	{
//		//System.out.println(nextState);
//		bw.write(nextState.toString());
//		bw.write("\n");
//	}
//	bw.flush();
}



class SimpleEvaluation extends EvaluationFunction
{

	

	public static int PAWNS = 0;
	public static int KINGS = 1;
	public static int CENTERPAWNS = 2;
	public static int CENTERKINGS = 3;
	public static int HOMEROW = 4;
	public static int NORMSCORE = 5;

	private static SimpleEvaluation nextEvaluation;
	double[] weights ;
	private SimpleEvaluation()
	{
		weights = new double[10];
		weights[PAWNS] = 100;
		weights[KINGS] = 150;
		weights[CENTERPAWNS] = 1;
		weights[CENTERKINGS] = 5;
		weights[HOMEROW] = 3;
//		weights[NORMSCORE] = 250;

	}
	
	public static SimpleEvaluation getInstance()
	{
		
		if(nextEvaluation == null)
		{
			nextEvaluation = new SimpleEvaluation();
		}
		return nextEvaluation;
	}
	
	

	
	
	boolean ifCenter(int i, int j)
	{
		return 	(i==2 && j==3) ||
				(i==2 && j==5) ||
				(i==3 && j==2) ||
				(i==3 && j==4) ||
				(i==4 && j==3) ||
				(i==4 && j==5) ||
				(i==5 && j==2) ||
				(i==5 && j==4);
	}
	
	public double getEvaluation(State state,int turn)
	{
		double[][] values = new double[2][10];
		double evaluationValue  = 0;
		boolean visited[][] = new boolean[Game.BOARDSIZE][Game.BOARDSIZE];
		int home=0;
		double aggregatePositionScore=0;
		
		Pair blackPairSum = new Pair(0, 0);
		Pair whitePairSum = new Pair(0, 0);
		int bcount=0;
		int wcount=0;
		
		for(int i=0;i<Game.BOARDSIZE;i++)
		{
			for(int j=0;j<Game.BOARDSIZE;j++)
			{
				if(!state.isEmpty(i, j))
				{
					//Find pawns and kings of individual players
					if(state.board[i][j]==PieceType.BLACK)
					{
						aggregatePositionScore+=(Game.BOARDSIZE-1-i);
						values[Turn.BLACKTURN][PAWNS] +=1;
						if(ifCenter(i, j))
						{
							values[Turn.BLACKTURN][CENTERPAWNS] +=1;
						}
						blackPairSum.x += i;
						blackPairSum.y+=j;
						bcount++;
					}
					else if(state.board[i][j]==PieceType.BLACKKING)
					{
						aggregatePositionScore+=(Game.BOARDSIZE-1-i);
						values[Turn.BLACKTURN][KINGS] +=1;
						if(ifCenter(i, j))
						{
							values[Turn.BLACKTURN][CENTERKINGS] +=1;
						}
						blackPairSum.x += i;
						blackPairSum.y+=j;
						bcount++;
					}
					else if(state.board[i][j]==PieceType.WHITE)
					{
						aggregatePositionScore-=(Game.BOARDSIZE-1-i);
						values[Turn.WHITETURN][PAWNS] +=1;
						if(ifCenter(i, j))
						{
							values[Turn.WHITETURN][CENTERPAWNS] +=1;
						}
						whitePairSum.x += i;
						whitePairSum.y+=j;
						wcount++;
						
					}
					else if(state.board[i][j]==PieceType.WHITEKING)
					{
						aggregatePositionScore-=(Game.BOARDSIZE-1-i);
						values[Turn.WHITETURN][KINGS] +=1;
						if(ifCenter(i, j))
						{
							values[Turn.BLACKTURN][CENTERKINGS] +=1;
						}
						whitePairSum.x += i;
						whitePairSum.y+=j;
						wcount++;
					}

				}
			}
		}
		
		evaluationValue+= (weights[PAWNS]*values[Turn.BLACKTURN][PAWNS]+
						  weights[KINGS]*values[Turn.BLACKTURN][KINGS])/(weights[PAWNS]+weights[KINGS]);
		evaluationValue-= (weights[PAWNS]*values[Turn.WHITETURN][PAWNS]+
						weights[KINGS]*values[Turn.WHITETURN][KINGS])/(weights[PAWNS]+weights[KINGS]);
		
		double rawValueB = (weights[PAWNS]*values[Turn.BLACKTURN][PAWNS]+
				  weights[KINGS]*values[Turn.BLACKTURN][KINGS]);
		double rawValueW = (weights[PAWNS]*values[Turn.WHITETURN][PAWNS]+
				weights[KINGS]*values[Turn.WHITETURN][KINGS]);
		
//		
//		if(rawValueB > 1.5*rawValueW)
//		{
//			blackPairSum.x = blackPairSum.x/bcount;
//			blackPairSum.y = blackPairSum.y/bcount;
//			whitePairSum.x = whitePairSum.x/wcount;
//			whitePairSum.x = whitePairSum.y/wcount;
//			
//			evaluationValue-= Math.sqrt(
//					(blackPairSum.x-whitePairSum.x)*(blackPairSum.x-whitePairSum.x) +
//					(blackPairSum.y-whitePairSum.y)*(blackPairSum.y-whitePairSum.y));
//		}
//		else if(rawValueB < 1.5*rawValueW)
//		{
//			blackPairSum.x = blackPairSum.x/bcount;
//			blackPairSum.y = blackPairSum.y/bcount;
//			whitePairSum.x = whitePairSum.x/wcount;
//			whitePairSum.x = whitePairSum.y/wcount;
//			
//			evaluationValue+= Math.sqrt(
//					(blackPairSum.x-whitePairSum.x)*(blackPairSum.x-whitePairSum.x) +
//					(blackPairSum.y-whitePairSum.y)*(blackPairSum.y-whitePairSum.y));
//		}
//		double denom = (weights[PAWNS]*values[Turn.BLACKTURN][PAWNS]) +
//				(weights[KINGS]*values[Turn.BLACKTURN][KINGS])+
//				(weights[PAWNS]*values[Turn.WHITETURN][PAWNS])+
//				(weights[KINGS]*values[Turn.WHITETURN][KINGS]);
//		double num = (weights[PAWNS]*values[Turn.BLACKTURN][PAWNS]);
//		num+=(weights[KINGS]*values[Turn.BLACKTURN][KINGS]);
//		num-=(weights[PAWNS]*values[Turn.WHITETURN][PAWNS]);
//		num-=(weights[KINGS]*values[Turn.WHITETURN][KINGS]);
//		evaluationValue+= (weights[NORMSCORE]*(num/denom));
		
		evaluationValue+=(weights[CENTERPAWNS]*values[Turn.BLACKTURN][CENTERPAWNS]);
		evaluationValue+=(weights[CENTERKINGS]*values[Turn.BLACKTURN][CENTERKINGS]);
		evaluationValue-=(weights[CENTERPAWNS]*values[Turn.WHITETURN][CENTERPAWNS]);
		evaluationValue-=(weights[CENTERKINGS]*values[Turn.WHITETURN][CENTERKINGS]);
		
		if(state.turn == Turn.BLACKTURN)
		{
			evaluationValue+=1;
		}
		else
		{
			evaluationValue-=1;
		}
		
		double homeScore = homeStrength(state);
		evaluationValue+= (weights[HOMEROW]*homeScore);
		
		double totalPawns = values[Turn.BLACKTURN][PAWNS]+values[Turn.WHITETURN][PAWNS];
		
//		if (totalPawns >= 16)
//			evaluationValue += -0.5 * aggregatePositionScore;
//		if ((totalPawns <= 15) && (totalPawns >= 10))
//			evaluationValue += -0.5 * aggregatePositionScore;
//		if (totalPawns <= 9)
//			evaluationValue += 1 * aggregatePositionScore;
		
		
//					
//					double totalKings = values[Turn.BLACKTURN][KINGS]+values[Turn.WHITETURN][KINGS];
		return turn==Turn.BLACKTURN?evaluationValue:-evaluationValue;

	
	}

	private double homeStrength(State state) {
		int home = 0;
		double homeStrength = 0;
		
		for(int j=0;j<Game.BOARDSIZE-1;j++)
		{
			if(state.board[0][j]==PieceType.BLACK ||
					state.board[0][j]==PieceType.BLACKKING)
			{
				home+=j;
			}
		}


		if(home == 16)
		{
			homeStrength+=8;
		}
		else if(home==9)
		{
			homeStrength+=9;
		}
		else if(home==10) //bridge
		{
			homeStrength += 7;
		}
		else if(home==8 || home==12) //continuous pieces
		{
			homeStrength+=2;
		}
		else if(home==1 || home==3|| home==5 || home==7)
		{
			homeStrength-=1;
		}
		
		
		home = 0;

		
		for(int j=0;j<Game.BOARDSIZE-1;j++)
		{
			if(state.board[Game.BOARDSIZE-1][j]==PieceType.WHITE ||
					state.board[Game.BOARDSIZE-1][j]==PieceType.WHITEKING)
			{
				home +=(j+1);
			}
			
		}
		
		if(home == 16)
		{
			homeStrength-=8;
		}
		else if(home==9)
		{
			homeStrength-=9;
		}
		else if(home==10) //bridge
		{
			homeStrength -= 7;
		}
		else if(home==8 || home==12) //continuous pieces
		{
			homeStrength-=2;
		}
		else if(home==1 || home==3|| home==5 || home==7)
		{
			homeStrength+=1;
		}
		
		return homeStrength;
		
	}
}




class GitEvaluation extends EvaluationFunction
{
	
	public static int PAWNS = 0;
	public static int KINGS = 1;
	public static int HOMEROW = 2;
	public static int MIDBOX = 3;
	public static int MIDROW = 4;
	public static int VULNERABLE = 5;
	public static int PROTECTED = 6;
	private static GitEvaluation nextEvaluation;
	double[] weights = {5,7.75,4.25,2.5,0.5,-3,3};
	
	private GitEvaluation()
	{


	}
	
	public static GitEvaluation getInstance()
	{
		if(nextEvaluation == null)
		{
			nextEvaluation = new GitEvaluation();
		}
		return nextEvaluation;
	}
	public double getEvaluation(State state,int turn)
	{
		int[] values = new int[weights.length];
		boolean visited[][] = new boolean[Game.BOARDSIZE][Game.BOARDSIZE];
		for(int i=0;i<Game.BOARDSIZE;i++)
		{
			for(int j=0;j<Game.BOARDSIZE;j++)
			{
				if(state.isPlayerPiece(i, j, turn))
				{
					if(state.isKing(i, j,turn))
					{
						values[KINGS]++;
					}
					else values[PAWNS]++;
					
					
					if(i==3 || i==4)
					{
						if(j==2 || j==5)
						{
							values[MIDBOX]++;
						}
						else values[MIDROW]++;
					}
					if(i == state.getHomeRow(turn))
					{
						values[HOMEROW]++;
					}
					
					values[VULNERABLE] += attackCount(state, i, j, turn^1, visited);
					
					
				}
				values[PROTECTED]= values[PAWNS]+values[KINGS]-values[VULNERABLE];
			}
		}
		
		
		//patterns
		double evaluation = 0.0;
		
		for(int i=0;i<weights.length;i++)
		{
			evaluation += (weights[i]*values[i]);
		}
		return evaluation;
		
	}

}




class NextEvaluation extends EvaluationFunction
{
	
	public static int PAWNS = 0;
	public static int KINGS = 1;
	public static int HOMEROW = 2;
	public static int MIDBOX = 3;
	public static int MIDROW = 4;
	public static int VULNERABLE = 5;
	public static int PROTECTED = 6;
	public static int ATTACKING = 7;
	public static int GROUP = 8;
	private static NextEvaluation nextEvaluation;
	double[] weights = {5,8,4,2.5,1,-3,4,5,2} ;
	
	private NextEvaluation()
	{


	}
	
	public static NextEvaluation getInstance()
	{
		if(nextEvaluation == null)
		{
			nextEvaluation = new NextEvaluation();
		}
		return nextEvaluation;
	}
	public double getEvaluation(State state,int turn)
	{
		int[] values = new int[weights.length];
		int[] valuesO = new int[weights.length];
		boolean visited[][] = new boolean[Game.BOARDSIZE][Game.BOARDSIZE];
		double row=0;;

		for(int i=0;i<Game.BOARDSIZE;i++)
		{
			for(int j=0;j<Game.BOARDSIZE;j++)
			{
				if(state.isPlayerPiece(i, j, turn))
				{

					row+=i;
					if(state.isPlayerPiece(i-1, j-1,turn))
					{
						values[GROUP]++;
					}
					if(state.isPlayerPiece(i-1, j+1,turn))
					{
						values[GROUP]++;
					}
					if(state.isPlayerPiece(i+1, j-1,turn))
					{
						values[GROUP]++;
					}
					if(state.isPlayerPiece(i+1, j+1,turn))
					{
						values[GROUP]++;
					}
					
					
					
					
					if(state.isKing(i, j,turn))
					{

						values[KINGS]++;
					}
					else values[PAWNS]++;
					
					
					if(i==3 || i==4)
					{
						if(j==2 || j==5)
						{
					
//					if(i==2 || i==3 || i==4 || i==5)
//					{
//						if(j==2 || j==5 || j==3 || j==5)
//						{
							values[MIDBOX]++;
						}
						else values[MIDROW]++;
					}
					if(i == state.getHomeRow(turn))
					{
						values[HOMEROW]++;
					}
					
					//values[VULNERABLE] += attackCount(state, i, j, turn^1, visited);
					values[ATTACKING] +=attackCount(state, i, j, turn, visited);
					
					
				}
				else
				{
					
					
					
					if(!state.isEmpty(i, j))
					{
						
						if(state.isPlayerPiece(i-1, j-1,turn^1))
						{
							valuesO[GROUP]++;
						}
						if(state.isPlayerPiece(i-1, j+1,turn^1))
						{
							valuesO[GROUP]++;
						}
						if(state.isPlayerPiece(i+1, j-1,turn^1))
						{
							valuesO[GROUP]++;
						}
						if(state.isPlayerPiece(i+1, j+1,turn^1))
						{
							valuesO[GROUP]++;
						}
						
						
						
						
						if(state.isKing(i, j,turn^1))
						{

							valuesO[KINGS]++;
						}
						else valuesO[PAWNS]++;
						
						
						if(i==3 || i==4)
						{
							if(j==2 || j==5)
							{
						
//						if(i==2 || i==3 || i==4 || i==5)
//						{
//							if(j==2 || j==5 || j==3 || j==5)
//							{
								valuesO[MIDBOX]++;
							}
							else valuesO[MIDROW]++;
						}
						if(i == state.getHomeRow(turn^1))
						{
							valuesO[HOMEROW]++;
						}
						
						//valuesO[VULNERABLE] += attackCount(state, i, j, turn, visited);
						valuesO[ATTACKING] +=attackCount(state, i, j, turn^1, visited);
						
						row-=i;
					}

				}
//				values[PROTECTED]= values[PAWNS]+values[KINGS]-values[VULNERABLE];
//				valuesO[PROTECTED]= valuesO[PAWNS]+valuesO[KINGS]-valuesO[VULNERABLE];
			}
		}
		
		
		//patterns
		double evaluation = 0.0;
		if(turn==Turn.BLACKTURN )
		{
//			if(state.isBlack(0, 1) && state.isBlack(1, 0) && state.isBlack(1, 2))
//			{
//				evaluation+=2;
//			}
//			if( (state.isWhite(1, 0) || state.isWhiteKing(1, 0)) && 
//					(state.isBlackKing(0, 1) || state.isBlack(0, 1))) {
//				
//				evaluation+=2;
//				
//			}
//			
		}
		if(turn==Turn.WHITETURN)
		{
//			if(state.isWhite(6, 5) && state.isWhite(6,7) && state.isWhite(7, 6))
//			{
//				evaluation+=2;
//			}
//			if( (state.isWhite(7,6) || state.isWhiteKing(7,6)) && 
//					(state.isBlackKing(6,7) || state.isBlack(6,7))) {
//				
//				evaluation+=2;
//				
//			}
//			
		}
//		
		int pieces = state.noWhites + state.noBlacks; 
//		if(pieces<=16)
//		{
//			weights[GROUP]=5;
//		}
//		
//		if (pieces >= 10)
//		{
//		
//		}
//		else
//		{
//			double materialB = weights[KINGS]*state.noBlackKings + 
//					weights[PAWNS]*state.noBlackKings;
//			double materialW = weights[KINGS]*state.noWhites + 
//					weights[PAWNS]*state.noWhiteKings;
//
//			
//			
//
//
//			
//			
//			if(Turn.BLACKTURN==turn)
//			{
//				
//				weights[ATTACKING]=4+(materialB/(materialB +materialW));
//				weights[PROTECTED]=4-(materialB/(materialB +materialW));
//				weights[VULNERABLE]=-3+(materialB/(materialB +materialW));
//				
//				
//				if(materialB> 3*materialW) {
//					evaluation+=Math.abs(row)/(state.noBlacks+state.noWhites);
//				}
//			}
//			else
//			{
//
//				
//				weights[ATTACKING]=4+(materialW/(materialB+materialW));
//				weights[PROTECTED]=4-(materialW/(materialB+materialW));
//				weights[VULNERABLE]=-3+(materialW/(materialB+materialW));
//				
//				if(3*materialB < materialW) {
//					evaluation+=Math.abs(row)/(state.noBlacks+state.noWhites);
//				}
//				
//
//		}
//		
//	if (totalPawns <= 9)
//		evaluationValue += 1 * aggregatePositionScore;
		for(int i=0;i<weights.length;i++)
		{
			if(i!=ATTACKING) {
			evaluation += (weights[i]*values[i]);
			evaluation -= (weights[i]*valuesO[i]);
			}
			else
			{
				if(turn==state.turn)
				{
					eval+=values[ATTACKING];
				}
				else eval-=valuesO[ATTACKING];
			}
		}
		return evaluation; //-(weights[PAWNS]*(turn==Turn.BLACKTURN?state.noWhites:state.noBlacks));
						//+weights[KINGS]*(turn==Turn.BLACKTURN?state.noWhiteKings:state.noBlackKings));
		
	}

}


class GitEvaluation extends EvaluationFunction
{
	
	public static int PAWNS = 0;
	public static int KINGS = 1;
	public static int HOMEROW = 2;
	public static int MIDBOX = 3;
	public static int MIDROW = 4;
	public static int VULNERABLE = 5;
	public static int PROTECTED = 6;
	private static GitEvaluation nextEvaluation;
	double[] weights = {5,7.75,4.25,2.5,0.5,-3,3};
	
	private GitEvaluation()
	{


	}
	
	public static GitEvaluation getInstance()
	{
		if(nextEvaluation == null)
		{
			nextEvaluation = new GitEvaluation();
		}
		return nextEvaluation;
	}
	public double getEvaluation(State state,int turn)
	{
		int[] values = new int[weights.length];
		int[] valuesO = new int[weights.length];
		boolean visited[][] = new boolean[Game.BOARDSIZE][Game.BOARDSIZE];
		for(int i=0;i<Game.BOARDSIZE;i+=2)
		{
			for(int j=1;j<Game.BOARDSIZE;j+=2)
			{
				if(state.isPlayerPiece(i, j, turn))
				{
					if(state.isKing(i, j,turn))
					{
						values[KINGS]++;
					}
					else values[PAWNS]++;
					
					
					if(i==3 || i==4)
					{
						if(j==2 || j==5)
						{
							values[MIDBOX]++;
						}
						else values[MIDROW]++;
					}
					if(i == state.getHomeRow(turn))
					{
						values[HOMEROW]++;
					}
					
					values[VULNERABLE] += attackCount(state, i, j, turn^1, visited);
					
					
				}
				else if(!state.isEmpty(i, j))
				{
					
					if(state.isKing(i, j,turn^1))
					{
						valuesO[KINGS]++;
					}
					else valuesO[PAWNS]++;
					
					
					if(i==3 || i==4)
					{
						if(j==2 || j==5)
						{
							valuesO[MIDBOX]++;
						}
						else valuesO[MIDROW]++;
					}
					if(i == state.getHomeRow(turn))
					{
						valuesO[HOMEROW]++;
					}
					
					valuesO[VULNERABLE] += attackCount(state, i, j, turn, visited);
					
					
					
				}
			}
		}
		
		
		
		for(int i=1;i<Game.BOARDSIZE;i+=2)
		{
			for(int j=0;j<Game.BOARDSIZE;j+=2)
			{
				if(state.isPlayerPiece(i, j, turn))
				{
					if(state.isKing(i, j,turn))
					{
						values[KINGS]++;
					}
					else values[PAWNS]++;
					
					
					if(i==3 || i==4)
					{
						if(j==2 || j==5)
						{
							values[MIDBOX]++;
						}
						else values[MIDROW]++;
					}
					if(i == state.getHomeRow(turn))
					{
						values[HOMEROW]++;
					}
					
					values[VULNERABLE] += attackCount(state, i, j, turn^1, visited);
					
					
				}
				else if(!state.isEmpty(i, j))
				{
					
					if(state.isKing(i, j,turn^1))
					{
						valuesO[KINGS]++;
					}
					else valuesO[PAWNS]++;
					
					
					if(i==3 || i==4)
					{
						if(j==2 || j==5)
						{
							valuesO[MIDBOX]++;
						}
						else valuesO[MIDROW]++;
					}
					if(i == state.getHomeRow(turn))
					{
						valuesO[HOMEROW]++;
					}
					
					valuesO[VULNERABLE] += attackCount(state, i, j, turn, visited);
					
					
					
				}
			}
		}
		
		
		
		values[PROTECTED]= values[PAWNS]+values[KINGS]-values[VULNERABLE];
		valuesO[PROTECTED]= valuesO[PAWNS]+valuesO[KINGS]-valuesO[VULNERABLE];
		
		//patterns
		double evaluation = 0.0;
		
		for(int i=0;i<weights.length;i++)
		{
			evaluation += (weights[i]*values[i]);
			evaluation -= (weights[i]*valuesO[i]);
		}
		return evaluation;
		
	}

}








