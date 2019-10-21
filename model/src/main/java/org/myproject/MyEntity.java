package org.myproject;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "my_entity")
public class MyEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @NotNull
  @Lob
  @Column(nullable = false, length = 5120, columnDefinition = "BLOB")
  private byte[] bytes;



  public MyEntity() {
    this.bytes = new byte[0];
  }

  public Long getId() {
    return id;
  }

  public byte[] getBytes() { return bytes; }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof MyEntity) {
      MyEntity that = (MyEntity) obj;

      // Same bytes can be encrypted using different keys so do not use them for testing equality
      return this.id != null && that.id != null ? Objects.equals(this.id, that.id) : false;
    }
    return false;
  }
}
