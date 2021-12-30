package com.deviget.minesweeper.model;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameStatus {
    private UUID gameId;
    private UUID userId;
    private boolean isGameOver;
    private boolean isWon;
    private int cellValue;
    private List<BoardCell> cellsOpened;
}
