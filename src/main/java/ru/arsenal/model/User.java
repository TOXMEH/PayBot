package ru.arsenal.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static ru.arsenal.model.states.none;


/**
 * Created by Anton Nesudimov on 24.09.2016.
 */
@Entity
@Table(name = "user")
public class User {
    @ApiModelProperty(value = "chatId of User in Telegram", required = true)
    @Getter
    @Setter
    @Id
    @Column(name = "chat_id")
    private String chatId;

    @ApiModelProperty(value = "Bot, to which User belongs", required = true)
    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "provider_id", nullable = false)
    private Bot bot;

    @ApiModelProperty(value = "User MSISDN")
    @Getter
    @Setter
    @Column(name = "msisdn")
    private String msisdn;

    @ApiModelProperty(value = "User account number")
    @Getter
    @Setter
    @Column(name = "account")
    private String account;
    //    @Getter
//    @Setter
//    @Column(name = "id_in_arsenal_pay")
//    private String idInArsenalPay;
    @ApiModelProperty(value = "state of User, specifying special behavior on next message to Bot from User", required = true)
    @Getter
    @Setter
    @Column(name = "state")
    @Enumerated(EnumType.ORDINAL)
    private states state;

    @ApiModelProperty(value = "if User has card")
    @Getter
    @Setter
    @Column(name = "having_card")
    private boolean havingCard;

    public User() {
        this.state = none;
    }


}
