/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ucitel.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import ucitel.util.HibernateUtil;


/**
 *
 * @author strafeldap
 */
public class SlovickaHelper {

    Session session = null;

    public SlovickaHelper() {
        this.session = HibernateUtil.getSessionFactory().getCurrentSession();
    }

    /*public ArrayList getSlovicka() {
    ArrayList<Slovicka> filmList = null;
    try {
        //org.hibernate.Transaction tx = session.beginTransaction();
        Query q = session.createQuery ("from Slovicka");
        filmList = (ArrayList<Slovicka>) q.list();
    } catch (Exception e) {
        e.printStackTrace();
    }
    return filmList;
    }*/

    public int getCountSlovicka() {
        int count=0;
        try {
            //org.hibernate.Transaction tx = session.beginTransaction();
            //Query q = session.createQuery("select count(*) from Slovicka slovicka group by id");

            Criteria criteria = session.createCriteria(Slovicka.class).setProjection(Projections.rowCount());
            criteria.add(Restrictions.gt("id",100));
            criteria.add(Restrictions.eq("userId", 3));
            count=(Integer) criteria.uniqueResult();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }


    public static void main(String args[]){

    /*List slovicka = new SlovickaHelper().getSlovicka();
    Iterator i = slovicka.iterator();
    while ( i.hasNext()){
        Slovicka slovicko = (Slovicka) i.next();
        //System.out.println(slovicko.getCzech());
    }

    int cislo = new SlovickaHelper().getCountSlovicka();
    System.out.println(cislo);
*/

  }
}
