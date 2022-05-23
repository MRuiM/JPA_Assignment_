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
        dataSource.setUser("root");
        dataSource.setPassword("123456");
        dataSource.setUrl("jdbc:mysql://localhost:3306/store");
        return dataSource;
    }

    private Properties getProperties() {
        final Properties properties = new Properties();
        properties.put( "hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect" );
        properties.put( "hibernate.connection.driver_class", "com.mysql.jdbc.Driver" );
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

        insertToColor(em); //insert a new color "green"
        insertToProduct(em); //insert a new product "shoes"

        getColorById(em, 2); //get the color "green" by id

        addToJunctionTable1(em); // create new color and product, add to the junction table.

       withoutOrphanRemove(em, 4, 4);









    }
    private static void insertToColor(EntityManager em) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Color c = new Color();
        c.setName("green");
//        c.setId("7");
//        em.merge(c);
        em.persist(c);
        tx.commit();
    }

    private static void insertToProduct(EntityManager em) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Product p = new Product();
        p.setName("shoes");
        em.persist(p);
        tx.commit();
    }

    private static void getColorById(EntityManager em, int id) {
        Query query = em.createQuery("select c from Color c left join fetch c.product_colors ps where c.id = ?1");
        query.setParameter(1, ""+id);
        Color c = (Color)query.getSingleResult();
        System.out.println(c);
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

    private static void withoutOrphanRemove(EntityManager em, int p_id, int c_id) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Query query = em.createQuery("select p from Product p join fetch p.product_colors pc where p.id = ?1");
        query.setParameter(1, "" + p_id);
        Product p = (Product) query.getSingleResult();
        Iterator<Product_Color> itr = p.getProduct_color().iterator();
        while(itr.hasNext()) {
            Product_Color ts = itr.next();
            if(ts.getCol().getId().equals(""+c_id)) {
                itr.remove();
                em.remove(ts);
            }
        }
        tx.commit();
    }
}

