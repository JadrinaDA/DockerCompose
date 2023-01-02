package com.emse.spring.faircorp.dao;

import com.emse.spring.faircorp.model.Window;
import com.emse.spring.faircorp.model.WindowStatus;
import org.springframework.data.repository.query.Param;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class WindowDaoCustomImpl implements WindowDaoCustom{

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Window> findRoomOpenWindows(Long id) {
        String jpql = "select w from Window w where w.room.id = :id and w.windowStatus = :status";
        return em.createQuery(jpql, Window.class)
                .setParameter("id", id)
                .setParameter("status", WindowStatus.OPEN)
                .getResultList();
    }

    public void deleteWindows(@Param("id") Long id) {
        String jpql = "delete from Window w where w.room.id=:id";
        em.createQuery(jpql)
                .setParameter("id", id)
                .executeUpdate();
    }

    public List<Window> findbyRoomName(String name){
        String jpql = "select w from Window w where w.room.name =:name";
        return em.createQuery(jpql, Window.class)
                .setParameter("name", name)
                .getResultList();
    }
}
