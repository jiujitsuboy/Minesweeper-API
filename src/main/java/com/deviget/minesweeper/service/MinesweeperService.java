package com.deviget.minesweeper.service;

import com.deviget.minesweeper.model.GameStatus;
import com.deviget.minesweeper.model.MinesweeperGameDetails;
import com.deviget.minesweeper.model.MinesweeperGame;
import java.util.List;
import java.util.UUID;

public interface MinesweeperService {

  MinesweeperGameDetails createNewGame(UUID userId, int rows, int columns, int numBombs);
  MinesweeperGame getGameForUser(UUID gameId, UUID userId);
  List<MinesweeperGameDetails> getAllGamesForUser(UUID userId);
  GameStatus openCell(UUID gameId, UUID userId,int row, int column);
  boolean markCell(UUID gameId, UUID userId,int row, int column, boolean flagCell);
}
