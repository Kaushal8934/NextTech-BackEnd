package Repostitory;
import Entity.UserSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginRepostitory extends JpaRepository<UserSummary, Long> {
}
