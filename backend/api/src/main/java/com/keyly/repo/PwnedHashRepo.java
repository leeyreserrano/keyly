package com.keyly.repo;

import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import com.keyly.model.PwnedHash;
import com.keyly.model.PwnedHashKey;

@Repository
public interface PwnedHashRepo extends CassandraRepository<PwnedHash, PwnedHashKey> {

    List<PwnedHash> findByKeyPrefix(String prefix);

}
