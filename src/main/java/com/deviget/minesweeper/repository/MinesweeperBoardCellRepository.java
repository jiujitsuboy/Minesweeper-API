package com.deviget.minesweeper.repository;

import com.deviget.minesweeper.entity.MinesweeperBoardCellEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MinesweeperBoardCellRepository extends CrudRepository<MinesweeperBoardCellEntity, UUID> {

 Optional<MinesweeperBoardCellEntity> findByGameIdAndRowAndColumn(UUID gameId, int row, int column);

}
