package com.graphite.competitionplanner.classes;

public class TestPlayerDTO {
    private Integer id;
    private String firstName;

    public TestPlayerDTO(Integer id, String firstName) {
        this.id = id;
        this.firstName = firstName;
    }

    public TestPlayerDTO() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
}
