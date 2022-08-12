package com.cobnet.spring.boot.controller.support;

import com.cobnet.interfaces.spring.controller.BodyPart;
import com.cobnet.interfaces.spring.controller.BodyPartKey;
import com.cobnet.interfaces.spring.controller.ExpressionGenerator;
import com.cobnet.interfaces.spring.controller.ExpressionOperator;
import com.cobnet.interfaces.spring.controller.annotations.EntityVariable;
import com.cobnet.spring.boot.controller.support.enums.OrderDirection;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.query.criteria.internal.OrderImpl;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.criteria.*;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Stream;

@Getter
@Setter
public class EntityVariableInfo<T> implements BodyPart, ExpressionGenerator<CriteriaQuery<T>> {

    private Class<T> type;

    private FlushModeType flush;

    private boolean creatable;

    private boolean cacheable;

    private EntityAttributeConditionInfo[] conditions;

    private EntityCollectionInfo info;

    private EntityQueryHintInfo[] hints;

    private String name;

    private boolean required;

    private boolean isolated;

    public EntityVariableInfo(Class<T> type, String name, boolean required, boolean isolated, FlushModeType flush, boolean creatable, boolean cacheable, EntityAttributeConditionInfo[] conditions, EntityCollectionInfo info, EntityQueryHintInfo[] hints) {
        this.type = type;
        this.name = name;
        this.required = required;
        this.isolated = isolated;
        this.flush = flush;
        this.creatable = creatable;
        this.cacheable = cacheable;

        this.conditions = Arrays.stream(conditions).map(condition -> {

            if(condition.getParent() != this) {

                condition.setParent(this);
            }

            return condition;
        }).toArray(EntityAttributeConditionInfo[]::new);

        this.info = info;
        this.hints = hints;
    }

    public EntityVariableInfo(Class<T> type, EntityVariable variable) {

        this(type, variable.name(), variable.required(), variable.isolated(), variable.flush(), variable.creatable(), variable.cacheable(), Arrays.stream(variable.groups()).map(condition -> new EntityAttributeConditionInfo(null, condition)).toArray(EntityAttributeConditionInfo[]::new), new EntityCollectionInfo(variable.collection()), Arrays.stream(variable.hints()).map(EntityQueryHintInfo::new).toArray(EntityQueryHintInfo[]::new));
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isRequired() {
        return this.required;
    }

    @Override
    public boolean isIsolated() {
        return this.isolated;
    }

    @Override
    public BodyPart[] getReferenced() {

        return Stream.concat(Arrays.stream(this.getKeyInfos()).map(key -> {

            if(key.getReferences() != null && key.getReferences().length > 0) {

                BodyPart[] parts = Arrays.stream(key.getReferences()).map(referenced -> {

                    BodyPart part = MethodContextHolder.getContext().getInfos().get(referenced);

                    if (part == null) {

                        throw new NoSuchElementException(String.format("Unable to find referenced argument '%s' for parameter '%s'.", referenced, name));
                    }

                    return part;

                }).toArray(BodyPart[]::new);

                return parts;
            }

            return null;

        }).filter(Objects::nonNull).flatMap(Stream::of), Arrays.stream(this.getInfo().getPagination().getReferences()).map(referenced -> {

            BodyPart part = MethodContextHolder.getContext().getInfos().get(referenced);

            if (part == null) {

                throw new NoSuchElementException(String.format("Unable to find referenced argument '%s' for parameter '%s'.", referenced, name));
            }

            return part;

        })).distinct().toArray(BodyPart[]::new);
    }

    @Override
    public BodyPartKey[] getKeys() {

        return this.getKeyInfos();
    }

    public EntityAttributeInfo[] getAttributeInfos() {

        return getAttributeInfoStream().toArray(EntityAttributeInfo[]::new);
    }

    public Stream<EntityAttributeInfo> getAttributeInfoStream() {

        return Arrays.stream(this.getConditions()).map(EntityAttributeConditionInfo::getAttributes).flatMap(Stream::of);
    }

    public EntityKeyPredicateInfo[] getKeyPredicateInfos() {

        return getKeyPredicateInfoStream().toArray(EntityKeyPredicateInfo[]::new);
    }

    public Stream<EntityKeyPredicateInfo> getKeyPredicateInfoStream() {

        return getAttributeInfoStream().map(EntityAttributeInfo::getPredicates).flatMap(Stream::of);
    }

    public Stream<EntityKeyInfo> getKeyInfoStream() {

        return getKeyPredicateInfoStream().map(EntityKeyPredicateInfo::getKeys).flatMap(Stream::of);
    }

    public EntityKeyInfo[] getKeyInfos() {

        return getKeyInfoStream().toArray(EntityKeyInfo[]::new);
    }

    public void setConditions(EntityAttributeConditionInfo... conditions) {
        this.conditions = conditions;
    }

    public void setHints(EntityQueryHintInfo... hints) {
        this.hints = hints;
    }


    @Override
    public String toString() {
        return "EntityVariableInfo{" +
                "type=" + type +
                ", name='" + name + '\'' +
                ", flush=" + flush +
                ", creatable=" + creatable +
                ", cacheable=" + cacheable +
                ", conditions=" + Arrays.toString(conditions) +
                ", info=" + info +
                ", hints=" + Arrays.toString(hints) +
                ", required=" + required +
                ", isolated=" + isolated +
                '}';
    }

    @Override
    public Expression<?> generate(EntityManager manager, CriteriaBuilder builder, CriteriaQuery<T> query) {

        Root<T> root = (Root<T>) query.from(this.type).alias("entity");

        query.select(root);

        Predicate predicate = this.getPredicate(manager, builder, root, this.getConditions());

        if(predicate != null) {

            query.where(predicate);
        }

        Order[] orders = new Order[this.getInfo().getOrders().length];

        for(int i = 0; i < this.getInfo().getOrders().length; i++) {

            EntityOrderInfo order = this.getInfo().getOrders()[i];

            String[] nodes = order.getAttribute().split("\\.");

            Path<?> attribute = null;

            for(String node : nodes) {

                if(attribute == null) {

                    attribute = root.get(node);
                    continue;
                }

                attribute = attribute.get(node);
            }

            orders[i] = new OrderImpl(attribute, this.getInfo().getOrders()[i].getOrderDirection() == OrderDirection.ASCENDING);
        }

        if(orders.length > 0) {

            query.orderBy(orders);
        }

        query.distinct(this.getInfo().isDistinct());

        //TODO page, query, hash

        return predicate;
    }


    @Override
    public ExpressionOperator getOperator() {
        return null;
    }

    public static final class Builder<T> {

        private Class<T> type;

        private FlushModeType flush;

        private boolean creatable;

        private boolean cacheable;

        private EntityAttributeConditionInfo[] conditions;

        private EntityCollectionInfo info;

        private EntityQueryHintInfo[] infos;

        private String name;

        private boolean required;

        private boolean isolated;

        public Builder<T> setType(Class<T> type) {
            this.type = type;
            return this;
        }

        public Builder<T> setFlush(FlushModeType flush) {
            this.flush = flush;
            return this;
        }

        public Builder<T> setCreatable(boolean creatable) {
            this.creatable = creatable;
            return this;
        }

        public Builder<T> setCacheable(boolean cacheable) {
            this.cacheable = cacheable;
            return this;
        }

        public Builder<T> setConditions(EntityAttributeConditionInfo[] conditions) {
            this.conditions = conditions;
            return this;
        }

        public Builder<T> setInfo(EntityCollectionInfo info) {
            this.info = info;
            return this;
        }

        public Builder<T> setInfos(EntityQueryHintInfo[] infos) {
            this.infos = infos;
            return this;
        }

        public Builder<T> setName(String name) {
            this.name = name;
            return this;
        }

        public Builder<T> setRequired(boolean required) {
            this.required = required;
            return this;
        }

        public Builder<T> setIsolated(boolean isolated) {
            this.isolated = isolated;
            return this;
        }

        public EntityVariableInfo<T> build() {

            return new EntityVariableInfo<>(this.type, this.name, this.required, this.isolated, this.flush, this.creatable, this.cacheable, this.conditions, this.info, this.infos);
        }
    }
}
