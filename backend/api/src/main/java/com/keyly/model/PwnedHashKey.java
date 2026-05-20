package com.keyly.model;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyClass
public class PwnedHashKey {

    @PrimaryKeyColumn(name = "prefix", type = PrimaryKeyType.PARTITIONED)
    private String prefix;

    @PrimaryKeyColumn(name = "sha1", type = PrimaryKeyType.CLUSTERED)
    private String sha1;

}
