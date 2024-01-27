package mate.academy.dao.impl;

import java.util.Optional;
import mate.academy.dao.ShoppingCartDao;
import mate.academy.exception.DataProcessingException;
import mate.academy.lib.Dao;
import mate.academy.model.ShoppingCart;
import mate.academy.model.User;
import mate.academy.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

@Dao
public class ShoppingCartDaoImpl implements ShoppingCartDao {
    @Override
    public ShoppingCart add(ShoppingCart shoppingCart) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.persist(shoppingCart);
            transaction.commit();
            return shoppingCart;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DataProcessingException("Can't insert shopping cart " + shoppingCart, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public Optional<ShoppingCart> getByUser(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<ShoppingCart> getMovieSessionByIdQuery =
                    session.createQuery("FROM ShoppingCart sc "
                        + "LEFT JOIN FETCH sc.tickets t "
                        + "LEFT JOIN FETCH t.movieSession ms "
                        + "LEFT JOIN FETCH ms.movie m "
                        + "LEFT JOIN FETCH ms.cinemaHall ch "
                        + "WHERE sc.user = :user ", ShoppingCart.class);
            getMovieSessionByIdQuery.setParameter("user", user);
            return getMovieSessionByIdQuery.uniqueResultOptional();
        } catch (Exception e) {
            throw new DataProcessingException("Can't get a user by id: " + user, e);
        }
    }

    @Override
    public void update(ShoppingCart shoppingCart) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.merge(shoppingCart);
        } catch (Exception e) {
            throw new DataProcessingException("Can't update shopping cart: " + shoppingCart, e);
        }
    }
}
