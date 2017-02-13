package com.brdalsnes.predictor2017;

import java.io.Serializable;

/**
 * Created by Bjornar on 05/08/2015.
 */
public class Event implements Serializable {

    private String name;
    private String image;
    private String statement;
    private String category;
    private Double probability;
    private Long yes;
    private Long no;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getNo() {
        return no;
    }

    public void setNo(Long no) {
        this.no = no;
    }

    public Double getProbability() {
        return probability;
    }

    public void setProbability(Double probability) {
        this.probability = probability;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public Long getYes() {
        return yes;
    }

    public void setYes(Long yes) {
        this.yes = yes;
    }
}