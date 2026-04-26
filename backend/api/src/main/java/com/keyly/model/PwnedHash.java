package com.keyly.model;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("pwned_hashes")
public class PwnedHash {

    @PrimaryKey
    private PwnedHashKey key;

    @Column("count")
    private Integer count;

}
