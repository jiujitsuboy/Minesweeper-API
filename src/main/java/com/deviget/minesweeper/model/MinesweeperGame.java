package com.deviget.minesweeper.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MinesweeperGame {
  private MinesweeperGameDetails details;
  private BoardCell[][] board;
}
