package org.example.sjpa;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "student")
public class Stu {
    @Id
    private Integer id;
    private String name;
    private String gender;
    private Integer age;
}
