package edu.cit.lingguahey.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Entity
@Table(name = "monster_entity")
public class MonsterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "monster_id")
    private int monsterId;

    @Column(unique = true)
    private String tagalogName;

    private String englishName;

    private String description;

    @Lob
    @Column(name = "image_data", columnDefinition = "MEDIUMBLOB")
    private byte[] imageData;

    public MonsterEntity(){
        super();
    }

    public MonsterEntity(String tagalogName, String englishName, String description, byte[] imageData) {
        this.tagalogName = tagalogName;
        this.englishName = englishName;
        this.description = description;
        this.imageData = imageData;
    }
    
}
