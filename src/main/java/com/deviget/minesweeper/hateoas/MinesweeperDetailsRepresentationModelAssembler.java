package com.deviget.minesweeper.hateoas;

import com.deviget.minesweeper.controller.MinesweeperController;
import com.deviget.minesweeper.entity.MinesweeperGameEntity;
import com.deviget.minesweeper.model.MinesweeperGameDetails;
import com.deviget.minesweeper.service.MinesweeperService;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class MinesweeperDetailsRepresentationModelAssembler extends
    RepresentationModelAssemblerSupport<MinesweeperGameEntity, MinesweeperGameDetails> {

  private MinesweeperService minesweeperService;
  private MinesweeperGameRepresentationModelAssembler minesweeperGameRepresentationModelAssembler;

  public MinesweeperDetailsRepresentationModelAssembler(MinesweeperService minesweeperService,
      MinesweeperGameRepresentationModelAssembler minesweeperGameRepresentationModelAssembler) {
    super(MinesweeperController.class, MinesweeperGameDetails.class);
    this.minesweeperService = minesweeperService;
    this.minesweeperGameRepresentationModelAssembler = minesweeperGameRepresentationModelAssembler;
  }

  @Override
  public MinesweeperGameDetails toModel(MinesweeperGameEntity entity) {
    MinesweeperGameDetails minesweeperGameDetails = minesweeperGameRepresentationModelAssembler.toModel(entity).getDetails();
    return minesweeperGameDetails;
  }
}
