package com.davitmartirosyan.exp.pojo;


public class UserResponseDTO {

    private long id;
    private boolean status;

    public UserResponseDTO() {

    }

    public UserResponseDTO(long id, boolean status) {
        this.id = id;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "UserResponseDTO{" +
                "id=" + id +
                ", status=" + status +
                '}';
    }
}
