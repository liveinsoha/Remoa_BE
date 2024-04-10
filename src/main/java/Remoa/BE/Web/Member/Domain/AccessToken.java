package Remoa.BE.Web.Member.Domain;

import jakarta.persistence.*;
import lombok.Builder;

import java.util.Date;

@Entity
@Builder
public class AccessToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(length = 1000)
    private String token;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expirationDate;

    private boolean blacklisted;

    // Getters and setters
}