package com.graphite.competitionplanner.classes;

import java.time.LocalDate;

public class TestPlayerDTO {

    private Integer id;
    private String firstName;
    private String lastName;
    private TestClubNoAddressDTO club;
    private LocalDate dateOfBirth;

    public TestPlayerDTO(
            Integer id,
            String firstName,
            String lastName,
            TestClubNoAddressDTO club,
            LocalDate dateOfBirth)
    {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.club = club;
        this.dateOfBirth = dateOfBirth;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public TestClubNoAddressDTO getClub() {
        return club;
    }

    public void setClub(TestClubNoAddressDTO club) {
        this.club = club;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
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
