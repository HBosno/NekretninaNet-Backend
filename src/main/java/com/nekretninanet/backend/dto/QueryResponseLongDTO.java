package com.nekretninanet.backend.dto;

import java.time.LocalDate;

public class QueryResponseLongDTO {

    private Long id;            // ID upita
    private LocalDate queryDate; // datum upita
    private String question;    // pitanje
    private String response;    // odgovor
    private String queryType;   // tip upita (REAL_ESTATE_QUERY)
    private String status;      // status upita
    private Long userId;        // ID korisnika koji je poslao upit
    private String username;    // username korisnika

    // Konstruktor
    public QueryResponseLongDTO(Long id, LocalDate queryDate, String question, String response,
                              String queryType, String status, Long userId, String username) {
        this.id = id;
        this.queryDate = queryDate;
        this.question = question;
        this.response = response;
        this.queryType = queryType;
        this.status = status;
        this.userId = userId;
        this.username = username;
    }

    // Getteri i setteri
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getQueryDate() {
        return queryDate;
    }

    public void setQueryDate(LocalDate queryDate) {
        this.queryDate = queryDate;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getQueryType() {
        return queryType;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
