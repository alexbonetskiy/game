package com.game.controller;

import com.game.dto.PlayerCreationDto;
import com.game.dto.PlayerFilterDto;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/rest/players")
public class PlayerController {

    private final PlayerService playerService;


    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping
    public ResponseEntity<List<Player>> getAllPlayers(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "race", required = false) Race race,
            @RequestParam(value = "profession", required = false) Profession profession,
            @RequestParam(value = "after", required = false, defaultValue = "0") Long after,
            @RequestParam(value = "before", required = false, defaultValue = "0") Long before,
            @RequestParam(value = "banned", required = false) Boolean banned,
            @RequestParam(value = "minExperience", required = false, defaultValue = "0") Integer minExperience,
            @RequestParam(value = "maxExperience", required = false, defaultValue = "0") Integer maxExperience,
            @RequestParam(value = "minLevel", required = false, defaultValue = "0") Integer minLevel,
            @RequestParam(value = "maxLevel", required = false, defaultValue = "0") Integer maxLevel,
            @RequestParam(value = "order", required = false, defaultValue = "ID") PlayerOrder order,
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize)
    {
        PlayerFilterDto filter = PlayerFilterDto.createFilter(name, title, race, profession, after, before, banned, minExperience, maxExperience, minLevel, maxLevel, order, pageNumber, pageSize);
        return new ResponseEntity<>(playerService.findAllPlayers(filter).getContent(), HttpStatus.OK);
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getCount(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "race", required = false) Race race,
            @RequestParam(value = "profession", required = false) Profession profession,
            @RequestParam(value = "after", required = false, defaultValue = "0") Long after,
            @RequestParam(value = "before", required = false, defaultValue = "0") Long before,
            @RequestParam(value = "banned", required = false) Boolean banned,
            @RequestParam(value = "minExperience", required = false, defaultValue = "0") Integer minExperience,
            @RequestParam(value = "maxExperience", required = false, defaultValue = "0") Integer maxExperience,
            @RequestParam(value = "minLevel", required = false, defaultValue = "0") Integer minLevel,
            @RequestParam(value = "maxLevel", required = false, defaultValue = "0") Integer maxLevel,
            @RequestParam(value = "order", required = false, defaultValue = "ID") PlayerOrder order,
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize)
    {
        PlayerFilterDto filter = PlayerFilterDto.createFilter(name, title, race, profession, after, before, banned, minExperience, maxExperience, minLevel, maxLevel, order, pageNumber, pageSize);
        return new ResponseEntity<>(playerService.countPlayers(filter), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable Long id) {
    Player player = playerService.findPlayerById(id);
    if (player == null)
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    else return new ResponseEntity<>(player, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Player> createPlayer(@RequestBody PlayerCreationDto playerCreationDto) {
        return new ResponseEntity<>(playerService.createPlayer(playerCreationDto), HttpStatus.OK);
    }

    @PostMapping("/{id}")
    public ResponseEntity<Player> updatePlayer(@PathVariable Long id, @RequestBody PlayerCreationDto playerCreationDto) {
        return new ResponseEntity<>(playerService.updatePlayer(id, playerCreationDto), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public void deletePlayerById(@PathVariable Long id) {
        playerService.deletePlayerById(id);
    }


}
