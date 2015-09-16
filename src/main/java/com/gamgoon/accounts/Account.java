package com.gamgoon.accounts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by gamgoon on 2015. 9. 15..
 */
@Entity
@Getter
@Setter
public class Account {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String username;

//    @JsonIgnore
    private String password;

    private String email;

    private String fullName;

    @Temporal(TemporalType.TIMESTAMP)
    private Date joined;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;
}
