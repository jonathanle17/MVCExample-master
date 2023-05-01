package com.mrjaffesclass.apcs.mvc.template;

import com.mrjaffesclass.apcs.messenger.*;

/**
 * The model represents the data that the app uses.
 * @author Roger Jaffe
 * @version 1.0
 */
public class Model implements MessageHandler {

  // Messaging system for the MVC
  private final Messenger mvcMessaging;
  Boolean whoseMove = true;
  Boolean isWinner = false;
  int[][] board = new int[8][8];
  // Model's data variables

  /**
   * Model constructor: Create the data representation of the program
   * @param messages Messaging class instantiated by the Controller for 
   *   local messages between Model, View, and controller
   */
  public Model(Messenger messages) {
    mvcMessaging = messages;
  }
  
  /**
   * Initialize the model here and subscribe to any required messages
   */
  public void init() {
      this.mvcMessaging.subscribe("squareClicked", this);
      this.mvcMessaging.subscribe("newGame", this);
  }
  
  public void setBoard(String mp) {
      int row = Integer.parseInt(mp.substring(0,1));
      int col = Integer.parseInt(mp.substring(1,2));
      board[row][col] = this.whoseMove ? 1 : 2;
      this.mvcMessaging.notify("boardChange", mp);
  }
  
  public void setInitialBoard() {
      board[3][3] = 1;
      this.mvcMessaging.notify("colorChange", "33t");
      board[3][4] = 2;
      this.mvcMessaging.notify("boardChange", "34f");
      board[4][3] = 2;
      this.mvcMessaging.notify("boardChange", "43f");
      board[4][4] = 1;
      this.mvcMessaging.notify("boardChange", "44f");
  }
  
  public void setSquareNeutral(int row, int col) {
      board[row][col] = 0;
      String mp = String.valueOf(row);
      mp += String.valueOf(col);
      this.mvcMessaging.notify("setBlank", mp);
  }
  
  public int countWhiteSquares() {
      int whiteSquares = 0;
      for (int i = 0; i < 8; i++) {
          for (int j = 0; j < 8; j++) {
              if (board[i][j] == 1) {
                  whiteSquares++;
              }
          }
      }
      return whiteSquares;
  }
  
  public int countBlackSquares() {
      int blackSquares = 0;
      for (int i = 0; i < 8; i++) {
          for (int j=0; j < 8; j++) {
              if (board[i][j] == 2) {
                  blackSquares++;
              }
          }
      }
      return blackSquares;
  }
  
  public void resetBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                setSquareNeutral(row, col);
            }
        }
        setInitialBoard();
    }
  
  public String spaceNameTrim(String name) {
        name = name.substring(7);
        return name;
    }

  public String MPStringSetter(String str) {
        String ret = spaceNameTrim(str);
        if (this.whoseMove) {
            ret += "t";
        } else {
            ret += "f";
        }
        return ret;
    }

  public void resetWhoseMove() {
        this.whoseMove = true;
    }
  
  public void resetLabels() {
        this.mvcMessaging.notify("wCount", countWhiteSquares());
        this.mvcMessaging.notify("bCount", countBlackSquares());
        this.mvcMessaging.notify("playerTurn", this.whoseMove);
    }
  
  public int getSquareValue(int row, int col) {
        return this.board[row][col];
    }
  
  public int getDistanceToWall(String dir, int row, int col) {
        if (dir.equals(Constants.NORTH)) {
            return row;

        } else if (dir.equals(Constants.NORTHEAST)) {
            return (7 - col) < row ? (7 - col) : row;

        } else if (dir.equals(Constants.EAST)) {
            return (7 - col);

        } else if (dir.equals(Constants.SOUTHEAST)) {
            return (7 - col) < (7 - row) ? (7 - col) : (7 - row);

        } else if (dir.equals(Constants.SOUTH)) {
            return (7 - row);

        } else if (dir.equals(Constants.SOUTHWEST)) {
            return (col) < (7 - row) ? col : (7 - row);

        } else if (dir.equals(Constants.WEST)) {
            return col;

        } else if (dir.equals(Constants.NORTHWEST)) {
            return row < col ? row : col;
        }

        return -1;
    }
  
   public int getDirectionSquare(String dir, int row, int col, int spaces) {
        if (dir.equals(Constants.NORTH)) {
            return getSquareValue(row - spaces, col);

        } else if (dir.equals(Constants.NORTHEAST)) {
            return getSquareValue(row - spaces, col + spaces);

        } else if (dir.equals(Constants.EAST)) {
            return getSquareValue(row, col + spaces);

        } else if (dir.equals(Constants.SOUTHEAST)) {
            return getSquareValue(row + spaces, col + spaces);

        } else if (dir.equals(Constants.SOUTH)) {
            return getSquareValue(row + spaces, col);

        } else if (dir.equals(Constants.SOUTHWEST)) {
            return getSquareValue(row + spaces, col - spaces);

        } else if (dir.equals(Constants.WEST)) {
            return getSquareValue(row, col - spaces);

        } else if (dir.equals(Constants.NORTHWEST)) {
            return getSquareValue(row - spaces, col - spaces);
        }
        return 10;
    }
   
   public String getDirectionIndexes(String dir, int row, int col, int spaces) {
        String str = "";
        if (dir.equals(Constants.NORTH)) {
            str += row - spaces;
            str += col;
            return str;
        } else if (dir.equals(Constants.NORTHEAST)) {
            str += row - spaces;
            str += col + spaces;
            return str;
        } else if (dir.equals(Constants.EAST)) {
            str += row;
            str += col + spaces;
            return str;
        } else if (dir.equals(Constants.SOUTHEAST)) {
            str += row + spaces;
            str += col + spaces;
            return str;
        } else if (dir.equals(Constants.SOUTH)) {
            str += row + spaces;
            str += col;
            return str;
        } else if (dir.equals(Constants.SOUTHWEST)) {
            str += row + spaces;
            str += col - spaces;
            return str;
        } else if (dir.equals(Constants.WEST)) {
            str += row;
            str += col - spaces;
            return str;
        } else if (dir.equals(Constants.NORTHWEST)) {
            str += row - spaces;
            str += col - spaces;
            return str;
        }
        return "";
    }
   
   public Boolean checkDirection(String dir, int row, int col) {

        int target = (this.whoseMove ? 1 : 2);
        String str = getDirectionSquares(dir, row, col);

        if (str.length() < 1) {
            return false;
        }

        if (Character.getNumericValue(str.charAt(0)) == target || Character.getNumericValue(str.charAt(0)) == 0) {// if char is anything other than opposite color
            return false;
        }

        for (int i = 1; i < str.length(); i++) {
            if (Character.getNumericValue(str.charAt(i)) == target) { // if char is the target, then pattern is right
                return true;
            } else if (Character.getNumericValue(str.charAt(i)) == 0) { // if char is blank, then not good pattern
                return false;
            } // if neither(the opposite color again), then continue along the str
        }

        return false;
    }
   
   public String getDirectionSquares(String dir, int row, int col) {
        String str = "";
        int dis = getDistanceToWall(dir, row, col);
        for (int i = 1; i <= dis; i++) {
            str += getDirectionSquare(dir, row, col, i);
        }
        return str;
    }
   
   public Boolean isLegalMove(String mp) {

        int row = Integer.parseInt(mp.substring(0, 1));
        int col = Integer.parseInt(mp.substring(1, 2));
        if (this.board[row][col] != 0) {// check if target is filled
            return false;
        }
        if (!checkDirection(Constants.NORTH, row, col)
                && !checkDirection(Constants.NORTHEAST, row, col)
                && !checkDirection(Constants.EAST, row, col)
                && !checkDirection(Constants.SOUTHEAST, row, col)
                && !checkDirection(Constants.SOUTH, row, col)
                && !checkDirection(Constants.SOUTHWEST, row, col)
                && !checkDirection(Constants.WEST, row, col)
                && !checkDirection(Constants.NORTHWEST, row, col)) {
            return false;
        } else {
            return true;
        }
    }
   
   public void flipSquare(int row, int col) {
        int square = getSquareValue(row, col);
        int newColor = 0;

        if (square == 1) {
            newColor = 2;
        } else if (square == 2) {
            newColor = 1;
        }

        String mp = String.valueOf(row);
        mp += col;
        if (newColor == 1) {
            mp += "t";
        } else if (newColor == 2) {
            mp += "f";
        }
        this.board[row][col] = newColor;

        this.mvcMessaging.notify("boardChange", mp);
    }
   
    public int getInverseOfValue(int val) {
        if (val == 1) {
            return 2;
        } else if (val == 2) {
            return 1;
        }
        return 0;
    }
    
    public int getLastIndexOfSquaresToFlip(String str, int target) {

        for (int i = 1; i < str.length(); i++) {
            if (Character.getNumericValue(str.charAt(i)) != (getInverseOfValue(target))) {
                return i - 1;
            }
        }

        return -1;
    }
    
     public void changeSurroundedSquares(String mp) {
        int row = Integer.parseInt(mp.substring(0, 1));
        int col = Integer.parseInt(mp.substring(1, 2));

        int targetValue = getSquareValue(row, col);
        int squaresOutToChange;
        int rowToChange;
        int colToChange;

        if (checkDirection(Constants.NORTH, row, col)) {
            squaresOutToChange = 1 + getLastIndexOfSquaresToFlip(getDirectionSquares(Constants.NORTH, row, col), targetValue);
            for (int i = 1; i <= squaresOutToChange; i++) {
                rowToChange = Character.getNumericValue(getDirectionIndexes(Constants.NORTH, row, col, i).charAt(0));
                colToChange = Character.getNumericValue(getDirectionIndexes(Constants.NORTH, row, col, i).charAt(1));
                flipSquare(rowToChange, colToChange);
            }
        }

        if (checkDirection(Constants.NORTHEAST, row, col)) {
            squaresOutToChange = 1 + getLastIndexOfSquaresToFlip(getDirectionSquares(Constants.NORTHEAST, row, col), targetValue);
            for (int i = 1; i <= squaresOutToChange; i++) {
                rowToChange = Character.getNumericValue(getDirectionIndexes(Constants.NORTHEAST, row, col, i).charAt(0));
                colToChange = Character.getNumericValue(getDirectionIndexes(Constants.NORTHEAST, row, col, i).charAt(1));
                flipSquare(rowToChange, colToChange);
            }
        }

        if (checkDirection(Constants.EAST, row, col)) {
            squaresOutToChange = 1 + getLastIndexOfSquaresToFlip(getDirectionSquares(Constants.EAST, row, col), targetValue);
            for (int i = 1; i <= squaresOutToChange; i++) {
                rowToChange = Character.getNumericValue(getDirectionIndexes(Constants.EAST, row, col, i).charAt(0));
                colToChange = Character.getNumericValue(getDirectionIndexes(Constants.EAST, row, col, i).charAt(1));
                flipSquare(rowToChange, colToChange);
            }
        }

        if (checkDirection(Constants.SOUTHEAST, row, col)) {
            squaresOutToChange = 1 + getLastIndexOfSquaresToFlip(getDirectionSquares(Constants.SOUTHEAST, row, col), targetValue);
            for (int i = 1; i <= squaresOutToChange; i++) {
                rowToChange = Character.getNumericValue(getDirectionIndexes(Constants.SOUTHEAST, row, col, i).charAt(0));
                colToChange = Character.getNumericValue(getDirectionIndexes(Constants.SOUTHEAST, row, col, i).charAt(1));
                flipSquare(rowToChange, colToChange);
            }
        }

        if (checkDirection(Constants.SOUTH, row, col)) {
            squaresOutToChange = 1 + getLastIndexOfSquaresToFlip(getDirectionSquares(Constants.SOUTH, row, col), targetValue);
            for (int i = 1; i <= squaresOutToChange; i++) {
                rowToChange = Character.getNumericValue(getDirectionIndexes(Constants.SOUTH, row, col, i).charAt(0));
                colToChange = Character.getNumericValue(getDirectionIndexes(Constants.SOUTH, row, col, i).charAt(1));
                flipSquare(rowToChange, colToChange);
            }
        }

        if (checkDirection(Constants.SOUTHWEST, row, col)) {
            squaresOutToChange = 1 + getLastIndexOfSquaresToFlip(getDirectionSquares(Constants.SOUTHWEST, row, col), targetValue);
            for (int i = 1; i <= squaresOutToChange; i++) {
                rowToChange = Character.getNumericValue(getDirectionIndexes(Constants.SOUTHWEST, row, col, i).charAt(0));
                colToChange = Character.getNumericValue(getDirectionIndexes(Constants.SOUTHWEST, row, col, i).charAt(1));
                flipSquare(rowToChange, colToChange);
            }
        }

        if (checkDirection(Constants.WEST, row, col)) {
            squaresOutToChange = 1 + getLastIndexOfSquaresToFlip(getDirectionSquares(Constants.WEST, row, col), targetValue);
            for (int i = 1; i <= squaresOutToChange; i++) {
                rowToChange = Character.getNumericValue(getDirectionIndexes(Constants.WEST, row, col, i).charAt(0));
                colToChange = Character.getNumericValue(getDirectionIndexes(Constants.WEST, row, col, i).charAt(1));
                flipSquare(rowToChange, colToChange);
            }
        }

        if (checkDirection(Constants.NORTHWEST, row, col)) {
            squaresOutToChange = 1 + getLastIndexOfSquaresToFlip(getDirectionSquares(Constants.NORTHWEST, row, col), targetValue);
            for (int i = 1; i <= squaresOutToChange; i++) {
                rowToChange = Character.getNumericValue(getDirectionIndexes(Constants.NORTHWEST, row, col, i).charAt(0));
                colToChange = Character.getNumericValue(getDirectionIndexes(Constants.NORTHWEST, row, col, i).charAt(1));
                flipSquare(rowToChange, colToChange);
            }
        }
    }
     public boolean allSquaresFilled() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (this.board[i][j] == 0) {
                    return false;
                }
            }
        }
        return true;
    }
     
     public int winner() {
        if (countWhiteSquares() == 0) {
            this.isWinner = true;
            return 2;
        }
        if (countBlackSquares() == 0) {
            this.isWinner = true;
            return 1;
        }
        if (allSquaresFilled()) {
            if (countWhiteSquares() > countBlackSquares()) {
                return 1;
            } else if (countBlackSquares() > countWhiteSquares()) {
                return 2;
            } else {
                return 0;
            }
        }

        return -1;
    }
     
     public String numWinnerToStr(int num) {
        if (num == 1) {
            return "White";
        } else if (num == 2) {
            return "Black";
        } else if (num == 0) {
            return "Draw";
        }
        return "";
    }
     
    

  
  
  
  @Override
  public void messageHandler(String messageName, Object messagePayload) {
    if (messagePayload != null) {
      System.out.println("MSG: received by model: "+messageName+" | "+messagePayload.toString());
    } else {
      System.out.println("MSG: received by model: "+messageName+" | No data sent");
    }
     if (messageName.equals("spaceClicked")) {
        //set message payload to row, col, whosemove
        String MPString = (String)(messagePayload);
        MPString = MPStringSetter(MPString);
        System.out.println(MPString);
     }
  }

  /**
   * Getter function for variable 1
   * @return Value of variable1
   */
  

}
