package org.simpleproject.view;

import org.simpleproject.beans.StringGenerator;
import org.simpleproject.beans.StringOne;
import org.simpleproject.model.SimpleEntity;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Backing bean for SimpleEntity entities.
 * <p>
 * This class provides CRUD functionality for all SimpleEntity entities. It
 * focuses purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt>
 * for state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD
 * framework or custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class SimpleEntityBean implements Serializable {

    private static final long serialVersionUID = 1L;

	/*
     * Support creating and retrieving SimpleEntity entities
	 */

    private Long id;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private SimpleEntity simpleEntity;

    public SimpleEntity getSimpleEntity() {
        return this.simpleEntity;
    }

    public void setSimpleEntity(SimpleEntity simpleEntity) {
        this.simpleEntity = simpleEntity;
    }

    @Inject
    private Conversation conversation;

    @Inject
    private StringGenerator stringSevenGenerator;

    @Inject
    @StringOne
    private StringGenerator stringOneGenerator;

    @PersistenceContext(unitName = "simpleproject-persistence-unit", type = PersistenceContextType.EXTENDED)
    private EntityManager entityManager;

    public String create() {

        this.conversation.begin();
        this.conversation.setTimeout(1800000L);
        return "create?faces-redirect=true";
    }

    public void retrieve() {

        if (FacesContext.getCurrentInstance().isPostback()) {
            return;
        }

        if (this.conversation.isTransient()) {
            this.conversation.begin();
            this.conversation.setTimeout(1800000L);
        }

        if (this.id == null) {
            this.simpleEntity = this.example;
        } else {
            this.simpleEntity = findById(getId());
        }
    }

    public SimpleEntity findById(Long id) {

        return this.entityManager.find(SimpleEntity.class, id);
    }

	/*
	 * Support updating and deleting SimpleEntity entities
	 */

    public String update() {
        this.conversation.end();

        try {
            if (this.id == null) {
                this.simpleEntity.setSimpleString(stringSevenGenerator.generateString());
                this.simpleEntity.setAnotherString(stringOneGenerator.generateString());
                this.entityManager.persist(this.simpleEntity);
                return "search?faces-redirect=true";
            } else {
                this.entityManager.merge(this.simpleEntity);
                return "view?faces-redirect=true&id="
                        + this.simpleEntity.getId();
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(e.getMessage()));
            return null;
        }
    }

    public String delete() {
        this.conversation.end();

        try {
            SimpleEntity deletableEntity = findById(getId());

            this.entityManager.remove(deletableEntity);
            this.entityManager.flush();
            return "search?faces-redirect=true";
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(e.getMessage()));
            return null;
        }
    }

	/*
	 * Support searching SimpleEntity entities with pagination
	 */

    private int page;
    private long count;
    private List<SimpleEntity> pageItems;

    private SimpleEntity example = new SimpleEntity();

    public int getPage() {
        return this.page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return 10;
    }

    public SimpleEntity getExample() {
        return this.example;
    }

    public void setExample(SimpleEntity example) {
        this.example = example;
    }

    public String search() {
        this.page = 0;
        return null;
    }

    public void paginate() {

        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

        // Populate this.count

        CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
        Root<SimpleEntity> root = countCriteria.from(SimpleEntity.class);
        countCriteria = countCriteria.select(builder.count(root)).where(
                getSearchPredicates(root));
        this.count = this.entityManager.createQuery(countCriteria)
                .getSingleResult();

        // Populate this.pageItems

        CriteriaQuery<SimpleEntity> criteria = builder
                .createQuery(SimpleEntity.class);
        root = criteria.from(SimpleEntity.class);
        TypedQuery<SimpleEntity> query = this.entityManager
                .createQuery(criteria.select(root).where(
                        getSearchPredicates(root)));
        query.setFirstResult(this.page * getPageSize()).setMaxResults(
                getPageSize());
        this.pageItems = query.getResultList();
    }

    private Predicate[] getSearchPredicates(Root<SimpleEntity> root) {

        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        List<Predicate> predicatesList = new ArrayList<Predicate>();

        String simpleString = this.example.getSimpleString();
        if (simpleString != null && !"".equals(simpleString)) {
            predicatesList.add(builder.like(
                    builder.lower(root.<String>get("simpleString")),
                    '%' + simpleString.toLowerCase() + '%'));
        }
        String name = this.example.getName();
        if (name != null && !"".equals(name)) {
            predicatesList.add(builder.like(
                    builder.lower(root.<String>get("name")),
                    '%' + name.toLowerCase() + '%'));
        }
        String anotherString = this.example.getAnotherString();
        if (anotherString != null && !"".equals(anotherString)) {
            predicatesList.add(builder.like(
                    builder.lower(root.<String>get("anotherString")),
                    '%' + anotherString.toLowerCase() + '%'));
        }

        return predicatesList.toArray(new Predicate[predicatesList.size()]);
    }

    public List<SimpleEntity> getPageItems() {
        return this.pageItems;
    }

    public long getCount() {
        return this.count;
    }

	/*
	 * Support listing and POSTing back SimpleEntity entities (e.g. from inside
	 * an HtmlSelectOneMenu)
	 */

    public List<SimpleEntity> getAll() {

        CriteriaQuery<SimpleEntity> criteria = this.entityManager
                .getCriteriaBuilder().createQuery(SimpleEntity.class);
        return this.entityManager.createQuery(
                criteria.select(criteria.from(SimpleEntity.class)))
                .getResultList();
    }

    @Resource
    private SessionContext sessionContext;

    public Converter getConverter() {

        final SimpleEntityBean ejbProxy = this.sessionContext
                .getBusinessObject(SimpleEntityBean.class);

        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context,
                                      UIComponent component, String value) {

                return ejbProxy.findById(Long.valueOf(value));
            }

            @Override
            public String getAsString(FacesContext context,
                                      UIComponent component, Object value) {

                if (value == null) {
                    return "";
                }

                return String.valueOf(((SimpleEntity) value).getId());
            }
        };
    }

	/*
	 * Support adding children to bidirectional, one-to-many tables
	 */

    private SimpleEntity add = new SimpleEntity();

    public SimpleEntity getAdd() {
        return this.add;
    }

    public SimpleEntity getAdded() {
        SimpleEntity added = this.add;
        this.add = new SimpleEntity();
        return added;
    }
}
