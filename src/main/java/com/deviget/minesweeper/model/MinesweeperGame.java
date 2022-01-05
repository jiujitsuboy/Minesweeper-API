package com.deviget.minesweeper.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MinesweeperGame extends RepresentationModel<MinesweeperGame> {
  private MinesweeperGameDetails details;
  private BoardCell[][] board;
}
