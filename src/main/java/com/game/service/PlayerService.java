package com.game.service;


import com.game.dto.PlayerCreationDto;
import com.game.dto.PlayerFilterDto;
import com.game.entity.Player;
import com.game.repository.PlayerRepository;
import exception.IdValidationException;
import exception.NoSuchPlayerException;
import exception.InputDataValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PlayerService {

    private final PlayerRepository repository;
    private PlayerFilterDto filter;
    private final Specification<Player> specification = new Specification<Player>() {

        @Override
        public Predicate toPredicate(Root<Player> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getName() != null) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + filter.getName().toLowerCase() + "%"));
            }
            if (filter.getTitle() != null) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + filter.getTitle().toLowerCase() + "%"));
            }
            if (filter.getRace() != null) {
                predicates.add(cb.equal(root.get("race"), filter.getRace()));
            }
            if (filter.getProfession() != null) {
                predicates.add(cb.equal(root.get("profession"), filter.getProfession()));
            }
            Date before = new Date(filter.getBefore());
            Date after = new Date(filter.getAfter());
            if (filter.getAfter() != 0 && filter.getBefore() != 0 && filter.getAfter() < filter.getBefore()) {
                predicates.add(cb.between(root.get("birthday"), after, before));
            } else if (filter.getAfter() != 0 && filter.getBefore() == 0) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("birthday"), after));
            } else if (filter.getAfter() == 0 && filter.getBefore() != 0) {
                predicates.add(cb.lessThanOrEqualTo(root.get("birthday"), before));
            }
            if (filter.getBanned() != null) {
                predicates.add(cb.equal(root.get("banned"), filter.getBanned()));
            }
            if (filter.getMinExperience() < filter.getMaxExperience()) {
                predicates.add(cb.between(root.get("experience"), filter.getMinExperience(), filter.getMaxExperience()));
            } else if (filter.getMinExperience() != 0 && filter.getMaxExperience() == 0) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("experience"), filter.getMinExperience()));
            } else if (filter.getMinExperience() == 0 && filter.getMaxExperience() != 0) {
                predicates.add(cb.lessThanOrEqualTo(root.get("experience"), filter.getMaxExperience()));
            }
            if (filter.getMinLevel() != 0 && filter.getMaxLevel() != 0 && filter.getMinLevel() < filter.getMaxLevel()) {
                predicates.add(cb.between(root.get("level"), filter.getMinLevel(), filter.getMaxLevel()));
            } else if (filter.getMinLevel() != 0 && filter.getMaxLevel() == 0) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("level"), filter.getMinLevel()));
            } else if (filter.getMinLevel() == 0 && filter.getMaxLevel() != 0) {
                predicates.add(cb.lessThanOrEqualTo(root.get("level"), filter.getMaxLevel()));
            }
            switch (filter.getOrder()) {
                case NAME:
                    return cq.where(cb.and(predicates.toArray(new Predicate[0])))
                            .distinct(true).orderBy(cb.asc(root.get("name"))).getRestriction();
                case LEVEL:
                    return cq.where(cb.and(predicates.toArray(new Predicate[0])))
                            .distinct(true).orderBy(cb.asc(root.get("level"))).getRestriction();
                case BIRTHDAY:
                    return cq.where(cb.and(predicates.toArray(new Predicate[0])))
                            .distinct(true).orderBy(cb.asc(root.get("birthday"))).getRestriction();
                case EXPERIENCE:
                    return cq.where(cb.and(predicates.toArray(new Predicate[0])))
                            .distinct(true).orderBy(cb.asc(root.get("experience"))).getRestriction();
                default:
                    return cq.where(cb.and(predicates.toArray(new Predicate[0])))
                            .distinct(true).orderBy(cb.asc(root.get("id"))).getRestriction();
            }
        }

    };

    public PlayerService(PlayerRepository repository) {
        this.repository = repository;
    }

    public void setFilter(PlayerFilterDto filter) {
        this.filter = filter;
    }

    public Page<Player> findAllPlayers (PlayerFilterDto filter) {
        setFilter(filter);
        Pageable pageable = PageRequest.of(filter.getPageNumber(), filter.getPageSize());
        return repository.findAll(specification, pageable);
    }

    public Integer countPlayers(PlayerFilterDto filter) {
        setFilter(filter);
        return  (int) repository.count(specification);
    }

    public Player findPlayerById(Long id){
        if (!isIdValid(id))
            throw new IdValidationException();
        if (!repository.findById(id).isPresent())
            throw new NoSuchPlayerException();
        return repository.findById(id).orElse(null);
    }

    public Player createPlayer(PlayerCreationDto playerCreationDto){
        if (!isPlayerCreationDtoValid(playerCreationDto)) throw new InputDataValidationException();
        Player player = toPlayer(playerCreationDto);
        return repository.save(player);
    }

    public Player updatePlayer(Long id, PlayerCreationDto playerCreationDto) {
        Player player = findPlayerById(id);

        if (playerCreationDto.getName() != null)
            {if (!isNameValid(playerCreationDto))
                throw new InputDataValidationException();
            player.setName(playerCreationDto.getName());}

        if (playerCreationDto.getTitle() != null)
           {if (!isTitleValid(playerCreationDto))
                throw new InputDataValidationException();
            player.setTitle(playerCreationDto.getTitle());}

        if (playerCreationDto.getRace() != null)
            player.setRace(playerCreationDto.getRace());

        if (playerCreationDto.getProfession() != null)
            player.setProfession(playerCreationDto.getProfession());

        if (playerCreationDto.getBirthday() != null)
            {if(!isBirthDayValid(playerCreationDto))
                throw  new InputDataValidationException();
            player.setBirthday(new Date(playerCreationDto.getBirthday()));}

        if (playerCreationDto.getBanned() != null)
            player.setBanned(playerCreationDto.getBanned());

        if (playerCreationDto.getExperience() != null)
           { if (!isExperienceValid(playerCreationDto))
               throw new InputDataValidationException();
            player.setExperience(playerCreationDto.getExperience());
            player.setLevel(calculateLevel(playerCreationDto));
            player.setUntilNextLevel(calculateUntilNextLevel(playerCreationDto, player.getLevel()));}

        return repository.save(player);
    }

    public void deletePlayerById(Long id) {
        Player player = findPlayerById(id);
         repository.delete(player);
    }


    public static Player toPlayer(PlayerCreationDto playerCreationDto) {
        Player player = new Player();
        player.setName(playerCreationDto.getName());
        player.setTitle(playerCreationDto.getTitle());
        player.setRace(playerCreationDto.getRace());
        player.setProfession(playerCreationDto.getProfession());
        player.setBirthday(new Date(playerCreationDto.getBirthday()));
        player.setBanned(playerCreationDto.getBanned());
        player.setExperience(playerCreationDto.getExperience());

        int level = calculateLevel(playerCreationDto);
        player.setLevel(level);
        player.setUntilNextLevel(calculateUntilNextLevel(playerCreationDto, level));
        return player;
    }


    private static Integer calculateLevel(PlayerCreationDto playerCreationDto) {
        return (((int) (Math.sqrt(2500 + 200 * playerCreationDto.getExperience())) - 50) / 100);
    }

    private static Integer calculateUntilNextLevel(PlayerCreationDto playerCreationDto, Integer level) {
        return (50 * (level + 1) * (level + 2) - playerCreationDto.getExperience());
    }

    private static boolean isPlayerCreationDtoValid(PlayerCreationDto playerCreationDto) {
        return isRequiredParametersAvailable(playerCreationDto) && isNameValid(playerCreationDto) && isTitleValid(playerCreationDto) && isBirthDayValid(playerCreationDto) &&
                isExperienceValid(playerCreationDto);

    }

    private static boolean isRequiredParametersAvailable(PlayerCreationDto playerCreationDto) {
        return playerCreationDto.getName() != null && playerCreationDto.getTitle() != null && playerCreationDto.getRace() != null
                && playerCreationDto.getProfession() != null && playerCreationDto.getBirthday() != 0 && playerCreationDto.getExperience() != 0;
    }

    private static boolean isNameValid (PlayerCreationDto playerCreationDto) {
        return playerCreationDto.getName().trim().length() > 0 && playerCreationDto.getName().trim().length() < 30;

    }

    private static boolean isTitleValid(PlayerCreationDto playerCreationDto) {
        return playerCreationDto.getTitle().trim().length() > 0 && playerCreationDto.getTitle().trim().length() < 30;
    }

    private static boolean isBirthDayValid(PlayerCreationDto playerCreationDto) {
        Date date = new Date(playerCreationDto.getBirthday());
        ZoneId timeZone = ZoneId.systemDefault();
        LocalDate getLocalDate = date.toInstant().atZone(timeZone).toLocalDate();
        return getLocalDate.getYear() >= 2000 && getLocalDate.getYear() <= 3000;
    }
    private static boolean isExperienceValid(PlayerCreationDto playerCreationDto) {
        return playerCreationDto.getExperience() >= 0 && playerCreationDto.getExperience() <= 10000000;
    }

    private static boolean isIdValid(Long id) {
        return id > 0 ;
    }


}
