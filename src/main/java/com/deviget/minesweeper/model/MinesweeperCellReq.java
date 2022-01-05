package com.deviget.minesweeper.model;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MinesweeperCellReq {

  private UUID gameId;
  private UUID userId;
  private int row;
  private int column;
  private boolean flaggedCell;
}
