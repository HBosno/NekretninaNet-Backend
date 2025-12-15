package com.nekretninanet.backend.model;

import com.fasterxml.jackson.annotation.JsonView;
import com.nekretninanet.backend.view.QueryViews;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "queries") // Preporučujem plural, ranije je bilo "querie"
public class Query {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView({QueryViews.SupportRequestSummary.class, QueryViews.SupportRequestResponseSummary.class})
    private Long id;

    @Column(name = "query_date")
    @JsonView(QueryViews.SupportRequestSummary.class)
    private LocalDate queryDate;
    @JsonView(QueryViews.SupportRequestSummary.class)
    private String question;
    @JsonView({QueryViews.SupportRequestSummary.class, QueryViews.SupportRequestResponseSummary.class})
    private String response;
    @Enumerated(EnumType.STRING)
    @Column(name = "query_type")
    private QueryType queryType;
    @Enumerated(EnumType.STRING)
    @JsonView({QueryViews.SupportRequestSummary.class, QueryViews.SupportRequestResponseSummary.class})
    private QueryStatus status;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    @JsonView(QueryViews.SupportRequestSummary.class)
    private User user;

    @ManyToOne
    @JoinColumn(name = "realEstateId")
    @JsonView(QueryViews.SupportRequestSummary.class)
    private RealEstate realEstate;


    public Query() {
    }

    public Query(LocalDate queryDate, String question, String response, QueryType queryType,
                 QueryStatus status, User user, RealEstate realEstate) {
        this.queryDate = queryDate;
        this.question = question;
        this.response = response;
        this.queryType = queryType;
        this.status = status;
        this.user = user;
        this.realEstate = realEstate;
    }

    // Getteri i setteri
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getQueryDate() { return queryDate; }
    public void setQueryDate(LocalDate queryDate) { this.queryDate = queryDate; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }

    public QueryType getQueryType() { return queryType; }
    public void setQueryType(QueryType queryType) { this.queryType = queryType; }

    public QueryStatus getStatus() { return status; }
    public void setStatus(QueryStatus status) { this.status = status; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public RealEstate getRealEstate() { return realEstate; }
    public void setRealEstate(RealEstate realEstate) { this.realEstate = realEstate; }


    @Override
    public String toString() {
        return "Query{" +
                "id=" + id +
                ", queryDate=" + queryDate +
                ", question='" + question + '\'' +
                ", response='" + response + '\'' +
                ", queryType=" + queryType +
                ", status=" + status +
                ", user=" + user +
                ", realEstate=" + realEstate +
                '}';
    }
}
