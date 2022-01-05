package com.deviget.minesweeper.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.deviget.minesweeper.controller.MinesweeperController;
import com.deviget.minesweeper.entity.MinesweeperGameEntity;
import com.deviget.minesweeper.model.MinesweeperGame;
import com.deviget.minesweeper.model.MinesweeperGameDetails;
import com.deviget.minesweeper.model.User;
import com.deviget.minesweeper.service.MinesweeperService;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class MinesweeperGameRepresentationModelAssembler extends
    RepresentationModelAssemblerSupport<MinesweeperGameEntity, MinesweeperGame> {

  private MinesweeperService minesweeperService;
  private UserRepresentationModelAssembler userRepresentationModelAssembler;

  public MinesweeperGameRepresentationModelAssembler(MinesweeperService minesweeperService,
      UserRepresentationModelAssembler userRepresentationModelAssembler) {
    super(MinesweeperController.class, MinesweeperGame.class);
    this.minesweeperService = minesweeperService;
    this.userRepresentationModelAssembler= userRepresentationModelAssembler;
  }

  @Override
  public MinesweeperGame toModel(MinesweeperGameEntity minesweeperGameEntity) {
    User user = userRepresentationModelAssembler.toModel(minesweeperGameEntity.getUser());

    MinesweeperGame minesweeperGame = MinesweeperGameEntity.toModel(minesweeperGameEntity);
    MinesweeperGameDetails minesweeperGameDetails = minesweeperGame.getDetails();
    minesweeperGameDetails.setUser(user);

    minesweeperGameDetails.add(linkTo(methodOn(MinesweeperController.class).getGameForUser(minesweeperGameDetails.getId(),
        minesweeperGameDetails.getUser().getId())).withRel("get-gameForUser"));

    minesweeperGame.add(linkTo(methodOn(MinesweeperController.class).getGameForUser(minesweeperGame.getDetails().getId(),user.getId())).withSelfRel());
    return minesweeperGame;
  }
}
