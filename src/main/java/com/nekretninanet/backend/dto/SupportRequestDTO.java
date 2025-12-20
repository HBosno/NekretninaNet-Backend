package com.nekretninanet.backend.dto;

import java.time.LocalDate;

public class SupportRequestDTO {
    private Long id;
    private String question;
    private LocalDate queryDate;
    private String response;
    private String queryType;
    private String status;
    private String username;

    public SupportRequestDTO(Long id, String question, LocalDate queryDate, String response,
                             String queryType, String status, String username) {
        this.id = id;
        this.question = question;
        this.queryDate = queryDate;
        this.response = response;
        this.queryType = queryType;
        this.status = status;
        this.username = username;
    }

    // Getteri i setteri
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public LocalDate getQueryDate() { return queryDate; }
    public void setQueryDate(LocalDate queryDate) { this.queryDate = queryDate; }

    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }

    public String getQueryType() { return queryType; }
    public void setQueryType(String queryType) { this.queryType = queryType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}