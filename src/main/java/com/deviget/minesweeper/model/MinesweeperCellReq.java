package com.deviget.minesweeper.model;

import java.util.UUID;
import lombok.Data;

@Data
public class MinesweeperCellReq {

  private UUID gameId;
  private UUID userId;
  private int row;
  private int column;
  private boolean flaggedCell;
}
