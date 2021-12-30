package com.deviget.minesweeper.model;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MinesweeperGameDetails {

  private UUID id;
  private User user;
  private int rows;
  private int columns;
  private int numBombs;
  private int numCellsOpened;
  private Instant startTime;
  private Instant endTime;
  private long durationInSegs;
  private boolean isGameOver;
  private boolean isWon;
}
