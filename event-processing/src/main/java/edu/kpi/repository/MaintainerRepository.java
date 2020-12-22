package edu.kpi.repository;

import edu.kpi.model.Maintainer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface MaintainerRepository extends ReactiveCrudRepository<Maintainer, Long> {
}
