package ru.arsenal.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Anton Nesudimov on 06.09.2016.
 */
@Entity
@Table(name = "bot")
public class Bot {
    @ApiModelProperty(value = "bot token", required = true)
    @Getter
    @Setter
    @Id
    @Column(name = "token")
    private String token;

    @ApiModelProperty(value = "bot name", required = true)
    @Getter
    @Setter
    @Column(name = "name")
    private String name;

    @ApiModelProperty(value = "users of bot")
    @Getter
    @Setter
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "bot")
    private List<User> users;

    @ApiModelProperty(value = "id of merchant", required = true)
    @Getter
    @Setter
    @Column(name = "provider_id")
    private String providerId;

//    @Column(name = "bot_commands")
//    String botCommands;
//    @Getter
//    @Setter
//    @Column(name = "arsenal_pay_id")
//    private String arsenalPayId;

    @ApiModelProperty(value = "bot picture")
    @Getter
    @Setter
    @Column(name = "picture")
    private String picture;

    @ApiModelProperty(value = "bot description")
    @Getter
    @Setter
    @Column(name = "description")
    private String description;

    @ApiModelProperty(value = "name of service, represented by bot", required = true)
    @Getter
    @Setter
    @Column(name = "name_of_service")
    private String nameOfService;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bot bot = (Bot) o;

        if (token != null ? !token.equals(bot.token) : bot.token != null) return false;
        if (name != null ? !name.equals(bot.name) : bot.name != null) return false;
        if (users != null ? !users.equals(bot.users) : bot.users != null) return false;
        if (providerId != null ? !providerId.equals(bot.providerId) : bot.providerId != null) return false;
        if (picture != null ? !picture.equals(bot.picture) : bot.picture != null) return false;
        if (description != null ? !description.equals(bot.description) : bot.description != null) return false;
        return nameOfService != null ? nameOfService.equals(bot.nameOfService) : bot.nameOfService == null;

    }

    @Override
    public int hashCode() {
        int result = token != null ? token.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (users != null ? users.hashCode() : 0);
        result = 31 * result + (providerId != null ? providerId.hashCode() : 0);
        result = 31 * result + (picture != null ? picture.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (nameOfService != null ? nameOfService.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Bot{" +
                "token='" + token + '\'' +
                ", name='" + name + '\'' +
//                ", users=" + users +
                ", providerId='" + providerId + '\'' +
                ", picture='" + picture + '\'' +
                ", description='" + description + '\'' +
                ", nameOfService='" + nameOfService + '\'' +
                '}';
    }
}
