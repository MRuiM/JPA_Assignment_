package com.example.java92022.demo;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.persistence.*;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class MyJPADemo {
    private DataSource getDataSource() {
        final MysqlDataSource dataSource = new MysqlDataSource();
//        dataSource.setDatabaseName("OrmDemo");
        dataSource.setUser("root");
        dataSource.setPassword("123456");
        dataSource.setUrl("jdbc:postgresql://localhost:3306/store");
        return dataSource;
    }

    private Properties getProperties() {
        final Properties properties = new Properties();
        properties.put( "hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect" );
        properties.put( "hibernate.connection.driver_class", "com.mysql.jdbc.Driver" );
//        properties.put("hibernate.show_sql", "true");
        return properties;
    }

    private EntityManagerFactory entityManagerFactory(DataSource dataSource, Properties hibernateProperties ){
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com/example/java92022/demo");
        em.setJpaVendorAdapter( new HibernateJpaVendorAdapter() );
        em.setJpaProperties( hibernateProperties );
        em.setPersistenceUnitName( "demo-unit" );
        em.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        em.afterPropertiesSet();
        return em.getObject();
    }

    //////////////////////////////

    public static void main(String[] args) {
        MyJPADemo jpaDemo = new MyJPADemo();
        DataSource dataSource = jpaDemo.getDataSource();
        Properties properties = jpaDemo.getProperties();
        EntityManagerFactory entityManagerFactory = jpaDemo.entityManagerFactory(dataSource, properties);
        EntityManager em = entityManagerFactory.createEntityManager();
        PersistenceUnitUtil unitUtil = entityManagerFactory.getPersistenceUnitUtil();

    }
    private static void insertToColor(EntityManager em) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Color c = new Color();
        c.setName("Blue");
        c.setId("7");
//        em.merge(c);
        em.persist(c);
        tx.commit();
    }

    private static void getProductById(EntityManager em) {
        Query query = em.createQuery("select p from Product p left join fetch p.product_colors ts where p.id = ?1");
        query.setParameter(1, "17");
        Product p = (Product)query.getSingleResult();
        System.out.println(p);
    }

    private static void addToJunctionTable1(EntityManager em) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Product p = new Product();
        p.setName("1th pro");
        Color c = new Color();
        //persist c first to get new id
        em.persist(c);
        c.setName("1th col");
        //build connection between c and p
        Product_Color pc = new Product_Color();
        pc.setCol(c);
        pc.setPro(p);
        c.addProduct_color(pc);

        em.persist(p);
        tx.commit();
    }


}

