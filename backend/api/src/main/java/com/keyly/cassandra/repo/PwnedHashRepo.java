package com.keyly.cassandra.repo;

import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;
import com.keyly.model.PwnedHash;
import com.keyly.model.PwnedHashKey;

public interface PwnedHashRepo extends CassandraRepository<PwnedHash, PwnedHashKey> {

    List<PwnedHash> findByKeyPrefix(String prefix);

}
