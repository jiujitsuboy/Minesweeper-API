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
public class MinesweeperGameCreateReq {

  private UUID userId;
  private int numRows;
  private int numColumns;
  private int numBombs;

}
