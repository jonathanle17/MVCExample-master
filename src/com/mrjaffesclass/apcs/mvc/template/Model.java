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
