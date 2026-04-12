package nlu.com.app.repository;

import nlu.com.app.constant.ECategory;
import nlu.com.app.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByCategoryName(ECategory name);

    Optional<Category> findCategoriesByCategoryName(ECategory eCategory);

    Optional<Category> findByCategoryId(Long categoryId);

    List<Category> findByParentCategory(Category parentCategory);

    List<Category> findByParentCategoryIsNull();

}
