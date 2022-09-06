package com.counsulteer.coolerimdb.repository;

import com.counsulteer.coolerimdb.entity.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {

}
