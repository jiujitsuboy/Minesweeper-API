package com.deviget.minesweeper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableSpringDataWebSupport
public class MinesweeperApplication {

  public static void main(String[] args) {
    SpringApplication.run(MinesweeperApplication.class, args);
  }

}
