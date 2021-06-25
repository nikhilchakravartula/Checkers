# Checkers Game


![Demo](./resources/demo.gif?raw=true "Demo")
## Overview

* In this project, we will play the game of Checkers, the classic strategy board game. It is a version of the game draughts and is also called American checkers or straight checkers. 
* The agent uses MINIMAX with ALPHA-BETA pruning to decide the best possible move, given a board configuration.
## Setup
![Board](./resources/board.png?raw=true "Board")
* It uses an 8x8 checkered gameboard. Each player starts with 12 game pieces placed on the dark squares of the side of the gameboard closest to them, as can be seen in the figure above. The side with the 
darker colored pieces is usually called ‘Black’ and the side with the lighter color is ‘White’. 
* Black opens the game. The pieces move diagonally forward and can capture opponent’s pieces by jumping over them. Whenever a piece reaches the opposite side of the board, it is crowned 
king and gains the ability to move/capture both forward and backward. 
* The first row of squares on each player’s side is usually called the ‘king’s row’. When an opponent’s piece reaches this row, it will be crowned ‘king’. The king can move in 4 directions diagnoally.
* Each player has a set of 12 pieces in a distinct color, which are placed on the 12 closest dark squares on their side of the board
* The game ends when one side wins or a draw condition applies. One side wins when the opponent cannot make a movee game can be found at [ENGLISH DRAUGHTS](https://en.wikipedia.org/wiki/English_draughts)

## Simple move
* Move the piece to any diagonally forward adjacent empty square. (King 
pieces can also move to one of the adjacent backward diagonals.) 
* This move ends this player’s turn, even if the move results in a position 
that makes one or more subsequent jumps possible (see below for jumps). <br>
## A Jump move
![Jump Move](./resources/jumps.png?raw=true "Jump Move")
* One or more jumps, capturing one or more of the opponent’s pieces (Note: If a 
jump is possible at any point in the turn, it is mandatory):
* An adjacent piece of the opponent in any of the allowed diagonal directions, i.e., forward-left and forward-right (and also backward-left and backward-right for king pieces) can be jumped over if there is an empty square on the other side of that piece.
* After the jump, the piece that was jumped over is “captured” (eliminated 
from the game).
* Place the piece in the empty square on the opposite side of the jumped 
piece.
* All jumping moves are compulsory. Every opportunity to jump must be 
taken. In the case where there are different jump sequences available, the 
player may choose which sequence to make, whether it results in the most 
pieces being taken or not. (This means that a player is not allowed to make 
a sequence of one or more jump
## Playing with agents

In this project, our agent is designed to play against another agent or just perform a single move based on the current board configuration

`Single move` <br/> 
* The agent will be given in input.txt a board configuration, a color to play,
and some number of seconds of allowed time to play one move. 
* The agent should return in output.txt the chosen move(s), before the time expires.

`Play against reference agent`
* The agent is also designed to play full games against another agent. There will be a limited total amount of play time available to for the whole game (e.g., 300 seconds).

`Agent vs agent games`
<br>A master game playing agent will do the following 
* Create the initial board setup.
* Assign a player color (black or white) to the agent. The player who gets assigned black will have the first move.
* Then, in sequence, until the game is over:
o The master game playing agent will create an input.txt file which lets the agent 
know the current board configuration, which color the agent should play, and 
how much total play time the agent has left.
* The player agent should read input.txt in the current directory, decide on a move, and create an output.txt file that describes the move (details below) before the timer expires. 
* If the player agent does not return before the time is up, it will be killed and it loses the game.
* The player agent's remaining playing time will be updated by subtracting the time taken by the agent on this move. If time left reaches zero or negative, the agent loses the game.
* The move will be executed by the master game playing agent. This will update 
the game board to a new configuration.
* The master game playing agent will check for a game-over condition. If one occurs, 
the winning agent or a draw will be declared accordingly.
* The master game playing agent will then present the updated board to the 
opposing agent and let that agent make one move (with the same rules as just 
described; the only difference is that the opponent plays the other 
color and has its own time counter)

## Input and output file formats

`Input` <br>
* ![Input sample](./resources/input_sample.png?raw=true "Input sample")
* First line: A string SINGLE or GAME to know whether we are playing a single move
(and can use all of the available time for it) of playing a full game with potentially
many moves (in which case we should strategically decide how to best allocate
the time across moves).
* Second line: A string BLACK or WHITE indicating which color you play. The colors will always be
organized on the board as follows:
(black starts in the top-left corner and white in the bottom-right).
* Third line: A strictly positive floating point number indicating the amount of total play time
remaining for the agent.
* Next 16 lines: Description of the game board, with 16 lines of 16 symbols each:
<br> w for a grid cell occupied by a white regular piece
<br>  W for a grid cell occupied by a white king piece
<br> b for a grid cell occupied by a black regular piece
<br> B for a grid cell occupied by a black king piece
<br> . (a dot) for an empty grid cell
  
`Output`
* ![Notation](./resources/notation.png?raw=true "Notation")
* 1 or more lines: Describing the move(s). 
* There are two possible types of moves:
    * E FROM_POS TO_POS – The agent moves one of the pieces from location FROM_POS
to an adjacent empty location (on the diagonal) TO_POS. FROM_POS and TO_POS will be  represented using the notations explained above, by a lowercase letter from a to h and a 
number from 1 to 8. As explained above, TO_POS should be adjacent to FROM_POS (on 
the diagonal) and should be empty. 

    * J FROM_POS TO_POS – The agent moves one of the pieces from location FROM_POS 
to empty location TO_POS by jumping over a piece in between. Write out one
jump per line in output.txt if the play results in more than one jumps
## Prerequisites

* Java 1.8
## Using the experiment

If you are using an IDE like IntelliJ or Eclipse, use the following steps to run the project
* Point the working directory to Halma-Game
* Main class: Checkers
* JRE: 1.8
* Run the project

If you are running the project on command line, follow these steps:<br/>
* Clone/Download the project to your local machine.
* Navigate to the folder with HalmaGame.java file.
* Run the following commands:
```
javac Checkers.java
```

```
java Checkers
```

## Code Structure / File description

`input.txt`
<br> The master will update input.txt after each move. So, if you would like to watch the game, enable automatic reloading on the input.txt file.

## Contact

If you want to contact me you can reach me at <nchakrav@usc.edu> or <nikhilchakravartula@gmail.com>