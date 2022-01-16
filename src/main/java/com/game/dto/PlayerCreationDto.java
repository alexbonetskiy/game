package com.game.dto;

import com.game.entity.Profession;
import com.game.entity.Race;
import org.springframework.stereotype.Component;
import java.util.Objects;

@Component
public class PlayerCreationDto {

    private String name;
    private String title;
    private Race race;
    private Profession profession;
    private Long birthday;
    private Boolean banned;
    private Integer experience;


    private PlayerCreationDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Race getRace() {
        return race;
    }

    public void setRace(Race race) {
        this.race = race;
    }

    public Profession getProfession() {
        return profession;
    }

    public void setProfession(Profession profession) {
        this.profession = profession;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public Long getBirthday() {
        return birthday;
    }

    public void setBirthday(Long birthday) {
        this.birthday = birthday;
    }

    public Boolean getBanned() {
        return banned;
    }

    public void setBanned(Boolean banned) {
        this.banned = banned;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerCreationDto playerCreationDto = (PlayerCreationDto) o;
        return Objects.equals(name, playerCreationDto.name) && Objects.equals(title, playerCreationDto.title) && race == playerCreationDto.race && profession == playerCreationDto.profession && Objects.equals(experience, playerCreationDto.experience) && Objects.equals(birthday, playerCreationDto.birthday) && Objects.equals(banned, playerCreationDto.banned);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, title, race, profession, experience, birthday, banned);
    }


    @Override
    public String toString() {
        return "PlayerCreationDto{" +
                "name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", race=" + race +
                ", profession=" + profession +
                ", birthday=" + birthday +
                ", banned=" + banned +
                ", experience=" + experience +
                '}';
    }
}
