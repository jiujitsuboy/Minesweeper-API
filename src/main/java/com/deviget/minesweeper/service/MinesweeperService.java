package com.deviget.minesweeper.service;

import com.deviget.minesweeper.entity.MinesweeperBoardCellEntity;
import com.deviget.minesweeper.entity.MinesweeperGameEntity;
import com.deviget.minesweeper.model.GameStatus;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MinesweeperService {

  MinesweeperGameEntity createNewGame(UUID userId, int rows, int columns, int numBombs);
  MinesweeperGameEntity getGameForUser(UUID gameId, UUID userId);
  Page<MinesweeperGameEntity> getAllGamesForUser(UUID userId, Pageable pageable);
  GameStatus openCell(UUID gameId, UUID userId,int row, int column);
  boolean markCell(UUID gameId, UUID userId,int row, int column, boolean flagCell);
}
