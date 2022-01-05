package com.deviget.minesweeper.repository;

import com.deviget.minesweeper.entity.MinesweeperGameEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MinesweeperGameRepository extends JpaRepository<MinesweeperGameEntity, UUID> {

  Optional<MinesweeperGameEntity> findByIdAndUserId(UUID gameId, UUID userId);

  Page<MinesweeperGameEntity> findAllByUserId(UUID userId, Pageable pageable);

  //@Query(value = "select bc from game g inner join board_cell bc where g.id = :gameId and bc.row = :row and bc.column= :column ", nativeQuery = true)
  //MinesweeperGameEntity findCellByGameId(UUID gameId, int row, int column);

}
