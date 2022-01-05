package com.deviget.minesweeper.entity;

import com.deviget.minesweeper.model.BoardCell;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "board_cell")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MinesweeperBoardCellEntity {

  @Id
  @GeneratedValue
  @Column(name="ID", updatable = false, nullable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  private MinesweeperGameEntity game;

  @Column(name = "ROW_ID")
  private int row;
  @Column(name = "COLUMN_ID")
  private int column;
  @Column(name = "CELL_VALUE")
  private int value;
  @Column(name = "IS_FLAGGED")
  private boolean isFlagged;
  @Column(name = "IS_OPENED")
  private boolean isOpened;
  @Column(name = "IS_DETONATED")
  private boolean isDetonated;
}
