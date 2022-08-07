package com.chiko.studyhooray.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Entity
@Table(name = "persistent_logins")
@Getter
@Setter
public class PersistentLogins {
    @Id
    @Column(length = 64)
    private String series;

    @Column(length = 64)
    @NotNull
    private String username;

    @Column(length = 64)
    private String token;

    @NotNull
    private LocalDateTime lastUsed;
}
