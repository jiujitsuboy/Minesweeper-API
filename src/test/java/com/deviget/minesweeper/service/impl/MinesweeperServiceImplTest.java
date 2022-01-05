package com.deviget.minesweeper.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.deviget.minesweeper.entity.MinesweeperGameEntity;
import com.deviget.minesweeper.model.MinesweeperGameDetails;
import com.deviget.minesweeper.service.MinesweeperService;
import java.util.UUID;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


//@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class MinesweeperServiceImplTest {

  @Autowired
  private MinesweeperService minesweeperService;

  @Disabled
  @Test
  public void createBoard3x4(){

    int rows = 3;
    int columns = 4;
    int bombs = 5;

    MinesweeperGameEntity minesweeperGame = minesweeperService.createNewGame(UUID.randomUUID(), rows, columns, bombs);

    assertNotNull(minesweeperGame);
    assertEquals(minesweeperGame.getNumRows(),rows);
    assertEquals(minesweeperGame.getNumColumns(),columns);
    assertEquals(minesweeperGame.getNumBombs(),bombs);
  }

}