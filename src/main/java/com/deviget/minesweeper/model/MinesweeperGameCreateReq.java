package com.deviget.minesweeper.model;

import java.util.UUID;
import lombok.Data;

@Data
public class MinesweeperGameCreateReq {

  private UUID userId;
  private int numRows;
  private int numColumns;
  private int numBombs;

}
