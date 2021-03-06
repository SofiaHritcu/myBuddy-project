package ro.mybuddy.server.user.repository;

import com.sun.istack.Nullable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ro.mybuddy.server.user.model.ConfirmationToken;

/**
 * JPA Repository that manages retrieval and persistence of ConfirmationTokens in the persistence layer
 * Instances of the repository are created by the Spring Container
 */
@Repository
public interface ConfirmationTokenRepository extends CrudRepository<ConfirmationToken, Long> {
    @Nullable
    ConfirmationToken findByConfirmationToken(String confirmationToken);
}