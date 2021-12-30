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
public class BoardCell {
  private UUID id;
  private int row;
  private int column;
  private int value;
  private boolean isFlagged;
  private boolean isOpened;
}
