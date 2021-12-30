package com.deviget.minesweeper.controller;

import com.deviget.minesweeper.model.GameStatus;
import com.deviget.minesweeper.model.MinesweeperGameDetails;
import com.deviget.minesweeper.model.MinesweeperGame;
import com.deviget.minesweeper.model.MinesweeperGameCreateReq;
import com.deviget.minesweeper.model.MinesweeperCellReq;
import com.deviget.minesweeper.service.MinesweeperService;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/game")
public class MinesweeperController {

  private MinesweeperService minesweeperService;

  public MinesweeperController(MinesweeperService minesweeperService){
    this.minesweeperService  = minesweeperService;
  }

  @PostMapping("/")
  public MinesweeperGameDetails createNewGame(@RequestBody(required = true) MinesweeperGameCreateReq minesweeperGameCreateReq){
      return minesweeperService.createNewGame(minesweeperGameCreateReq.getUserId(),minesweeperGameCreateReq.getNumRows(),minesweeperGameCreateReq.getNumColumns(),minesweeperGameCreateReq.getNumBombs());
  }

  @GetMapping("/{userId}/{gameId}")
  public MinesweeperGame getGameForUser(@PathVariable("gameId")UUID gameId, @PathVariable("userId")UUID userId){
    return minesweeperService.getGameForUser(gameId,userId);
  }
  @GetMapping("/{userId}")
  public List<MinesweeperGameDetails> getAllGamesForUser(@PathVariable("userId")UUID userId){
    return minesweeperService.getAllGamesForUser(userId);
  }

  @PatchMapping("/cell")
  public GameStatus openCell(@RequestBody(required = true) MinesweeperCellReq minesweeperCellReq) {
    return minesweeperService.openCell(minesweeperCellReq.getGameId(),minesweeperCellReq.getUserId(), minesweeperCellReq.getRow(),minesweeperCellReq.getColumn());
  }

  @PatchMapping("/cell/flagged")
  public boolean markCell(@RequestBody(required = true) MinesweeperCellReq minesweeperCellReq){
    return minesweeperService.markCell(minesweeperCellReq.getGameId(),minesweeperCellReq.getUserId(), minesweeperCellReq.getRow(),minesweeperCellReq.getColumn(),minesweeperCellReq.isFlaggedCell());
  }

}
