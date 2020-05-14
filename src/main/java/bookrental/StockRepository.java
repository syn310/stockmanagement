package bookrental;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface StockRepository extends CrudRepository<Stock, Long>{
    Optional<Stock> findByBookid(String BookId);

}