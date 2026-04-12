package nlu.com.app.repository;

import nlu.com.app.constant.EGenre;
import nlu.com.app.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
    Optional<Genre> findGenreByName(EGenre eGenre);
}
