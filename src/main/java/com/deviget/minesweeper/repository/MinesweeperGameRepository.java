package com.deviget.minesweeper.repository;

import com.deviget.minesweeper.entity.MinesweeperGameEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MinesweeperGameRepository extends CrudRepository<MinesweeperGameEntity, UUID> {

  Optional<MinesweeperGameEntity> findByIdAndUserId(UUID gameId, UUID userId);

  List<MinesweeperGameEntity> findAllByUserId(UUID userId);

  @Query(value = "select bc from game g inner join board_cell bc where g.id = :gameId and bc.row = :row and bc.column= :column ", nativeQuery = true)
  MinesweeperGameEntity findCellByGameId(UUID gameId, int row, int column);

}
