package store.wckd.server.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import store.wckd.server.entity.User;

@Repository
public interface UserRepository extends ReactiveSortingRepository<User, Long> {
    public Mono<User> findByUsername(String username);
}
