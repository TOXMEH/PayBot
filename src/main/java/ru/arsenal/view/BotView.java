package ru.arsenal.view;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import ru.arsenal.model.Bot;
import ru.arsenal.model.User;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anton Nesudimov on 08.11.2016.
 */
public class BotView {
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

    @ApiModelProperty(value = "chaIds of users of bot")
    @Getter
    @Setter
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "bot")
    private List<String> users;

    @ApiModelProperty(value = "id of merchant", required = true)
    @Getter
    @Setter
    @Column(name = "provider_id")
    private String providerId;

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

    public BotView(Bot bot) {
        this.token = bot.getToken();
        this.name = bot.getName();
        this.providerId = bot.getProviderId();
        this.picture = bot.getPicture();
        this.description = bot.getDescription();
        this.nameOfService = bot.getNameOfService();
        this.users = new ArrayList<>();
        for (User user : bot.getUsers()) {
            this.users.add(user.getChatId());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BotView botView = (BotView) o;

        if (token != null ? !token.equals(botView.token) : botView.token != null) return false;
        if (name != null ? !name.equals(botView.name) : botView.name != null) return false;
        if (users != null ? !users.equals(botView.users) : botView.users != null) return false;
        if (providerId != null ? !providerId.equals(botView.providerId) : botView.providerId != null) return false;
        if (picture != null ? !picture.equals(botView.picture) : botView.picture != null) return false;
        if (description != null ? !description.equals(botView.description) : botView.description != null) return false;
        return nameOfService != null ? nameOfService.equals(botView.nameOfService) : botView.nameOfService == null;

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
        return "BotView{" +
                "token='" + token + '\'' +
                ", name='" + name + '\'' +
                ", users=" + users +
                ", providerId='" + providerId + '\'' +
                ", picture='" + picture + '\'' +
                ", description='" + description + '\'' +
                ", nameOfService='" + nameOfService + '\'' +
                '}';
    }
}
