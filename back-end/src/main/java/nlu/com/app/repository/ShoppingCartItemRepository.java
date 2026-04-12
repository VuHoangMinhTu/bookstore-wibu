package nlu.com.app.repository;

import nlu.com.app.entity.ShoppingCart;
import nlu.com.app.entity.ShoppingCartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ShoppingCartItemRepository extends JpaRepository<ShoppingCartItem, Long> {

    List<ShoppingCartItem> findAllByShoppingCart(ShoppingCart shoppingCart);
}
