package edu.cit.lingguahey.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "cosmetic_entity")
public class CosmeticEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cosmetic_id")
    @EqualsAndHashCode.Include
    private int cosmeticId;

    @Column(unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    private Rarity rarity;

    @Lob
    @Column(name = "cosmetic_image", columnDefinition = "MEDIUMBLOB", nullable = true)
    private byte[] cosmeticImage;

    public CosmeticEntity() {
        super();
    }

    public CosmeticEntity(String name, Rarity rarity, byte[] cosmeticImage) {
        this.name = name;
        this.rarity = rarity;
        this.cosmeticImage = cosmeticImage;
    }

}
