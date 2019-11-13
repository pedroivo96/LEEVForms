package com.ufpi.leevforms.Model;

public class User {

    private String id;
    private String name;
    private String email;
    private String projects;
    private Boolean isVisible;
    private int userType;
    private Boolean isRegisterFinalized;
    private String idAdvisor;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProjects() {
        return projects;
    }

    public void setProjects(String projects) {
        this.projects = projects;
    }

    public Boolean getVisible() {
        return isVisible;
    }

    public void setVisible(Boolean visible) {
        isVisible = visible;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public Boolean getRegisterFinalized() {
        return isRegisterFinalized;
    }

    public void setRegisterFinalized(Boolean registerFinalized) {
        isRegisterFinalized = registerFinalized;
    }

    public String getIdAdvisor() {
        return idAdvisor;
    }

    public void setIdAdvisor(String idAdvisor) {
        this.idAdvisor = idAdvisor;
    }
}
